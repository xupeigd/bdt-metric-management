package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.metric.models.MetricLabelBindModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricLabelModifyModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import com.quicksand.bigdata.metric.management.metric.services.MetricLabelService;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * MetricLabelController
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/22 15:33
 * @description
 */
@Slf4j
@Validated
@CrossOrigin
@Tag(name = "指标标签服务Apis")
@RestController
public class MetricLabelController implements MetricLabelRestService, MetricLabelManageRestService {
    @Resource
    MetricLabelService metricLabelService;


    @Operation(description = "获取当前用户标签下的指标列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<PageImpl<MetricOverviewModel>> getMetricListByLabelId(Integer pageNo, Integer pageSize, Integer labelId) {
        PageRequest updateTime = PageRequest.of(pageNo - 1, pageSize);
        return Response.ok(metricLabelService.findAllMetricsByLabelId(labelId, updateTime));
    }

    @Operation(description = "获取当前用户下的所有标签列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<List<MetricLabelModifyModel>> getAllLabelsList() {
        return Response.ok(metricLabelService.findUserAllLabels());
    }

    @Operation(description = "获取当前用户与指标下已绑定的标签列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<List<MetricLabelModifyModel>> getMetricLabelsList(int metricId) {
        return Response.ok(metricLabelService.findUserMetricLabelsBind(metricId));
    }

    @Operation(description = "获取当前用户与指标下剩余可选的标签列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<List<MetricLabelModifyModel>> getMetricOptionalLabels(int metricId) {
        return Response.ok(metricLabelService.findUserMetricLabelsForAdd(metricId));
    }

    @Operation(description = "新建标签")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<MetricLabelModifyModel> createMetricLabel(@RequestBody @Validated({Insert.class}) MetricLabelModifyModel model) {
        return Response.ok(metricLabelService.upsertMetricLabel(model));
    }

    @Operation(description = "修改标签")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<MetricLabelModifyModel> modifyMetricLabel(@RequestBody @Validated({Update.class}) MetricLabelModifyModel model) {
        return Response.ok(metricLabelService.upsertMetricLabel(model));
    }

    @Operation(description = "删除标签")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<Void> removeMetricLabel(@Parameter(description = "要删除标签id", required = true) int id) {
        return Response.ok(metricLabelService.markDeleteByLabelId(id));
    }

    @Operation(description = "绑定指标与标签")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<Void> bindMetricAndLabel(@RequestBody @Validated({Insert.class}) MetricLabelBindModel model) {
        metricLabelService.bindMetricAndLabel(model);
        return Response.ok();
    }

    @Operation(description = "解开绑定指标与标签")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<Void> unbindMetricAndLabel(@RequestBody @Validated({Update.class}) MetricLabelBindModel model) {
        return Response.ok(metricLabelService.markRelationDeleteByLabelIdAndMetricId(model.getMetricId(), model.getLabelId()));
    }
}
