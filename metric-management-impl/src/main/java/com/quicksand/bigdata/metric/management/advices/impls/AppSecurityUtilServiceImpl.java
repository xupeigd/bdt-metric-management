package com.quicksand.bigdata.metric.management.advices.impls;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.quicksand.bigdata.Params;
import com.quicksand.bigdata.metric.management.advices.AppAuthRefreshEvent;
import com.quicksand.bigdata.metric.management.advices.AppSecurityUtilService;
import com.quicksand.bigdata.metric.management.apis.dbvos.AppDBVO;
import com.quicksand.bigdata.metric.management.apis.services.AppService;
import com.quicksand.bigdata.metric.management.apis.services.InvokeApplyService;
import com.quicksand.bigdata.metric.management.apis.vos.AppVO;
import com.quicksand.bigdata.metric.management.apis.vos.RelationOfAppAndMetricVO;
import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import com.quicksand.bigdata.metric.management.job.core.thread.JobTriggerPoolHelper;
import com.quicksand.bigdata.metric.management.job.core.trigger.TriggerTypeEnum;
import com.quicksand.bigdata.metric.management.job.repos.JobInfoAutoRepo;
import com.quicksand.bigdata.vars.concurrents.TraceFuture;
import com.quicksand.bigdata.vars.security.consts.VarsSecurityConsts;
import com.quicksand.bigdata.vars.security.service.AppTokenProvider;
import com.quicksand.bigdata.vars.security.service.VarsSecurityUtilService;
import com.quicksand.bigdata.vars.security.vos.AppRequestVO;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.HyperAttributes;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AppSecurityUtilServiceImpl
 *
 * @author page
 * @date 2022/11/28
 */
