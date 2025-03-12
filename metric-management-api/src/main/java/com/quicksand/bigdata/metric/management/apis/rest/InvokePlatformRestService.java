package com.quicksand.bigdata.metric.management.apis.rest;

import com.quicksand.bigdata.metric.management.apis.models.ExplainAttributesModel;
import com.quicksand.bigdata.metric.management.apis.models.ExplainGroupModel;
import com.quicksand.bigdata.metric.management.datasource.models.ClusterInfoModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * InvokePlatformRestService
 * (调用平台服务)
 *
 * @author page
 * @date 2022/10/18
 */
@FeignClient(
        name = "${vars.name.ms.apis:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.InvokePlatformRestService:}",
        contextId = "InvokePlatformRestService")
public interface InvokePlatformRestService {

    /**
     * 获取所有的clsuter连接信息
     *
     * @return list of ClusterInfoModel
     */
    @GetMapping("/invoke/clusters")
    Response<List<ClusterInfoModel>> fecthAllClusterInfos(@RequestParam(name = "clusterIds", required = false) List<Integer> clusterIds);

    /**
     * 解析指标
     *
     * @param attributes 解析参数
     * @return list of ExplainGroupModel
     */
    @PostMapping("/invoke/explain/metrics")
    Response<List<ExplainGroupModel>> explainMetrics(@Validated @RequestBody ExplainAttributesModel attributes);

}
