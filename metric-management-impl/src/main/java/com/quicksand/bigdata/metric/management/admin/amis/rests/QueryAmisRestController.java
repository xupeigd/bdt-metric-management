package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.engine.EngineService;
import com.quicksand.bigdata.metric.management.engine.impls.EngineServiceImpl;
import com.quicksand.bigdata.metric.management.utils.DatasetUtil;
import com.quicksand.bigdata.query.models.QueryRespModel;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class QueryAmisRestController
 *
 * @Author: page
 * @Date: 2025/3/15
 * @Description:
 */
@RequestMapping(Vars.PATH_ROOT + "/amis/query")
@RestController
public class QueryAmisRestController {

    @Resource
    DatasetDataManager datasetDataManager;
    @Resource
    EngineService engineService;

    @Data
    public static final class ColumnMeta {
        Integer index;
        String label;
        String name;
    }

    @Data
    public static final class DynamicTable {

        Long count;

        List<ColumnMeta> columns;

        List<Map<String, Object>> rows;

    }

    @GetMapping("/datasets/{datasetId}")
    public FrameworkResponse<DynamicTable, Void> datasetExploration(@PathVariable("datasetId") Integer datasetId) {
        DatasetDBVO dataset = datasetDataManager.findDatasetById(datasetId);
        if (null == dataset) {
            return FrameworkResponse.frameworkResponse(1, "dataset not found!");
        }
        List<String> fields = DatasetUtil.resolvingFields(dataset);
        DynamicTable dynamicTable = engineService.commonQuery(dataset.getCluster(),
                String.format("SELECT %s FROM %s LIMIT 20", StringUtils.collectionToCommaDelimitedString(fields), dataset.getTableName()),
                new EngineServiceImpl.Mapper<DynamicTable>() {
                    @Override
                    public DynamicTable applySuccess(QueryRespModel queryRespModel) {
                        DynamicTable dynamicTable = new DynamicTable();
                        List<ColumnMeta> columns = null != queryRespModel.getResultSet()
                                && !CollectionUtils.isEmpty(queryRespModel.getResultSet().getColumnMetas())
                                ? queryRespModel.getResultSet().getColumnMetas().stream()
                                .map(v -> {
                                    ColumnMeta columnMeta = new ColumnMeta();
                                    columnMeta.setIndex(v.getIndex());
                                    columnMeta.setLabel(v.getName());
                                    columnMeta.setName(v.getName());
                                    return columnMeta;
                                })
                                .collect(Collectors.toList())
                                : Collections.emptyList();
                        if (!CollectionUtils.isEmpty(columns)) {
                            dynamicTable.setColumns(columns);
                            Map<Integer, ColumnMeta> index2Column = columns.stream()
                                    .collect(Collectors
                                            .toMap(ColumnMeta::getIndex, v -> v));
                            List<Map<String, Object>> rows = new ArrayList<>();
                            int rowCount = queryRespModel.getResultSet().getColumns().get(0).size();
//                            dynamicTable.setCount((long) rowCount);
                            for (int i = 0; i < rowCount; i++) {
                                rows.add(new HashMap<>());
                            }
                            for (int i = 0; i < queryRespModel.getResultSet().getColumns().size(); i++) {
                                for (int j = 0; j < queryRespModel.getResultSet().getColumns().get(i).size(); j++) {
                                    Map<String, Object> objMap = rows.get(j);
                                    objMap.put(index2Column.get(i).name, queryRespModel.getResultSet().getColumns().get(i).get(j));
                                }
                            }
                            dynamicTable.setRows(rows);
                        }
                        return dynamicTable;
                    }
                });
        return FrameworkResponse.frameworkResponse(dynamicTable, null, 0, "not found!");
    }

}