@Slf4j
@Service("appSecurityUtilService")
public class AppSecurityUtilServiceImpl
        implements AppSecurityUtilService {

    public static final RelationOfAppAndMetricVO NULL = new RelationOfAppAndMetricVO();

    @Value("${bdt.management.jobs.enable:true}")
    boolean metricJobEnbale;

    @Resource
    VarsSecurityUtilService varsSecurityUtilService;
    @Resource
    AppTokenProvider appTokenProvider;
    @Resource
    InvokeApplyService invokeApplyService;
    @Resource
    AppService appService;
    @Resource
    JobInfoAutoRepo jobInfoAutoRepo;

    private RelationOfAppAndMetricVO scanAndFetch(String mergeKey) {
        String[] keys = mergeKey.split("_");
        int appId = Integer.parseInt(keys[0]);
        int metricId = Integer.parseInt(keys[1]);
        RelationOfAppAndMetricVO hit = null;
        Map<String, RelationOfAppAndMetricVO> effectivedRelas = invokeApplyService.findAllEffectiveRelasByAppId(appId).stream()
                .filter(v -> Objects.equals(PubsubStatus.Online, v.getMetric().getPubsub()))
                .collect(Collectors.toMap(k -> k.getAppId() + "_" + k.getMetric().getId(), Function.identity()));
        if (!CollectionUtils.isEmpty(effectivedRelas)) {
            Optional<RelationOfAppAndMetricVO> first = effectivedRelas.entrySet().stream()
                    .peek(v -> realationsCaches.put(v.getKey(), v.getValue()))
                    .filter(v -> Objects.equals(metricId, v.getValue().getMetric().getId()))
                    .map(Map.Entry::getValue)
                    .findFirst();
            hit = first.orElse(NULL);
        }
        return hit;
    }

    @EventListener
    public void onAppAuthRefresh(AppAuthRefreshEvent event) {
        Integer appId = event.getAppId();
        if (Objects.equals(0, appId)) {
            synchronized (realationsCaches) {
                realationsCaches.invalidateAll();
            }
        } else if (0 < appId) {
            //清理对应的appId
            List<String> hitKeys = realationsCaches.asMap().keySet().stream()
                    .filter(s -> s.startsWith(appId + "_"))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(hitKeys)) {
                synchronized (realationsCaches) {
                    for (String hitKey : hitKeys) {
                        realationsCaches.invalidate(hitKey);
                    }
                }
            }
        }
        //notify stitcher sync quotas
        Try.run(() -> {
                    if (metricJobEnbale) {
                        JobInfo jobInfo = jobInfoAutoRepo.findByExecutorHandler("preloadQuotaConfigs");
                        if (null != jobInfo) {
                            JobTriggerPoolHelper.trigger(jobInfo.getId(), TriggerTypeEnum.MANUAL, -1, null, "", "");
                            log.info("AppAuthRefreshEvent onfire : trigger metric job! id:{}", jobInfo.getId());
                        }
                    }
                })
                .onFailure(ex -> log.error("onAppAuthRefresh trugger metric-jobs fail !", ex));
    }

    @Override
    public boolean validationRequestSignValue(Params... params) {
        StopWatch stopWatch = new StopWatch("appSecurityUtilService-validationRequestSignValue");
        stopWatch.start();
        AppRequestVO req = HyperAttributes.get(VarsSecurityConsts.KEY_HEADER_APP_AUTH, AppRequestVO.class);
        boolean retVal = false;
        if (null != req
                && StringUtils.hasText(req.getSignValue())
                && !CollectionUtils.isEmpty(req.getParams())
                && !Objects.equals(req.getSignKey(), req.getSignValue())) {
            Map<String, Params> name2Params = Arrays.stream(params).collect(Collectors.toMap(Params::getName, Function.identity()));
            for (AppRequestVO.ValuePair param : req.getParams()) {
                if (null != name2Params.get(param.getName())) {
                    param.setValue(name2Params.get(param.getName()).getVal());
                }
            }
            retVal = req.validationSignValue(appTokenProvider.fetchToken(req.getId()));
        }
        stopWatch.stop();
        TraceFuture.run(() -> log.info("PMF:validationRequestSignValue " + stopWatch));
        return retVal;
    }

    /**
     * 内部应用开放所有的鉴权
     *
     * @param userSecurityDetails instance of UserSecurityDetails
     * @return true/false
     */
    private boolean isInnerApp(UserSecurityDetails userSecurityDetails) {
        StopWatch stopWatch = new StopWatch("appSecurityUtilService-isInnerApp");
        stopWatch.start();
        //根据主键查询，性能损耗不大
        AppVO app = appService.findApp(userSecurityDetails.getId());
        boolean retVal = null != app && Objects.equals(app.getType(), AppDBVO.TYPE_INNER);
        stopWatch.stop();
        TraceFuture.run(() -> log.info("PMF:isInnerApp " + stopWatch));
        return retVal;
    }

    /**
     * 一般性的接口鉴权
     *
     * @param authentication 授权信息
     * @return true/false
     */
    public boolean isApp(Authentication authentication) {
        UserSecurityDetails usd = varsSecurityUtilService.resolveUserDetails(authentication);
        return null != usd && Objects.equals(UserSecurityDetails.TYPE_APP, usd.getType());
    }

    /**
     * App级别的权限
     *
     * @param authentication 授权信息
     * @return true/false
     */
    public boolean hasSeniorAuthority(Authentication authentication) {
        return isApp(authentication)
                && isInnerApp(varsSecurityUtilService.resolveUserDetails(authentication));
    }

    /**
     * 是否拥有当前指标的权限
     *
     * @param authentication 授权信息
     * @param metricId       指标Id
     * @return true/false
     */
    public boolean hasMetric(Authentication authentication, int metricId) {
        StopWatch stopWatch = new StopWatch("appSecurityUtilService-hasMetric");
        stopWatch.start();
        boolean retVal = false;
        if (isApp(authentication)) {
            UserSecurityDetails usd = varsSecurityUtilService.resolveUserDetails(authentication);
            retVal = isInnerApp(usd) || Try.of(() -> !Objects.equals(NULL, realationsCaches.get(String.format("%d_%d", usd.getId(), metricId))))
                    .getOrElse(false);
        }
        stopWatch.stop();
        TraceFuture.run(() -> log.info("PMF:hasMetric " + stopWatch));
        return retVal;
    }

    public boolean hasMetrics(Authentication authentication, List<Integer> metricIds) {
        StopWatch stopWatch = new StopWatch("appSecurityUtilService-hasMetrics");
        stopWatch.start();
        boolean retVal = false;
        if (isApp(authentication)) {
            UserSecurityDetails usd = varsSecurityUtilService.resolveUserDetails(authentication);
            retVal = isInnerApp(usd) || Try.of(() -> metricIds.size() == metricIds.stream()
                            .map(v -> String.format("%d_%d", usd.getId(), v))
                            .map(v -> Try.of(() -> realationsCaches.get(v)).getOrElse(NULL))
                            .filter(v -> !Objects.equals(NULL, v))
                            .count())
                    .getOrElse(false);
        }
        stopWatch.stop();
        TraceFuture.run(() -> log.info("PMF:hasMetrics " + stopWatch));
        return retVal;
    }

    /**
     * 是否拥有当前数据集的权限
     *
     * @param authentication 授权信息
     * @param datasetId      数据集Id
     * @return true/false
     */
    public boolean hasDataset(Authentication authentication, int datasetId) {
        StopWatch stopWatch = new StopWatch("appSecurityUtilService-hasMetrics");
        stopWatch.start();
        boolean retVal = false;
        if (isApp(authentication)) {
            UserSecurityDetails usd = varsSecurityUtilService.resolveUserDetails(authentication);
            retVal = isInnerApp(usd) || (Objects.equals(NULL, Try.of(() -> realationsCaches.get(String.format("%d_0", usd.getId()))).getOrElse(NULL))
                    && realationsCaches.asMap().entrySet().stream()
                    .anyMatch(v -> v.getKey().startsWith(usd.getId() + "_") && datasetId == v.getValue().getMetric().getDataset().getId()));
        }
        stopWatch.stop();
        TraceFuture.run(() -> log.info("PMF:hasMetrics " + stopWatch));
        return retVal;
    }

    /**
     * 是否拥有集群的权限
     *
     * @param authentication 授权信息
     * @param clusterId      集群Id
     * @return true/false
     */
    public boolean hasCluster(Authentication authentication, int clusterId) {
        StopWatch stopWatch = new StopWatch("appSecurityUtilService-authentication");
        stopWatch.start();
        boolean retVal = false;
        if (isApp(authentication)) {
            UserSecurityDetails usd = varsSecurityUtilService.resolveUserDetails(authentication);
            retVal = isInnerApp(usd) ||
                    (Objects.equals(NULL, Try.of(() -> realationsCaches.get(String.format("%d_0", usd.getId()))).getOrElse(NULL))
                            && realationsCaches.asMap().entrySet().stream()
                            .anyMatch(v -> v.getKey().startsWith(usd.getId() + "_") && clusterId == v.getValue().getMetric().getDataset().getCluster().getId()));
        }
        stopWatch.stop();
        TraceFuture.run(() -> log.info("PMF:authentication " + stopWatch));
        return retVal;
    }

    /**
     * key  app_metricId
     * value RelationOfAppAndMetricVO
     */
    final LoadingCache<String, RelationOfAppAndMetricVO> realationsCaches = CacheBuilder.newBuilder()
            .maximumSize(1000L)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .refreshAfterWrite(1L, TimeUnit.MINUTES)
            .build(new CacheLoader<String, RelationOfAppAndMetricVO>() {
                @Override
                public RelationOfAppAndMetricVO load(String key) {
                    return scanAndFetch(key);
                }
            });


}
