package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetColumnDBVO;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetColumnModel;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetModel;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetModifyModel;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetOverviewModel;
import com.quicksand.bigdata.metric.management.datasource.rests.DatasetManageRestService;
import com.quicksand.bigdata.metric.management.datasource.rests.DatasetRestService;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据集AMIS接口控制器
 *
 * @Author: page
 * @Date: 2025/3/13
 * @Description: 提供数据集的CRUD操作和相关查询功能
 */
@RestController
@Tag(name = "数据集管理", description = "数据集的增删改查和相关操作接口")
@RequestMapping(Vars.PATH_ROOT + "/amis/datasource/datasets")
public class DatasetAmisRestController {

    @Resource
    DatasetRestService datasetRestService;
    @Resource
    DatasetManageRestService datasetManageRestService;
    @Resource
    DatasetDataManager datasetDataManager;

    @Operation(summary = "获取数据集列表", description = "分页获取所有数据集信息列表，支持多种过滤条件")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = PageImpl.class))),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @GetMapping
    public FrameworkResponse<PageImpl<DatasetOverviewModel>, Void> listDatasets(@Min(1) @RequestParam(name = "page", required = false, defaultValue = "1")
                                                                                @Parameter(description = "页码，默认为1") Integer pageNo,
                                                                                @Min(1) @RequestParam(name = "perPage", required = false, defaultValue = "20")
                                                                                @Parameter(description = "每页记录数，默认为20") Integer pageSize,
                                                                                @RequestParam(name = "nameKeyword", required = false, defaultValue = "")
                                                                                @Parameter(description = "数据集名称关键字") String nameKeyword,
                                                                                @RequestParam(name = "clusterIds", required = false)
                                                                                @Parameter(description = "集群ID列表(多个采用半角逗号分隔)") List<Integer> clusterIds,
                                                                                @RequestParam(name = "clusterNameKeyword", required = false, defaultValue = "")
                                                                                @Parameter(description = "集群名称关键字") String clusterNameKeyword,
                                                                                @RequestParam(name = "ownerIds", required = false)
                                                                                @Parameter(description = "负责人ID列表(多个采用半角逗号分隔)") List<Integer> ownerIds,
                                                                                @RequestParam(name = "ownerNameKeyword", required = false, defaultValue = "")
                                                                                @Parameter(description = "负责人名称关键字(可模糊搜索)") String ownerNameKeyword) {
        Response<PageImpl<DatasetOverviewModel>> pageResponse = datasetRestService.queryDatasets(pageNo, pageSize, nameKeyword, clusterIds, clusterNameKeyword, ownerIds, ownerNameKeyword);
        return FrameworkResponse.extend(pageResponse);
    }

    @Operation(summary = "创建数据集", description = "创建新的数据集记录")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = DatasetOverviewModel.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @Transactional
    @PostMapping
    public FrameworkResponse<DatasetOverviewModel, Void> createDatasets(
            @Parameter(description = "数据集修改模型", required = true) @RequestBody DatasetModifyModel model) {
        return FrameworkResponse.extend(datasetManageRestService.createDataset(model));
    }

    @Operation(summary = "按ID查找数据集", description = "根据数据集ID查询单个数据集的详细信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = DatasetModel.class))),
            @ApiResponse(responseCode = "404", description = "数据集不存在"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @GetMapping("/{id}")
    public FrameworkResponse<DatasetModel, Void> queryDataset(
            @Parameter(description = "数据集ID", required = true) @PathVariable("id") Integer id) {
        return FrameworkResponse.extend(datasetRestService.queryDataset(id));
    }

    @Operation(summary = "删除数据集", description = "根据数据集ID删除数据集信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @Transactional
    @DeleteMapping("/{id}")
    public FrameworkResponse<Integer, Void> deleteDataset(
            @Parameter(description = "数据集ID", required = true) @PathVariable("id") Integer id) {
        try {
            Response<Void> voidResponse = datasetManageRestService.deleteDataset(id);
            if (Objects.equals("200", voidResponse.getCode())) {
                return FrameworkResponse.frameworkResponse(id, null, 0, "success");
            } else {
                return FrameworkResponse.frameworkResponse(id, null, 1, "删除失败!" + voidResponse.getMsg());
            }
        } catch (Exception e) {
            return FrameworkResponse.frameworkResponse(id, null, 1, e.getMessage());
        }
    }

    /**
     * 编辑dataset
     * （成功则回显）
     *
     * @param datasetId 数据集合Id
     * @param model     修改参数
     * @return instance of DatasetOverviewModel
     */
    @Operation(summary = "修改数据集", description = "根据数据集ID更新数据集信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = DatasetOverviewModel.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "数据集不存在"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @Transactional
    @PutMapping("/{datasetId}")
    public FrameworkResponse<DatasetOverviewModel, Void> modifyDataset(
            @Parameter(description = "数据集ID", required = true)
            @PathVariable("datasetId") @Min(value = 0L, message = "不存在的数据集")
            @Max(value = Integer.MAX_VALUE, message = "不存在的数据集") int datasetId,
            @Parameter(description = "数据集修改模型", required = true)
            @RequestBody @Validated({Update.class}) DatasetModifyModel model) {
        return FrameworkResponse.extend(datasetManageRestService.modifyDataset(datasetId, model));
    }

    @Data
    public static final class DatasetColumnOptionModel extends DatasetColumnModel {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String label;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String value;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String description;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String aggregationType;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        DatasetColumnModel columnModel;
    }

    @Operation(summary = "获取数据集字段列表", description = "根据数据集ID查询其包含的字段列表，支持多种过滤条件")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "数据集不存在"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @GetMapping("/{datasetId}/columns")
    public FrameworkResponse<List<DatasetColumnOptionModel>, Void> queryColumns(
            @Parameter(description = "数据集ID", required = true) @PathVariable("datasetId") int datasetId,
            @RequestParam(name = "nameKeyword", required = false, defaultValue = "")
            @Parameter(description = "字段名称关键字，可选") String nameKeyword,
            @RequestParam(name = "dataTypes", required = false, defaultValue = "")
            @Parameter(description = "数据类型(可选，多个使用半角逗号分隔)") String dataTypes,
            @RequestParam(name = "selectedColumns", required = false)
            @Parameter(description = "已选择的字段列表") List<String> selectedColumns,
            @RequestParam(name = "selectType", required = false)
            @Parameter(description = "选择类型，1表示维度，2表示度量") Integer selectType) {
        DatasetDBVO dataset = datasetDataManager.findDatasetById(datasetId);
        if (null == dataset) {
            return FrameworkResponse.frameworkResponse(1, "无效的的数据集! ");
        }
        List<String> reqTypes = StringUtils.hasText(dataTypes)
                ? new ArrayList<>(Arrays.stream(dataTypes.split(","))
                .filter(StringUtils::hasText)
                .map(String::toUpperCase)
                .collect(Collectors.toSet()))
                : new ArrayList<>();
        List<DatasetColumnDBVO> columns = dataset.getColumns().stream()
                .filter(v -> Objects.equals(DataStatus.ENABLE, v.getIncluded()))
                .filter(v -> CollectionUtils.isEmpty(selectedColumns) || selectedColumns.contains(v.getName()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(columns)) {
            columns = columns.stream()
                    .filter(v -> !StringUtils.hasText(nameKeyword) || v.getName().contains(nameKeyword))
                    .filter(v -> reqTypes.isEmpty() || reqTypes.stream().anyMatch(v.getType().toUpperCase()::contains))
                    .collect(Collectors.toList());
        }
        DatasetOverviewModel transfrom = JsonUtils.transfrom(dataset, DatasetOverviewModel.class);
        return FrameworkResponse.frameworkResponse(columns.stream()
                .map(v -> JsonUtils.transfrom(v, DatasetColumnOptionModel.class))
                .peek(v -> v.setDataset(transfrom))
                .peek(v -> v.setLabel(v.getName()))
                .peek(v -> v.setValue(v.getName()))
                .peek(v -> {
                    if (null != selectType) {
                        v.setDescription(String.format("%s_%s%d", v.getName(), Objects.equals(1, selectType) ? "dim" : "mea", v.getSerial()));
                        // todo 可用增加函数默认逻辑
                        v.setAggregationType(Objects.equals(1, selectType) ? "" : "SUM");
                        DatasetColumnModel columnModel = new DatasetColumnModel();
                        columnModel.setName(v.getName());
                        columnModel.setType(v.getType());
                        columnModel.setDataset(JsonUtils.transfrom(dataset, DatasetOverviewModel.class));
                        v.setColumnModel(columnModel);
                    }
                })
                .collect(Collectors.toList()), null, 0, "success !");
    }

}
