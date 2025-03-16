package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.ClusterInfoDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.ClusterInfoModel;
import com.quicksand.bigdata.metric.management.datasource.models.TableColumnModel;
import com.quicksand.bigdata.metric.management.datasource.services.ClusterInfoService;
import com.quicksand.bigdata.metric.management.datasource.vos.ClusterInfoVO;
import com.quicksand.bigdata.metric.management.engine.EngineService;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class ClusterInfoRestController
 *
 * @Author: page
 * @Date: 2025/3/1
 * @Description:
 */
@RestController
public class ClusterInfoAmisRestController {

    @Resource
    ClusterInfoService clusterInfoService;
    @Resource
    ClusterInfoDataManager clusterInfoDataManager;
    @Resource
    EngineService engineService;

    @Operation(description = "集群列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @PreAuthorize("hasAuthority('OP_DATASOURCE_CLUSTER_LIST') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication)")
    @GetMapping(Vars.PATH_ROOT + "/amis/clusters")
    public FrameworkResponse<PageImpl<ClusterInfoModel>, Void> listClusterInfos(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                                                                @RequestParam(name = "perPage", defaultValue = "20") int pageSize) {
        PageImpl<ClusterInfoVO> page = clusterInfoService.queryClusterInfos(pageNo, pageSize);
        PageImpl<ClusterInfoModel> pi = null != page && !CollectionUtils.isEmpty(page.getItems())
                ? PageImpl.build(page.getPageNo(), page.getPageSize(), page.getTotalPage(), page.getTotal(),
                page.getItems().stream()
                        .map(v -> JsonUtils.transfrom(v, ClusterInfoModel.class))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                : PageImpl.buildEmptyPage(pageNo, pageSize);
        return FrameworkResponse.frameworkResponse(pi, null, 0, "success!");
    }

    @Operation(description = "创建集群信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @PostMapping(Vars.PATH_ROOT + "/amis/clusters")
    public FrameworkResponse<ClusterInfoModel, Void> createClusterInfo(@RequestBody ClusterInfoModel model) {
        ClusterInfoVO vo = clusterInfoService.saveClusterInfo(model);
        return FrameworkResponse.frameworkResponse(JsonUtils.transfrom(vo, ClusterInfoModel.class), null, 0, "success");
    }

    @Operation(description = "按id查找集群信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/clusters/{clusterId}")
    public FrameworkResponse<ClusterInfoModel, Void> queryClusterInfo(@PathVariable("clusterId") Integer clusterId) {
        ClusterInfoVO vo = clusterInfoService.queryClusterInfo(clusterId);
        return FrameworkResponse.frameworkResponse(JsonUtils.transfrom(vo, ClusterInfoModel.class), null, 0, "success");
    }

    @Operation(description = "修改集群信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @PutMapping(Vars.PATH_ROOT + "/amis/clusters/{clusterId}")
    public FrameworkResponse<ClusterInfoModel, Void> modifyClusterInfo(@PathVariable("clusterId") Integer clusterId,
                                                                       @RequestBody ClusterInfoModel model) {
        model.setId(clusterId);
        ClusterInfoVO vo = clusterInfoService.saveClusterInfo(model);
        return FrameworkResponse.frameworkResponse(JsonUtils.transfrom(vo, ClusterInfoModel.class), null, 0, "success");
    }

    @Operation(description = "删除集群信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @DeleteMapping(Vars.PATH_ROOT + "/amis/clusters/{clusterIds}")
    public FrameworkResponse<List<Integer>, Void> deleteClusterInfos(@PathVariable("clusterIds") List<Integer> clusterIds) {
        clusterInfoService.deleteClusterInfos(clusterIds);
        return FrameworkResponse.frameworkResponse(clusterIds, null, 0, "success");
    }

    @Operation(description = "查找用于select的集群信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/datasource/clusters")
    public FrameworkResponse<List<ClusterInfoModel>, Void> querySelectClusterInfos() {
        return FrameworkResponse.frameworkResponse(clusterInfoDataManager.findAllClusterInfos()
                .stream()
                .map(v -> {
                    ClusterInfoModel model = new ClusterInfoModel();
                    BeanUtils.copyProperties(v, model);
                    //清理密码
                    model.setPassword(null);
                    return model;
                })
                .collect(Collectors.toList()), null, 0, "success");
    }

    @Data
    public static final class Select {
        String name;
        String value;
    }

    /**
     * 获取集群对应的表
     * <p>
     * （集群必须有默认的库）
     * （从db/dwh fetch信息）
     *
     * @return list of tableName
     */
    @GetMapping(Vars.PATH_ROOT + "/amis/datasource/clusters/{clusterId}/tables")
    public FrameworkResponse<List<Select>, Void> queryTables(@PathVariable("clusterId") @Min(value = 1L, message = "无效的的集群")
                                                             @Max(value = Integer.MAX_VALUE, message = "无效的集群") int clusterId,
                                                             @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) throws Exception {
        ClusterInfoDBVO clusterInfo = clusterInfoDataManager.findClusterInfo(clusterId);
        if (null == clusterInfo) {
            return FrameworkResponse.frameworkResponse(1, "not found");
        }
        List<String> tables = engineService.queryTables(clusterInfo);
        if (!CollectionUtils.isEmpty(tables)) {
            return FrameworkResponse.frameworkResponse(tables.stream()
                    .map(v -> {
                        Select select = new Select();
                        select.setName(v);
                        select.setValue(v);
                        return select;
                    })
                    .collect(Collectors.toList()), null, 0, "success");
        }
        return FrameworkResponse.frameworkResponse(Collections.emptyList(), null, 0, "success");
    }

    @Data
    public static final class OptionModel<T> {
        List<T> options;
    }

    @Data
    public static final class TableColumnOptionModel extends TableColumnModel {
        String label;
        String value;
    }

    @GetMapping(Vars.PATH_ROOT + "/amis/datasource/clusters/{clusterId}/tables/{tableName}/columns")
    public FrameworkResponse<OptionModel<TableColumnOptionModel>, Void> queryColumns(@PathVariable("clusterId") @Min(value = 1L, message = "无效的的集群")
                                                                                     @Max(value = Integer.MAX_VALUE, message = "无效的集群") int clusterId,
                                                                                     @PathVariable("tableName") @NotBlank String tableName,
                                                                                     @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) throws Exception {
        ClusterInfoDBVO clusterInfo = clusterInfoDataManager.findClusterInfo(clusterId);
        if (null == clusterInfo) {
            return FrameworkResponse.frameworkResponse(1, "not found");
        }
        List<TableColumnModel> tableColumnModels = engineService.queryColumInfos(clusterInfo, tableName, keyword);
        OptionModel<TableColumnOptionModel> optionModel = new OptionModel<>();
        optionModel.setOptions(CollectionUtils.isEmpty(tableColumnModels)
                ? Collections.emptyList()
                : tableColumnModels.stream()
                .map(v -> {
                    TableColumnOptionModel tableColumnOptionModel = JsonUtils.transfrom(v, TableColumnOptionModel.class);
                    tableColumnOptionModel.setLabel(v.getName());
                    tableColumnOptionModel.setValue(v.getName());
                    return tableColumnOptionModel;
                })
                .collect(Collectors.toList()));
        return FrameworkResponse.extend(Response.ok(optionModel));
    }

}
