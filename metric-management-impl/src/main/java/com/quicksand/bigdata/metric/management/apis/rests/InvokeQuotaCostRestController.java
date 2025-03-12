package com.quicksand.bigdata.metric.management.apis.rests;

import com.quicksand.bigdata.metric.management.apis.models.QuotaCostModel;
import com.quicksand.bigdata.metric.management.apis.models.QuotaCostPackageModel;
import com.quicksand.bigdata.metric.management.apis.rest.InvokeQuotaCostRestService;
import com.quicksand.bigdata.metric.management.apis.services.QuotaCostService;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InvokeQuotaCostRestController
 *
 * @author page
 * @date 2022/12/14
 */
@RestController
@Tag(name = "应用配额使用")
public class InvokeQuotaCostRestController
        implements InvokeQuotaCostRestService {

    @Resource
    QuotaCostService quotaCostService;

    /**
     * 获取所有有效的quota消耗数据
     *
     * @param appId    应用Id
     * @param metricId 指标Id
     * @return instance of QuotaPackageModel
     */
    @Operation(description = "获取所有有效的quota消耗数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN') " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication)")
    @Override
    public Response<QuotaCostPackageModel> fetchAllQuotaCosts(@RequestParam(value = "appId", required = false) Integer appId,
                                                              @RequestParam(value = "metricId", required = false) Integer metricId) {
        List<QuotaCostModel> costModels = quotaCostService.fetchCurQuotaCosts(appId, metricId)
                .stream().map(v -> QuotaCostModel.builder()
                        .appId(v.getAppId())
                        .metricId(v.getMetricId())
                        .quota(v.getQuota())
                        .cost(v.getCost())
                        .updateTime(v.getUpdateTime())
                        .build())
                .collect(Collectors.toList());
        String signFlag = DigestUtils.md5DigestAsHex(JsonUtils.toJson(costModels).getBytes(StandardCharsets.UTF_8));
        return Response.ok(QuotaCostPackageModel.builder()
                .appId(appId)
                .metricId(metricId)
                .flag(signFlag)
                .quotas(costModels)
                .build());
    }

    /**
     * 同步配额消耗情况
     *
     * @param quotaCostModels 配额消耗数据
     * @return instance of QuotaPackageModel
     */
    @Operation(description = "同步配额消耗情况")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN') " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication)")
    @Transactional
    @Override
    public Response<QuotaCostPackageModel> syncQuotaCosts(@RequestBody List<QuotaCostModel> quotaCostModels) {
        quotaCostService.syncQuotaCosts(quotaCostModels);
        return fetchAllQuotaCosts(null, null);
    }

}
