package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogAmisModifyModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModifyModel;
import com.quicksand.bigdata.metric.management.metric.rests.MetricCatalogManageRestService;
import com.quicksand.bigdata.metric.management.metric.rests.MetricCatalogRestService;
import com.quicksand.bigdata.metric.management.metric.services.MetricCatalogService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricCatalogVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Class MetricCatalogAmisRestController
 *
 * @Author: page
 * @Date: 2025/3/7
 * @Description:
 */
@RestController
public class MetricCatalogAmisRestController {

    @Resource
    MetricCatalogService metricCatalogService;
    @Resource
    MetricCatalogRestService metricCatalogRestService;
    @Resource
    MetricCatalogManageRestService metricCatalogManageRestService;

    /**
     * 查询所有主题域
     *
     * @return List<MetricCatalogModel>
     */
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
    @GetMapping(Vars.PATH_ROOT + "/amis/metric-catalogs")
    public FrameworkResponse<List<MetricCatalogModel>, Void> listMetricCatalogs(@RequestParam(name = "parentCode", required = false)
                                                                                @Parameter(name = "parentCode", description = "父节点的code，可选，默认无", required = false) String parentCode,
                                                                                @RequestParam(name = "mode", required = false, defaultValue = "1")
                                                                                @Parameter(name = "mode", description = "模式 0仅当级 1带子 2 带父 可选 默认1")
                                                                                @Min(value = 0L, message = "不支持的模式！")
                                                                                @Max(value = 2L, message = "不支持的模式！") int mode,
                                                                                @RequestParam(name = "type", required = false, defaultValue = "-1")
                                                                                @Parameter(name = "type", description = "类型 0 主题域 1 业务域 可选 默认 -1 不限制")
                                                                                @Min(-1L) @Max(1L) int type) {
        return FrameworkResponse.extend(metricCatalogRestService.queryMetricCatalogs(parentCode, mode, type));
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
    @GetMapping(Vars.PATH_ROOT + "/amis/metric-catalogs/{id}")
    public FrameworkResponse<MetricCatalogModel, Void> queryMetricCatalog(@PathVariable("id") Integer id) {
        MetricCatalogVO catalog = metricCatalogService.findCatalog(id);
        if (null == catalog) {
            return FrameworkResponse.extend(Response.response(HttpStatus.NOT_FOUND));
        }
        return FrameworkResponse.extend(Response.ok(JsonUtils.transfrom(catalog, MetricCatalogModel.class)));
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
    @PostMapping(Vars.PATH_ROOT + "/amis/metric-catalogs")
    public FrameworkResponse<MetricCatalogModel, Void> createMetricCatalog(@RequestBody @Validated({Insert.class}) MetricCatalogModifyModel model) {
        return FrameworkResponse.extend(metricCatalogManageRestService.createMetricCatalog(model));
    }

    /**
     * 修改catlog实体节点
     * （修改父节点时，子节点一并移动）
     *
     * @param model 修改参数
     * @return 回显 instance of MetricCatalogModel
     */
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
    @PutMapping(Vars.PATH_ROOT + "/amis/metric-catalogs/{id}")
    public FrameworkResponse<MetricCatalogModel, Void> modifyMetricCatalog(@PathVariable("id") @Min(value = 1L, message = "不存在的目录实体！") int id,
                                                                           @RequestBody MetricCatalogAmisModifyModel model) {
        MetricCatalogModifyModel build = MetricCatalogModifyModel.builder()
                .id(model.getId())
                .name(model.getName())
                .businessCode(model.getBusinessCode())
                .status(model.getStatus())
                .parent(null == model.getParent() ? -1 : model.getParent().getId())
                .build();
        return FrameworkResponse.extend(metricCatalogManageRestService.modifyMetricCatalog(id, build));
    }

    /**
     * 删除权限
     *
     * @param ids catalogIds
     * @return
     */
    @Transactional
    @DeleteMapping(Vars.PATH_ROOT + "/amis/metric-catalogs/{ids}")
    public FrameworkResponse<List<Integer>, Void> deletePermissions(@PathVariable("ids") List<Integer> ids) {
        metricCatalogService.removeCatalogs(ids);
        return FrameworkResponse.frameworkResponse(ids, null, 0, "success");
    }

}
