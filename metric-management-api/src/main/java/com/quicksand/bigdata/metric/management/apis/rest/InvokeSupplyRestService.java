package com.quicksand.bigdata.metric.management.apis.rest;

import com.quicksand.bigdata.metric.management.apis.models.InvokeApplyDetailModel;
import com.quicksand.bigdata.metric.management.apis.models.InvokeApplyModel;
import com.quicksand.bigdata.metric.management.apis.models.MetricInvokeInfosModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * InvokeSupplyRestManageService
 *
 * @author page
 * @date 2022/10/11
 */
@FeignClient(
        name = "${vars.name.ms.apis:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.InvokeSupplyRestService:}",
        contextId = "InvokeSupplyRestService")
public interface InvokeSupplyRestService {

    /**
     * 列出市场指标
     * (可申请调用的)
     *
     * @param appId 应用Id，仅用于对当前页面的指标进行检查，是否有调用权限
     * @return page of MetricOverviewModel
     */
    @GetMapping("/apis/invoke/market")
    Response<PageImpl<MetricOverviewModel>> listMetrics4Market(@Parameter(name = "pageNo", description = "页码(最小1),可选")
                                                               @Min(value = 1, message = "最小pageNo为1！")
                                                               @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                               @Parameter(name = "pageSize", description = "页容量(最小1),可选")
                                                               @Min(value = 1, message = "最小pageSize为1！") @Max(value = 200, message = "最大pageSize不超过200")
                                                               @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                               @Parameter(name = "keyword", description = "指标名称/英文名/别名模糊搜索关键字")
                                                               @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                                                               @Parameter(name = "techOwnerIds", description = "技术负责人Ids，可选")
                                                               @RequestParam(name = "techOwnerIds", required = false) List<Integer> techOwnerIds,
                                                               @Parameter(name = "businessOwnerIds", description = "业务负责人Ids，可选")
                                                               @RequestParam(name = "businessOwnerIds", required = false) List<Integer> businessOwnerIds,
                                                               @Parameter(name = "appId", description = "应用Id")
                                                               @RequestParam(name = "appId", required = false) Integer appId);


    /**
     * 分页查询调用申请
     *
     * @return page of InvokeApplyDetailModel
     */
    @GetMapping("/apis/invoke/applys")
    Response<PageImpl<InvokeApplyDetailModel>> listApplys(@Parameter(name = "pageNo", description = "页码(最小1),可选")
                                                          @Min(value = 1, message = "最小pageNo为1！")
                                                          @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                          @Parameter(name = "pageSize", description = "页容量(最小1),可选")
                                                          @Min(value = 1, message = "最小pageSize为1！") @Max(value = 200, message = "最大pageSize不超过200")
                                                          @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                          @Parameter(name = "metricKeyword", description = "指标名称")
                                                          @RequestParam(name = "metricKeyword", required = false) String metricKeyword,
                                                          @Parameter(name = "appIds", description = "应用Id 可选,多选")
                                                          @RequestParam(name = "appIds", required = false) List<Integer> appIds,
                                                          @Parameter(name = "applyUsers", description = "提交人用户Id，可选,多选")
                                                          @RequestParam(name = "applyUsers", required = false) List<Integer> applyUsers,
                                                          @Parameter(name = "states", description = "审批状态 0未审核 1核准 2拒绝 可选,多选")
                                                          @RequestParam(name = "states", required = false) List<Integer> states);


    /**
     * 我提交的审批
     *
     * @return page of InvokeApplyDetailModel
     */
    @GetMapping("/apis/invoke/mine")
    Response<PageImpl<InvokeApplyDetailModel>> myApplys(@Parameter(name = "pageNo", description = "页码(最小1),可选")
                                                        @Min(value = 1, message = "最小pageNo为1！")
                                                        @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                        @Parameter(name = "pageSize", description = "页容量(最小1),可选")
                                                        @Min(value = 1, message = "最小pageSize为1！") @Max(value = 200, message = "最大pageSize不超过200")
                                                        @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                        @Parameter(name = "metricKeyword", description = "指标名称")
                                                        @RequestParam(name = "metricKeyword", required = false) String metricKeyword,
                                                        @Parameter(name = "appIds", description = "应用Id 可选,多选")
                                                        @RequestParam(name = "appIds", required = false) List<Integer> appIds,
                                                        @Parameter(name = "states", description = "审批状态 0未审核 1核准 2拒绝 可选,多选")
                                                        @RequestParam(name = "states", required = false) List<Integer> states);

    /**
     * 获取申请详情
     *
     * @param id 申请单Id
     * @return insstance of InvokeApplyDetailModel / null
     */
    @GetMapping("/apis/invoke/applys/{id}")
    Response<InvokeApplyDetailModel> findApply(@Parameter(name = "id", description = "申请Id")
                                               @Min(value = 1, message = "最小pageNo为1！")
                                               @PathVariable("id") int id);

    /**
     * 新建调用申请
     *
     * @param model 申请参数
     * @return instance of InvokeApplyDetailModel
     */
    @PostMapping("/apis/invoke/applys")
    Response<List<InvokeApplyDetailModel>> applyInvoke(@Validated @RequestBody InvokeApplyModel model);


    /**
     * 获取接口调用的信息
     *
     * @param id 指标id
     * @return instance of MetricInvokeInfoModel
     */
    @GetMapping("/apis/invoke/{id}/infos")
    Response<MetricInvokeInfosModel> invokeInfos(@Parameter(name = "id", description = "指标Id")
                                                 @Min(value = 1, message = "不存在的指标！")
                                                 @PathVariable("id") int id);

}
