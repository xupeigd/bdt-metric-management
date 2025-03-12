package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.metric.models.MetricLabelBindModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricLabelModifyModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

/**
 * MetricLabelRestService
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/22 15:41
 * @description
 */
public interface MetricLabelManageRestService {

    /**
     * 新建标签
     *
     * @param model 创建参数
     * @return 回显 instance of MetricLabelModifyModel
     */
    @PostMapping("/metric/label/add")
    Response<MetricLabelModifyModel> createMetricLabel(@RequestBody @Validated({Insert.class}) MetricLabelModifyModel model);


    /**
     * 修改标签
     *
     * @param model 修改参数
     * @return 回显 instance of MetricLabelModifyModel
     */
    @PutMapping("/metric/label/update")
    Response<MetricLabelModifyModel> modifyMetricLabel(@RequestBody @Validated({Update.class}) MetricLabelModifyModel model);

    /**
     * 删除标签实体
     * （删除标签时，标签绑定一并删除）
     *
     * @param id 标签Id
     * @return void
     */
    @DeleteMapping("/metric/label/{id}")
    Response<Void> removeMetricLabel(@PathVariable("id") @Min(value = 1L, message = "不存在的标签！") int id);


    /**
     * 绑定指标与标签
     *
     * @param model MetricLabelBindModel
     * @return void
     */
    @PutMapping("/metric/label/bind")
    Response<Void> bindMetricAndLabel(@RequestBody @Validated({Insert.class}) MetricLabelBindModel model);


    /**
     * 绑定指标与标签
     *
     * @param model MetricLabelBindModel
     * @return void
     */
    @PutMapping("/metric/label/unbind")
    Response<Void> unbindMetricAndLabel(@RequestBody @Validated({Update.class}) MetricLabelBindModel model);
}
