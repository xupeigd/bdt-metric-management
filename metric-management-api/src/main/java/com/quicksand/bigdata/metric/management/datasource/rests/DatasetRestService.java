package com.quicksand.bigdata.metric.management.datasource.rests;

import com.quicksand.bigdata.metric.management.datasource.models.*;
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
 * DatasetService
 *
 * @author page
 * @date 2022/7/28
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.DatasetService:}",
        contextId = "DatasetService")
public interface DatasetRestService {


    /**
     * 获取数据集列表
     *
     * @param nameKeyword 名称关键字
     * @param clusterIds  集群id
     * @return list of DatasetOverviewModel
     */
    @GetMapping("/datasource/datasets")
    Response<PageImpl<DatasetOverviewModel>> queryDatasets(@Min(1) @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                           @Min(1) @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                           @RequestParam(name = "nameKeyword", required = false, defaultValue = "")
                                                           @Parameter(name = "nameKeyword", description = "名称关键字") String nameKeyword,
                                                           @RequestParam(name = "clusterIds", required = false)
                                                           @Parameter(name = "clusterIds", description = "集群Ids(多个采用半角逗号分隔)") List<Integer> clusterIds,
                                                           @RequestParam(name = "clusterNameKeyword", required = false, defaultValue = "")
                                                           @Parameter(name = "clusterNameKeyword", description = "集群名称") String clusterNameKeyword,
                                                           @RequestParam(name = "ownerIds", required = false)
                                                           @Parameter(name = "ownerIds", description = "负责人ids(多个采用半角逗号分隔)") List<Integer> ownerIds,
                                                           @RequestParam(name = "ownerNameKeyword", required = false, defaultValue = "")
                                                           @Parameter(name = "ownerNameKeyword", description = "负责人名称关键字(可模糊搜索)") String ownerNameKeyword);


    /**
     * 获取单个dataset的信息
     * （与概览一样样）
     *
     * @param datasetId dataset id
     * @return instance of DatasetModel / null
     */
    @GetMapping("/datasource/datasets/{datasetId}")
    Response<DatasetModel> queryDataset(@PathVariable("datasetId") int datasetId);


    /**
     * 获取数据集的字段信息
     *
     * @param datasetId   数据集id
     * @param nameKeyword 字段名称关键字
     * @param dataTypes   字段类型
     * @return list of DatasetColumnModel
     */
    @GetMapping("/datasource/datasets/{datasetId}/columns")
    Response<List<DatasetColumnModel>> queryColumnsTypes(@PathVariable("datasetId") int datasetId,
                                                         @RequestParam(name = "nameKeyword", required = false, defaultValue = "")
                                                         @Parameter(name = "字段名称关键字，可选") String nameKeyword,
                                                         @RequestParam(name = "dataTypes", required = false, defaultValue = "")
                                                         @Parameter(name = "数据类型(可选，多个使用半角逗号分隔)") String dataTypes);

    /**
     * 获取数据集集类型
     *
     * @param datasetId 数据集合id
     * @return list of ColumnTypeModel
     */
    @GetMapping("/datasource/datasets/{datasetId}/columns/types")
    Response<List<ColumnTypeModel>> queryColumnsTypes(@PathVariable("datasetId") int datasetId);

    /**
     * 查询数据集的yaml model
     *
     * @param datasetId 数据集id
     * @return instance of YamlViewModel
     */
    @GetMapping("/datasource/datasets/{datasetId}/yaml")
    Response<YamlViewModel> queryDatasetYamlModel(@PathVariable("datasetId") @Min(value = 1L, message = "无效的的数据集")
                                                  @Max(value = Integer.MAX_VALUE, message = "无效的的数据集") int datasetId);


}
