package com.quicksand.bigdata.metric.management.apis.services.impls;

import com.quicksand.bigdata.metric.management.apis.dbvos.MetricInvokeStatisicsDBVO;
import com.quicksand.bigdata.metric.management.apis.dbvos.QuotaCostDBVO;
import com.quicksand.bigdata.metric.management.apis.models.QuotaCostModel;
import com.quicksand.bigdata.metric.management.apis.repos.MetricInvokeStatisicsAutoRepo;
import com.quicksand.bigdata.metric.management.apis.repos.QuotaCostAutoRepo;
import com.quicksand.bigdata.metric.management.apis.services.QuotaCostService;
import com.quicksand.bigdata.metric.management.apis.vos.QuotaCostVO;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.vars.concurrents.TraceFuture;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * QuotaCostServiceImpl
 *
 * @author page
 * @date 2022/12/14
 */
@Slf4j
@Service
public class QuotaCostServiceImpl
        implements QuotaCostService {

    @Resource
    QuotaCostAutoRepo quotaCostAutoRepo;
    @Resource
    MetricInvokeStatisicsAutoRepo metricInvokeStatisicsAutoRepo;

    @Override
    public List<QuotaCostVO> fetchCurQuotaCosts(Integer appId, Integer metricId) {
        return ((null == appId || 0 >= appId) && (null == metricId || 0 >= metricId) ? quotaCostAutoRepo.findCurQuotaCosts()
                : (null == appId || 0 >= appId ? quotaCostAutoRepo.findCurQuotaCostsByMetriceIds(Collections.singletonList(metricId))
                : (null == metricId || 0 >= metricId ? quotaCostAutoRepo.findCurQuotaCostsByAppIds(Collections.singletonList(appId))
                : quotaCostAutoRepo.findCurQuotaCosts(Collections.singletonList(appId), Collections.singletonList(metricId))))).stream()
                .map(v -> JsonUtils.transfrom(v, QuotaCostVO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void syncQuotaCosts(List<QuotaCostModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return;
        }
        Date curTime = new Date();
        boolean refreshSync = 2 == models.size() && Objects.equals(models.get(0).getAppId(), models.get(1).getAppId())
                && Objects.equals(models.get(0).getMetricId(), models.get(1).getMetricId());
        Set<Integer> appIds = new HashSet<>();
        Set<Integer> metricIds = new HashSet<>();
        if (refreshSync) {
            QuotaCostModel lastQuotaCost = models.get(0);
            QuotaCostModel newQuotaCost = models.get(1);
            appIds.add(lastQuotaCost.getAppId());
            metricIds.add(lastQuotaCost.getMetricId());
            List<QuotaCostDBVO> curQuotaCosts = quotaCostAutoRepo.findCurQuotaCosts(appIds, metricIds);
            if (CollectionUtils.isEmpty(curQuotaCosts)) {
                curQuotaCosts = new ArrayList<>();
                QuotaCostDBVO dbvo = QuotaCostDBVO.builder()
                        .appId(lastQuotaCost.getAppId())
                        .metricId(lastQuotaCost.getMetricId())
                        .status(DataStatus.ENABLE)
                        .createTime(curTime)
                        .quota(lastQuotaCost.getQuota())
                        .lockDownFlag(lastQuotaCost.getDateFlag())
                        .createUserId(0)
                        .updateUserId(0)
                        .build();
                curQuotaCosts.add(dbvo);
            }
            curQuotaCosts.get(0).setLockDownFlag(lastQuotaCost.getDateFlag());
            curQuotaCosts.get(0).setCost(lastQuotaCost.getCost());
            curQuotaCosts.get(0).setUpdateTime(curTime);
            //创建新的
            QuotaCostDBVO newCostDb = QuotaCostDBVO.builder()
                    .appId(newQuotaCost.getAppId())
                    .metricId(newQuotaCost.getMetricId())
                    .status(DataStatus.ENABLE)
                    .createTime(curTime)
                    .updateTime(curTime)
                    .quota(newQuotaCost.getQuota())
                    .lockDownFlag("")
                    .cost(newQuotaCost.getCost())
                    .createUserId(0)
                    .updateUserId(0)
                    .build();
            curQuotaCosts.add(newCostDb);
            quotaCostAutoRepo.saveAll(curQuotaCosts);
        } else {
            Map<String, QuotaCostModel> appMetric2CostModel = models.stream()
                    .collect(Collectors.toMap(k -> {
                        appIds.add(k.getAppId());
                        metricIds.add(k.getMetricId());
                        return String.format("%d:%d", k.getAppId(), k.getMetricId());
                    }, Function.identity()));
            //查找dbvo
            Map<String, QuotaCostDBVO> appMetric2dbvos = quotaCostAutoRepo.findCurQuotaCosts(appIds, metricIds).stream()
                    .collect(Collectors.toMap(k -> String.format("%d:%d", k.getAppId(), k.getMetricId()), Function.identity()));
            //循序寻找/更新
            List<QuotaCostDBVO> modifyCosts = new ArrayList<>();
            for (Map.Entry<String, QuotaCostModel> entry : appMetric2CostModel.entrySet()) {
                QuotaCostDBVO dbvo = appMetric2dbvos.get(entry.getKey());
                if (null == dbvo) {
                    dbvo = QuotaCostDBVO.builder()
                            .appId(entry.getValue().getAppId())
                            .metricId(entry.getValue().getMetricId())
                            .status(DataStatus.ENABLE)
                            .lockDownFlag("")
                            .createTime(curTime)
                            .createUserId(0)
                            .updateUserId(0)
                            .build();
                }
                dbvo.setUpdateTime(curTime);
                dbvo.setQuota(entry.getValue().getQuota());
                dbvo.setCost(entry.getValue().getCost());
                modifyCosts.add(dbvo);
            }
            quotaCostAutoRepo.saveAll(modifyCosts);
        }
        //记录条陈数据(按日条陈)
        TraceFuture.run(() ->
                models.forEach(v ->
                        Try.run(() -> {
                                    Calendar instance = Calendar.getInstance();
                                    instance.setTime(v.getUpdateTime());
                                    instance.set(Calendar.HOUR, 0);
                                    instance.set(Calendar.MINUTE, 0);
                                    instance.set(Calendar.SECOND, 0);
                                    instance.set(Calendar.MILLISECOND, 0);
                                    String[] flags = v.getDateFlag().split("\\|");
                                    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date parse = Try.of(() -> SDF.parse(flags[1])).getOrNull();
                                    if (null != parse) {
                                        MetricInvokeStatisicsDBVO dbvo = metricInvokeStatisicsAutoRepo.findByAppIdAndMetricIdAndLogDate(v.getAppId(), v.getMetricId(), instance.getTime());
                                        if (null == dbvo || !Objects.equals(parse, dbvo.getDateFlag())) {
                                            dbvo = MetricInvokeStatisicsDBVO.builder()
                                                    .appId(v.getAppId())
                                                    .metricId(v.getMetricId())
                                                    .quota(v.getQuota())
                                                    .curCost(v.getCost())
                                                    .maxQps(v.getMaxQps())
                                                    .logDate(instance.getTime())
                                                    .dayCost(null == v.getDayCost() ? 0L : v.getDayCost())
                                                    .dateFlag(parse)
                                                    .status(DataStatus.ENABLE)
                                                    .createTime(curTime)
                                                    .updateTime(curTime)
                                                    .build();
                                        } else {
                                            dbvo.setCurCost(v.getCost());
                                            dbvo.setMaxQps(null != v.getMaxQps() && v.getMaxQps() > dbvo.getMaxQps() ? v.getMaxQps() : dbvo.getMaxQps());
                                            dbvo.setDayCost(null == v.getDayCost() ? 0L : v.getDayCost());
                                            dbvo.setUpdateTime(curTime);
                                        }
                                        metricInvokeStatisicsAutoRepo.save(dbvo);
                                    }
                                })
                                .onFailure(ex -> log.error("syncQuotaCosts async execute error ! ", ex))));
    }

}
