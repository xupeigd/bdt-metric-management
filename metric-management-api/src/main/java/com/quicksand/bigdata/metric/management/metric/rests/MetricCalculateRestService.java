package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.metric.models.CandidateValuePairModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatculateResponseModel;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * MetricCalculateRestService
 * (指标计算restService)
 *
 * @author page
 * @date 2022/8/25
 */
@FeignClient(
        name = "${vars.name.ms.metric:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.MetricCalculateRestService:}",
        contextId = "MetricCalculateRestService")
public interface MetricCalculateRestService {

    /**
     * 试算
     *
     * @param metricId 指标id
     * @return insatnce of MetricCatculateResponseModel
     */
    @GetMapping("/calculate/metrics/{metricId}")
    Response<MetricCatculateResponseModel> calculate(@Parameter(name = "指标Id")
                                                     @Min(value = 1L, message = "不存在的指标！")
                                                     @Max(value = Integer.MAX_VALUE, message = "不存在的指标！")
                                                     @PathVariable("metricId") int metricId);

    /**
     * 探测候选值
     * （仅纬度）
     *
     * @return List of CandidateValuePairModel
     */
    @GetMapping("/calculate/metrics/{metricId}/candidateValue")
    Response<List<CandidateValuePairModel>> candidateValues(@Parameter(name = "指标Id")
                                                            @Min(value = 1L, message = "不存在的指标！")
                                                            @Max(value = Integer.MAX_VALUE, message = "不存在的指标！")
                                                            @PathVariable("metricId") int metricId,
                                                            @Parameter(name = "纬度，多选，半角逗号分隔")
                                                            @Validated @NotEmpty(message = "探索纬度不能为空")
                                                            @RequestParam("dimensions") List<String> dimensions);

}
