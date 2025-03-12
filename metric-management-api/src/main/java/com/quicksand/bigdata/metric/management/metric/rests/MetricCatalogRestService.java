package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModel;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * MetricTopicRestService
 *
 * @author page
 * @date 2022/7/29
 */
@FeignClient(
        name = "${vars.name.ms.metric:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.MetricCatalogRestService:}",
        contextId = "MetricCatalogRestService")
public interface MetricCatalogRestService {

    /**
     * 获取指标目录的数据
     * <p>
     * （同层时并列返回，不同层时合并树状结构返回）
     *
     * @param parentCode 父节点的code，可选，默认无
     * @param mode       模式 0仅当级 1带子 2 带父 可选 默认1
     * @return list of MetricCatalogModel
     */
    @GetMapping("/metric/catalogs")
    Response<List<MetricCatalogModel>> queryMetricCatalogs(@RequestParam(name = "parentCode", required = false)
                                                           @Parameter(name = "父节点的code，可选，默认无") String parentCode,
                                                           @RequestParam(name = "mode", required = false, defaultValue = "1")
                                                           @Parameter(name = "模式 0仅当级 1带子 2 带父 可选 默认1")
                                                           @Min(value = 0L, message = "不支持的模式！")
                                                           @Max(value = 2L, message = "不支持的模式！") int mode,
                                                           @RequestParam(name = "type", required = false, defaultValue = "-1")
                                                           @Parameter(name = "类型 0 主题域 1 业务域 可选 默认 -1 不限制")
                                                           @Min(-1L) @Max(1L) int type);

    /**
     * 按需获取业务域
     *
     * @param topicIds 主题域ids
     * @return list of MetricCatalogModel
     */
    @GetMapping("/metric/catalogs/business")
    Response<List<MetricCatalogModel>> queryMetricBusinessCatalogs(@Parameter(name = "topicIds", description = "主题域ids,可空(多个采用半角逗号分隔)")
                                                                   @RequestParam(name = "topicIds", required = false) List<Integer> topicIds);

}
