package com.quicksand.bigdata.metric.management.apis.rest;

import com.quicksand.bigdata.metric.management.apis.models.AppDetailModel;
import com.quicksand.bigdata.metric.management.apis.models.AppModel;
import com.quicksand.bigdata.metric.management.metric.models.DropDownListModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
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
 * AppRestService
 *
 * @author page
 * @date 2022/9/27
 */
@FeignClient(
        name = "${vars.name.ms.apis:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.AppRestService:}",
        contextId = "AppRestService")
public interface AppRestService {

    /**
     * 获取应用信息
     *
     * @return list of AppModel
     */
    @GetMapping("/apis/apps")
    Response<PageImpl<AppModel>> fetchApps(@Parameter(name = "pageNo", description = "页码（最小1）") @Min(value = 1, message = "最小pageNo为1！")
                                       @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                       @Parameter(name = "pageSize", description = "页容量（最小1）") @Min(value = 1, message = "最小pageSize为1！")
                                       @Max(value = 200, message = "最大pageSize不超过200")
                                       @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                       @Parameter(name = "nameKeyword", description = "名称关键字")
                                       @RequestParam(name = "nameKeyword", required = false, defaultValue = "") String nameKeyword,
                                       @Parameter(name = "ownerIds", description = "负责人ids(多个采用半角逗号分隔)")
                                       @RequestParam(name = "ownerIds", required = false) List<Integer> ownerIds,
                                       @Parameter(name = "appTypes", description = "应用类型ids(多个采用半角逗号分隔)")
                                       @RequestParam(name = "appTypes", required = false) List<Integer> appTypes,
                                       @Parameter(name = "ownerNameKeyword", description = "负责人名称关键字(可模糊搜索)")
                                       @RequestParam(name = "ownerNameKeyword", required = false, defaultValue = "") String ownerNameKeyword);

    /**
     * 获取所有app列表
     *
     * @return List of AppVO
     */
    @GetMapping("/apis/apps/list")
    Response<List<AppModel>> fetchAppList();

    /**
     * 获取应用的详细信息
     *
     * @param appId 应用id
     * @return instance of AppDetailModel
     */
    @GetMapping("/apis/apps/{id}")
    Response<AppDetailModel> findAppInfo(@Parameter(name = "id", description = "应用主键") @Min(value = 1, message = "不支持的应用主键！")
                                         @PathVariable("id") int appId);

    /**
     * 根据appId分页获取指标数据
     *
     * @return page of MetricOverviewModel
     */
    @GetMapping("/apis/apps/{id}/metrics")
    Response<PageImpl<MetricOverviewModel>> findAppMetrics(@Parameter(name = "id", description = "应用主键")
                                                           @Min(value = 1, message = "不支持的应用主键！")
                                                           @PathVariable("id") int id,
                                                           @Parameter(name = "pageNo", description = "页码（最小1）") @Min(value = 1, message = "最小pageNo为1！")
                                                           @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                           @Parameter(name = "pageSize", description = "页容量（最小1）") @Min(value = 1, message = "最小pageSize为1！")
                                                           @Max(value = 200, message = "最大pageSize不超过200")
                                                           @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                           @Parameter(name = "effective", description = "收否生效 1生效 0失效") @Min(value = 0, message = "")
                                                           @Max(value = 1, message = "")
                                                           @RequestParam(name = "effective", required = false, defaultValue = "1") Integer effective,
                                                           @Parameter(name = "metricKeyword", description = "指标名称(可模糊搜索)")
                                                           @RequestParam(name = "metricKeyword", required = false, defaultValue = "") String metricKeyword);


    /**
     * 获取应用下拉列表
     *
     * @return instance of AppDetailModel
     */
    @GetMapping("/apis/apps/ddList")
    Response<List<DropDownListModel>> getAppsDropDownList();


    /**
     * 获取应用可选指标下拉列表
     *
     * @return instance of AppDetailModel
     */
    @GetMapping("/metric/{appId}/ddList")
    Response<List<DropDownListModel>> getAppMetricsDropDownList(@PathVariable("appId") @Min(value = 1L, message = "不存在的应用! ") int appId);


}
