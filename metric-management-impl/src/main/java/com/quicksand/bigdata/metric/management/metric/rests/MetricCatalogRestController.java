package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModifyModel;
import com.quicksand.bigdata.metric.management.metric.services.MetricCatalogService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricCatalogVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MetricCatalogRestController
 *
 * @author page
 * @date 2022/7/29
 */
@Slf4j
@CrossOrigin
// @Api("指标目录Apis")
@Tag(name = "指标目录Apis")
@Validated
@RestController
public class MetricCatalogRestController
        implements MetricCatalogRestService, MetricCatalogManageRestService {

    @Resource
    MetricCatalogService metricCatalogService;
    @Resource
    OperationLogService operationLogService;
    @Resource
    MetricDataManager metricDataManager;

    @Operation(description = "获取指标目录的数据")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CATALOGS_LIST') " +
            "|| hasAuthority('OP_METRICS_LIST') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Override
    public Response<List<MetricCatalogModel>> queryMetricCatalogs(@RequestParam(name = "parentCode", required = false)
                                                                  @Parameter(name = "parentCode", description = "父节点的code，可选，默认无", required = false) String parentCode,
                                                                  @RequestParam(name = "mode", required = false, defaultValue = "1")
                                                                  @Parameter(name = "mode", description = "模式 0仅当级 1带子 2 带父 可选 默认1")
                                                                  @Min(value = 0L, message = "不支持的模式！")
                                                                  @Max(value = 2L, message = "不支持的模式！") int mode,
                                                                  @RequestParam(name = "type", required = false, defaultValue = "-1")
                                                                  @Parameter(name = "type", description = "类型 0 主题域 1 业务域 可选 默认 -1 不限制")
                                                                  @Min(-1L) @Max(1L) int type) {
        return Response.ok(
                (StringUtils.hasText(parentCode)
                        ? metricCatalogService.queryCatalogs(parentCode, mode, type)
                        : metricCatalogService.queryCatalogs(mode, type)).stream()
                        .map(v -> JsonUtils.transfrom(v, MetricCatalogModel.class))
                        .collect(Collectors.toList())
        );
    }

    @Operation(description = "创建指标目录实体")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CATALOGS_CREATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Override
    public Response<MetricCatalogModel> createMetricCatalog(@RequestBody @Validated({Insert.class}) MetricCatalogModifyModel model) {
        MetricCatalogVO parentCatalog = metricCatalogService.findCatalog(model.getParent());
        if (null == parentCatalog) {
            return Response.response(HttpStatus.BAD_REQUEST, "父节点不存在！");
        }
        if (null != metricCatalogService.findCatalog(model.getName())) {
            return Response.response(HttpStatus.BAD_REQUEST, String.format("名字为%s的节点已经存在！", model.getName()));
        }
        if (null != metricCatalogService.findCatalog(model.getBusinessCode())) {
            return Response.response(HttpStatus.BAD_REQUEST, String.format("业务编码为%s的节点已经存在！", model.getBusinessCode()));
        }
        return Response.ok(JsonUtils.transfrom(metricCatalogService.upsertCatalog(model), MetricCatalogModel.class));
    }

    @Operation(description = "修改指标目录实体")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CATALOGS_MODIFY') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Override
    public Response<MetricCatalogModel> modifyMetricCatalog(@PathVariable("id") @Min(value = 1L, message = "不存在的目录实体！") int id,
                                                            @RequestBody @Validated({Update.class}) MetricCatalogModifyModel model) {
        if (id != model.getId()) {
            return Response.response(HttpStatus.BAD_REQUEST, "越界访问：错误的节点Id！");
        }
        MetricCatalogVO parentCatalog = metricCatalogService.findCatalog(model.getParent());
        if (null == parentCatalog) {
            return Response.response(HttpStatus.BAD_REQUEST, "不存在的父节点！");
        }
        MetricCatalogVO byName = metricCatalogService.findCatalog(model.getName());
        if (null != byName && !Objects.equals(id, byName.getId())) {
            return Response.response(HttpStatus.BAD_REQUEST, String.format("名字为%s的节点已经存在！", model.getName()));
        }
        MetricCatalogVO byBussinessCode = metricCatalogService.findCatalog(model.getBusinessCode());
        if (null != byBussinessCode && !Objects.equals(id, byBussinessCode.getId())) {
            return Response.response(HttpStatus.BAD_REQUEST, String.format("业务编码为%s的节点已经存在！", model.getBusinessCode()));
        }
        return Response.ok(JsonUtils.transfrom(metricCatalogService.upsertCatalog(model), MetricCatalogModel.class));
    }

    @Operation(description = "删除指标目录实体")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CATALOGS_DELETE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Override
    public Response<Void> removeMetricCatalog(@PathVariable("id") @Min(value = 1L, message = "不存在的目录实体！") int id) {
        MetricCatalogVO catalog = metricCatalogService.findCatalog(id);
        if (null == catalog) {
            return Response.response(HttpStatus.NOT_FOUND);
        }
        if (Objects.equals(DomainType.Topic, catalog.getType())
                && !metricCatalogService.queryCatalogs(catalog.getCode(), 1, DomainType.Business.getCode()).isEmpty()) {
            return Response.response(HttpStatus.NOT_ACCEPTABLE, "当前主题域下含有业务域！");
        }
        if (!metricDataManager.findMetricsByDomain(catalog.getType(), catalog.getId()).isEmpty()) {
            return Response.response(HttpStatus.NOT_ACCEPTABLE, "当前域下含有未删除的指标！");
        }
        Date operationTime = new Date();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        catalog.setName(String.format("%s-%d", catalog.getName(), catalog.getId()));
        catalog.setCode(String.format("%s-%d", catalog.getCode(), catalog.getId()));
        catalog.setStatus(DataStatus.DISABLE);
        catalog.setUpdateTime(operationTime);
        catalog.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
        metricCatalogService.saveMetricCatalog(catalog);
        Try.run(() ->
                operationLogService.log(OperationLogDBVO.builder()
                        .operationTime(operationTime)
                        .address("未知")
                        .ip("0.0.0.0")
                        .userId(null == userDetail ? 0 : userDetail.getId())
                        .type(OperationLogType.DATACATLOG_DELETE)
                        .detail(String.format("删除数据目录，id：%d", id))
                        .build())
        ).onFailure(ex -> log.warn("log error! ", ex));
        return Response.ok();
    }

    @Operation(description = "获取业务域目录列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CATALOGS_LIST') " +
            "|| hasAuthority('OP_METRICS_LIST') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Override
    public Response<List<MetricCatalogModel>> queryMetricBusinessCatalogs(@Parameter(name = "topicIds", description = "主题域ids,可空(多个采用半角逗号分隔)")
                                                                          @RequestParam(name = "topicIds", required = false) List<Integer> topicIds) {
        return Response.ok(metricCatalogService.queryBusinessCatalogsByParentIds(topicIds).stream().map(v -> JsonUtils.transfrom(v, MetricCatalogModel.class)).collect(Collectors.toList()));
    }
}
