package com.quicksand.bigdata.metric.management.apis.rest;

import com.quicksand.bigdata.metric.management.apis.models.QuotaModel;
import com.quicksand.bigdata.metric.management.apis.models.QuotaPackageModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * InvokeQuotaRestService
 *
 * @author page
 * @date 2022/11/22
 */
@FeignClient(
        name = "${vars.name.ms.apis:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.InvokeQuotaRestService:}",
        contextId = "InvokeQuotaRestService")
public interface InvokeQuotaRestService {

    /**
     * 获取指标的配额数据
     *
     * @return instance of QuotaModel
     */
    @Deprecated
    @GetMapping("/invoke/{appId}/{metricId}/quota")
    Response<QuotaModel> findQuotaData(@PathVariable("appId") Integer appId,
                                       @PathVariable("metricId") Integer metricId);

    /**
     * 获取所有有效的quotas数据
     *
     * @return list of QuotaModel
     */
    @GetMapping("/invoke/quotas")
    Response<QuotaPackageModel> fetchAllQuotas(@RequestParam(value = "flag", required = false) String flag,
                                               @RequestParam(value = "appId", required = false) Integer appId,
                                               @RequestParam(value = "metricId", required = false) Integer metricId);

}
