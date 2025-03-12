package com.quicksand.bigdata.metric.management.datasource.rests;

import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.ClusterInfoDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.ClusterInfoModel;
import com.quicksand.bigdata.metric.management.datasource.models.TableColumnModel;
import com.quicksand.bigdata.metric.management.engine.EngineService;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author page
 * @date 2022/7/26
 */
@Slf4j
@Validated
@RestController
@CrossOrigin
// @Api("集群信息Apis")
@Tag(name = "集群信息Apis")
public class ClusterInfoRestController
        implements ClusterInfoRestService {

    @Resource
    ClusterInfoDataManager clusterInfoDataManager;
    @Resource
    EngineService engineService;

    @Operation(description = "获取集群信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
    })
    @PreAuthorize("hasAuthority('OP_DATASOURCE_CLUSTER_LIST') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication)")
    @Override
    public Response<List<ClusterInfoModel>> queryClusterInfos(@RequestParam(required = false, defaultValue = "") String keyword) {
        return Response.ok(clusterInfoDataManager.findAllClusterInfos()
                .stream()
                .filter(v -> !StringUtils.hasText(keyword) || v.getName().contains(keyword))
                .map(v -> {
                    ClusterInfoModel model = new ClusterInfoModel();
                    BeanUtils.copyProperties(v, model);
                    //清理密码
                    model.setPassword(null);
                    return model;
                }).collect(Collectors.toList()));
    }

    @Operation(description = "获取集群的数据表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Override
    public Response<List<String>> queryTables(@PathVariable("clusterId") @Min(value = 1L, message = "无效的的集群")
                                              @Max(value = Integer.MAX_VALUE, message = "无效的集群") int clusterId,
                                              @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) throws Exception {
        ClusterInfoDBVO clusterInfo = clusterInfoDataManager.findClusterInfo(clusterId);
        if (null == clusterInfo) {
            return Response.notfound();
        }
        return Response.ok(engineService.queryTables(clusterInfo));
    }

    @Operation(description = "获取集群数据表的字段信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Override
    public Response<List<TableColumnModel>> queryColumns(@PathVariable("clusterId") @Min(value = 1L, message = "无效的的集群")
                                                         @Max(value = Integer.MAX_VALUE, message = "无效的集群") int clusterId,
                                                         @PathVariable("tableName") @NotBlank String tableName,
                                                         @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) throws Exception {
        ClusterInfoDBVO clusterInfo = clusterInfoDataManager.findClusterInfo(clusterId);
        if (null == clusterInfo) {
            return Response.response(HttpStatus.NOT_FOUND, "非法集群信息！");
        }
        return Response.ok(engineService.queryColumInfos(clusterInfo, tableName, keyword));
    }

}
