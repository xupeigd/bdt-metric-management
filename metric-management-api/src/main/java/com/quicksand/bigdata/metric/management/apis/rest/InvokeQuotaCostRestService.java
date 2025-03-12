package com.quicksand.bigdata.metric.management.apis.rest;

import com.quicksand.bigdata.metric.management.apis.models.QuotaCostModel;
import com.quicksand.bigdata.metric.management.apis.models.QuotaCostPackageModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * InvokeQuotaCostRestService
 *
 * @author page
 * @date 2022/12/14
 */
@FeignClient(
        name = "${vars.name.ms.apis:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.InvokeQuotaCostRestService:}",
        contextId = "InvokeQuotaCostRestService")
public interface InvokeQuotaCostRestService {

    /**
     * 获取所有有效的quota消耗数据
     *
     * @param appId    应用Id
     * @param metricId 指标Id
     * @return instance of QuotaPackageModel
     */
    @GetMapping("/invoke/quota/costs")
    Response<QuotaCostPackageModel> fetchAllQuotaCosts(@RequestParam(value = "appId", required = false) Integer appId,
                                                       @RequestParam(value = "metricId", required = false) Integer metricId);

    /**
     * 同步配额消耗情况
     *
     * @param quotaCostModels 配额消耗数据
     * @return instance of QuotaPackageModel
     */
    @PostMapping("/invoke/quota/costs")
    Response<QuotaCostPackageModel> syncQuotaCosts(@RequestBody List<QuotaCostModel> quotaCostModels);

}
