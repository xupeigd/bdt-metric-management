package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.consts.AggregationType;
import com.quicksand.bigdata.metric.management.datasource.models.YamlViewModel;
import com.quicksand.bigdata.metric.management.metric.models.*;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * MetricsService
 *
 * @author page
 * @date 2022-07-27
 */
@FeignClient(
        name = "${vars.name.ms.metric:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.MetricsService:}",
        contextId = "MetricsService")
public interface MetricRestService {

    @GetMapping("/metric/metrics")
    Response<MetricOverviewModel> findMetric(@RequestParam(name = "name", required = false) String name,
                                             @RequestParam(name = "serialNumber", required = false) String serialNumber);

    /**
     * 获取单个metric的详细信息
     *
     * @param id 指标id
     * @return instance of MetricDetailModel
     */
    @GetMapping("/metric/metrics/{id}")
    Response<MetricDetailModel> findMetricById(@PathVariable("id") @Min(value = 0L, message = "不存在的指标实体! ") int id);

    /**
     * 获取单个metric的详细信息
     *
     * @param id 指标id
     * @return instance of MetricDetailModel
     */
    @GetMapping("/metric/metrics/{id}/model")
    Response<MetricSegmentModel> findMetricYamlSegmentById(@PathVariable("id") @Min(value = 0L, message = "不存在的指标实体! ") int id);

    /**
     * 获取聚合方式列表
     *
     * @return instance of AggregationType
     */
    @GetMapping("/metric/metrics/aggregations")
    Response<AggregationType[]> getMetricAggregation();

    /**
     * 获取yaml实体
     *
     * @return instance of YamlViewModel
     */
    @GetMapping("/metric/metrics/{metricId}/yaml")
    Response<YamlViewModel> getMetricYaml(@PathVariable("metricId") @Min(value = 0L, message = "不存在的指标实体! ") int metricId);

    /**
     * 获取指标被调用的应用列表
     *
     * @return instance of AppInvokeDetailModel
     */
    @GetMapping("/metric/{metricId}/apps")
    Response<PageImpl<AppInvokeDetailModel>> listInvokeApps(@PathVariable("metricId") @Min(value = 1L, message = "不存在的指标实体! ") int metricId,
                                                            @Parameter(name = "pageNo", description = "页码（最小1）") @Min(value = 1, message = "最小pageNo为1！")
                                                        @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                            @Parameter(name = "pageSize", description = "页容量（最小1）") @Min(value = 1, message = "最小pageSize为1！")
                                                        @Max(value = 200, message = "最大pageSize不超过200")
                                                        @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize
    );

    /**
     * 根据主题域和业务域自动生成指标编码
     *
     * @return 指标编码字符串
     */
    @GetMapping("/metric/serialNumber")
    Response<String> getSerialNumber(@Parameter(name = "topicId") @Min(value = 1L, message = "不存在的主题域! ")
                                     @RequestParam(name = "topicId", required = true)
                                     int topicId,
                                     @Parameter(name = "businessId") @Min(value = 1L, message = "不存在的业务域! ")
                                     @RequestParam(name = "businessId", required = true) int businessId
    );

    @GetMapping("/metric/metrics/{metricId}/version")
    Response<List<MetricSegmentVersionModel>> findMetricVersions(@Parameter(name = "metricId", description = "指标id") @PathVariable("metricId") @Min(value = 0L, message = "不存在的指标实体! ") int metricId);


}
