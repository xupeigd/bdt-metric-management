package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.datasource.models.YamlViewModel;
import com.quicksand.bigdata.metric.management.metric.models.*;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

/**
 * MetricManageRestService
 *
 * @author page
 * @date 2022/7/29
 */
@FeignClient(
        name = "${vars.name.ms.metric:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.MetricManageRestService:}",
        contextId = "MetricManageRestService")
public interface MetricManageRestService {

    /**
     * 创建指标
     *
     * @param model 创建参数
     * @return 回显model
     */
    @PostMapping("/metric/metrics")
    Response<MetricDetailModel> createMetric(@RequestBody @Validated({Insert.class}) MetricInsertModel model);

    /**
     * 修改指标
     *
     * @param model 修改参数
     * @return 回显model
     */
    @PutMapping("/metric/metrics/{id}")
    Response<MetricDetailModel> modifyMetric(@PathVariable("id") @Min(value = 0L, message = "不存在的指标实体！ ") int id,
                                             @RequestBody @Validated({Update.class}) MetricInsertModel model);

    /**
     * 删除指标实体
     *
     * @return void
     */
    @DeleteMapping("/metric/metrics/{id}")
    Response<Void> removeMetric(@PathVariable("id") @Min(value = 0L, message = "不存在的指标实体！ ") int id);

    /**
     * 修改指标的上下线状态
     *
     * @param id          指标id
     * @param pubsub      0下线 1上线
     * @param metricQuota 配额数据
     * @return 回显 PubsubStatus
     */
    @PutMapping("/metric/metrics/{id}/{pubsub}")
    Response<PubsubStatus> modifyMetricPubsubStatus(@PathVariable("id") int id,
                                                    @PathVariable("pubsub") int pubsub,
                                                    @Validated @RequestBody(required = false) MetricQuotaModel metricQuota);

    /**
     * 检查yaml片段的语法
     *
     * @return
     */
    @PostMapping("/metric/metrics/yaml/verfiy")
    Response<MetricInsertModel> verifyYamlSegment(@RequestBody @Validated({Update.class}) MetricInsertModel model);


    /**
     * 按指标数据集配置生产yaml
     *
     * @param model 指标前置信息
     * @return instance of MetricDetailModel
     */
    @PostMapping("/metric/metrics/build")
    Response<YamlViewModel> buildMetricYamlSegment(@RequestBody @Validated({Insert.class}) MetricYamlBuilderModel model);

    /**
     * 分页获取metric的列表
     *
     * @param queryModel 用户请求内容
     * @return Page of MetricOverviewModel
     */
    @PostMapping("/metric/list")
    Response<PageImpl<MetricOverviewModel>> listMetrics(@RequestBody MetricQueryModel queryModel);

    /**
     * 让指标与应用解绑
     *
     * @param appInvokeDetailModel 用户请求实体
     * @return null
     */
    @PostMapping("/metric/unbind/app")
    Response<Void> unbindAppAndMetric(@RequestBody AppInvokeDetailModel appInvokeDetailModel);


}
