package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModifyModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

/**
 * MetricCatalogManageRestService
 *
 * @author page
 * @date 2022/7/29
 */
@FeignClient(
        name = "${vars.name.ms.metric:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.MetricCatalogManageRestService:}",
        contextId = "MetricCatalogManageRestService")
public interface MetricCatalogManageRestService {

    /**
     * 创建catlog实体节点
     *
     * @param model 创建参数
     * @return 回显 instance of MetricCatalogModel
     */
    @PostMapping("/metric/catalogs")
    Response<MetricCatalogModel> createMetricCatalog(@RequestBody @Validated({Insert.class}) MetricCatalogModifyModel model);


    /**
     * 修改catlog实体节点
     * （修改父节点时，子节点一并移动）
     *
     * @param model 修改参数
     * @return 回显 instance of MetricCatalogModel
     */
    @PutMapping("/metric/catalogs/{id}")
    Response<MetricCatalogModel> modifyMetricCatalog(@PathVariable("id") @Min(value = 1L, message = "不存在的目录实体！") int id,
                                                     @RequestBody @Validated({Update.class}) MetricCatalogModifyModel model);

    /**
     * 删除catalog实体
     * （删除父节点时，子节点一并删除）
     *
     * @param id 节点Id
     * @return void
     */
    @DeleteMapping("/metric/catalogs/{id}")
    Response<Void> removeMetricCatalog(@PathVariable("id") @Min(value = 1L, message = "不存在的目录实体！") int id);


}
