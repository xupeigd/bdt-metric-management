package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.metric.models.MetricLabelModifyModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * MetricLabelRestService
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/22 15:41
 * @description
 */
public interface MetricLabelRestService {

    /**
     * 获取当前用户标签下的指标列表
     *
     * @param labelId 创建参数
     * @return 回显 instance of MetricCatalogModel
     */
    @GetMapping("/metric/label/metrics")
    Response<PageImpl<MetricOverviewModel>> getMetricListByLabelId(@Parameter(name = "pageNo", description = "页码（最小1）") @Min(value = 1, message = "最小pageNo为1！")
                                                               @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                                   @Parameter(name = "pageSize", description = "页容量（最小1）") @Min(value = 1, message = "最小pageSize为1！")
                                                               @Max(value = 200, message = "最大pageSize不超过200")
                                                               @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                                   @Parameter(name = "labelId", description = "标签id,必传值", required = true) @Min(value = 1L, message = "不存在的标签! ") @RequestParam("labelId") Integer labelId);


    /**
     * 获取当前用户下的所有标签列表
     */
    @GetMapping("/metric/label/list")
    Response<List<MetricLabelModifyModel>> getAllLabelsList();

    /**
     * 获取当前用户与指标下已绑定的标签列表
     */
    @GetMapping("/metric/label/{metricId}/bind")
    Response<List<MetricLabelModifyModel>> getMetricLabelsList(@Parameter(name = "metricId", description = "指标id") @PathVariable("metricId") @Min(value = 0L, message = "不存在的指标实体! ") int metricId);


    /**
     * 获取当前用户与指标下剩余可选的标签列表
     */
    @GetMapping("/metric/label/{metricId}/optional")
    Response<List<MetricLabelModifyModel>> getMetricOptionalLabels(@Parameter(name = "metricId", description = "指标id") @PathVariable("metricId") @Min(value = 0L, message = "不存在的指标实体! ") int metricId);
}
