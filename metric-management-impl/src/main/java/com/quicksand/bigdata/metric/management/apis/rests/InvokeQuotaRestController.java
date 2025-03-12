package com.quicksand.bigdata.metric.management.apis.rests;

import com.quicksand.bigdata.metric.management.apis.dbvos.AppDBVO;
import com.quicksand.bigdata.metric.management.apis.models.QuotaModel;
import com.quicksand.bigdata.metric.management.apis.models.QuotaPackageModel;
import com.quicksand.bigdata.metric.management.apis.rest.InvokeQuotaRestService;
import com.quicksand.bigdata.metric.management.apis.services.AppService;
import com.quicksand.bigdata.metric.management.apis.services.InvokeApplyService;
import com.quicksand.bigdata.metric.management.apis.vos.AppVO;
import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * InvokeQuotaRestController
 *
 * @author page
 * @date 2022/11/22
 */
@Slf4j
@RestController
@Tag(name = "应用配额服务")
public class InvokeQuotaRestController
        implements InvokeQuotaRestService {

    @Resource
    InvokeApplyService invokeApplyService;
    @Resource
    AppService appService;
    @Resource
    MetricDataManager metricDataManager;

    /**
     * 查找app及指标间的配额关系
     *
     * @param appId    应用Id
     * @param metricId 指标Id
     * @return instance of QuotaModel
     */
    @Operation(description = "获取应用对指标的调用额度")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Deprecated
    @PreAuthorize("hasRole('ADMIN') " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication)")
    @Override
    public Response<QuotaModel> findQuotaData(@PathVariable("appId") Integer appId,
                                              @PathVariable("metricId") Integer metricId) {
        AppVO app = appService.findApp(appId);
        if (null == app) {
            return Response.response(HttpStatus.NOT_FOUND, "应用不存在！");
        }
        if (AppDBVO.TYPE_INNER == app.getType()) {
            return Response.ok(QuotaModel.builder()
                    .id(0)
                    .appId(appId)
                    .appType(AppDBVO.TYPE_INNER)
                    .metricId(metricId)
                    .quota(999999999L)
                    .refreshCornExpress("")
                    .exchangeFlag("")
                    .build());
        }
        return invokeApplyService.findAllEffectiveRelasByAppId(appId).stream()
                .filter(v -> Try.of(() -> v.getMetric().getId().equals(metricId))
                        .getOrElse(false))
                .findFirst()
                .map(v -> Response.ok(QuotaModel.builder()
                        .id(v.getId())
                        .appId(v.getAppId())
                        .appType(AppDBVO.TYPE_OUTER)
                        .metricId(v.getMetric().getId())
                        .quota(v.getQuota().getQuota())
                        .refreshCornExpress(v.getQuota().getCronExpress())
                        .exchangeFlag(DigestUtils.md5DigestAsHex(String.format("%d:%d:%d:%d:%s", v.getId(), v.getAppId(),
                                v.getMetric().getId(), v.getQuota().getQuota(), v.getQuota().getCronExpress()).getBytes(StandardCharsets.UTF_8)))
                        .build()))
                .orElseGet(Response::notfound);
    }

    /**
     * 查找所有的app及指标间的配额关系
     *
     * @param appId    应用Id
     * @param metricId 指标Id
     * @return instance of QuotaModel
     */
    @Operation(description = "获取应用对指标的调用额度")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN') " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication)")
    @Override
    public Response<QuotaPackageModel> fetchAllQuotas(@RequestParam(value = "flag", required = false) String flag,
                                                      @RequestParam(value = "appId", required = false) Integer appId,
                                                      @RequestParam(value = "metricId", required = false) Integer metricId) {
        if (null != appId) {
            AppVO app = appService.findApp(appId);
            if (null == app) {
                return Response.response(HttpStatus.NOT_FOUND, "应用不存在！");
            }
            if (AppDBVO.TYPE_INNER == app.getType()) {
                if (null != metricId) {
                    MetricDBVO metricDBVO = metricDataManager.findByMetricId(metricId);
                    if (null == metricDBVO
                            || null == metricDBVO.getPubsub()
                            || !Objects.equals(PubsubStatus.Online, metricDBVO.getPubsub())) {
                        return Response.ok(QuotaPackageModel.builder()
                                .flag("")
                                .appId(appId)
                                .metricId(metricId)
                                .quotas(Collections.emptyList())
                                .build());
                    }
                    return Response.ok(QuotaPackageModel.builder()
                            .flag("")
                            .appId(appId)
                            .metricId(metricId)
                            .quotas(
                                    Collections.singletonList(
                                            QuotaModel.builder()
                                                    .id(0)
                                                    .appId(appId)
                                                    .appType(AppDBVO.TYPE_INNER)
                                                    .metricId(metricId)
                                                    .quota(-1L)
                                                    .refreshCornExpress("")
                                                    .exchangeFlag(String.format("%d:%d", appId, metricId))
                                                    .build()))
                            .build());

                } else {
                    List<QuotaModel> quotaModels = metricDataManager.findByMetricsByPubsubState(PubsubStatus.Online).stream()
                            .map(v -> QuotaModel.builder()
                                    .id(0)
                                    .appId(appId)
                                    .appType(AppDBVO.TYPE_INNER)
                                    .metricId(v.getId())
                                    .quota(-1L)
                                    .refreshCornExpress("")
                                    .exchangeFlag(String.format("%d:%d", appId, v.getId()))
                                    .build())
                            .collect(Collectors.toList());
                    String signFlag = DigestUtils.md5DigestAsHex(JsonUtils.toJsonString(quotaModels).getBytes(StandardCharsets.UTF_8));
                    return Response.ok(QuotaPackageModel.builder()
                            .appId(appId)
                            .metricId(metricId)
                            .flag(signFlag)
                            .quotas(Objects.equals(signFlag, flag) ? null : quotaModels)
                            .build());
                }
            }
        }
        //内部应用不会出现在列表中
        List<QuotaModel> quotaModels = invokeApplyService.findAllEffectiveRelas().stream()
                .filter(v -> null == appId || Objects.equals(appId, v.getAppId()))
                .filter(v -> null == metricId || Try.of(() -> Objects.equals(metricId, v.getMetric().getId())).getOrElse(false))
                .filter(v -> null != v.getMetric())
                .filter(v -> null != v.getQuota())
                .map(v -> QuotaModel.builder()
                        .id(v.getId())
                        .appId(v.getAppId())
                        .appType(AppDBVO.TYPE_OUTER)
                        .metricId(v.getMetric().getId())
                        .quota(v.getQuota().getQuota())
                        .refreshCornExpress(v.getQuota().getCronExpress())
                        .exchangeFlag(DigestUtils.md5DigestAsHex(String.format("%d:%d:%d:%d:%s", v.getId(), v.getAppId(),
                                v.getMetric().getId(), v.getQuota().getQuota(), v.getQuota().getCronExpress()).getBytes(StandardCharsets.UTF_8)))
                        .build())
                .collect(Collectors.toList());
        String signFlag = DigestUtils.md5DigestAsHex(JsonUtils.toJsonString(quotaModels).getBytes(StandardCharsets.UTF_8));
        return Response.ok(QuotaPackageModel.builder()
                .appId(appId)
                .metricId(metricId)
                .flag(signFlag)
                .quotas(Objects.equals(signFlag, flag) ? null : quotaModels)
                .build());
    }

}
