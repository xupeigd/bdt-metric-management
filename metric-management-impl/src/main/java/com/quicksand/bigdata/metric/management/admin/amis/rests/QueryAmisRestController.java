package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.engine.EngineService;
import com.quicksand.bigdata.metric.management.engine.impls.EngineServiceImpl;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.utils.DatasetUtil;
import com.quicksand.bigdata.metric.management.utils.SqlParserUtil;
import com.quicksand.bigdata.query.models.QueryRespModel;
import com.quicksand.bigdata.vars.security.service.VarsSecurityUtilService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Resource
    MetricDataManager metricDataManager;
    @Resource
    MetricService metricService;
    @Resource
    VarsSecurityUtilService varsSecurityUtilService;
    @Value("${metricflow.enable}")
    boolean metricflowEnable;

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

    public static final class DynamicTableMapper
            extends EngineServiceImpl.Mapper<DynamicTable> {

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

        @Override
        public DynamicTable applyDefault(QueryRespModel r) {
            return new DynamicTable();
        }

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
                new DynamicTableMapper());
        return FrameworkResponse.frameworkResponse(dynamicTable, null, 0, "not found!");
    }


    @PreAuthorize("hasAuthority('OP_METRICS_CALCULATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @GetMapping("/metrics/{metricId}/preview")
    public FrameworkResponse<DynamicTable, Void> metricPreview(@Parameter(name = "指标Id")
                                                               @Min(value = 1L, message = "不存在的指标！")
                                                               @Max(value = Integer.MAX_VALUE, message = "不存在的指标！")
                                                               @PathVariable("metricId") int metricId) {
        MetricDBVO metric = metricDataManager.findByMetricId(metricId);
        if (null == metric) {
            return FrameworkResponse.frameworkResponse(1, "指标不存在或已被删除！");
        }
        String transformSql = metricService.getMetricQuerySql(metricId);
        //获取sql
        if (!StringUtils.hasText(transformSql)) {
            if (Objects.equals(false, metricflowEnable)) {
                transformSql = String.format("select 1+1 as %s", metric.getEnName());
            } else {
                //不重新获取
                return FrameworkResponse.frameworkResponse(1, "指标状态异常：不存在或已被删除！");
            }
        }
        //转换sql db
        String mayName0 = String.format(" %s ", metric.getDataset().getTableName());
        String mayName1 = String.format(" `%s` ", metric.getDataset().getTableName());
        String completeName = String.format(" %s.%s ", metric.getDataset().getCluster().getDefaultDatabase(), metric.getDataset().getTableName());
        if (transformSql.contains(mayName0)
                || transformSql.contains(mayName1)) {
            transformSql = transformSql.replace(mayName0, completeName).replace(mayName1, completeName);
        }
        transformSql = SqlParserUtil.transformCalculateSql(transformSql, completeName);
        DynamicTable dynamicTable = null;
        if (AuthUtil.hasAuthority("OP_METRICS_CALCULATE_ACTION") || varsSecurityUtilService.isAnonymousUser(AuthUtil.getAuthentication())) {
            dynamicTable = engineService.commonQuery(metric.getDataset().getCluster(),
                    String.format(" %s limit 10", transformSql.replaceAll("\\n", " ")),
                    new DynamicTableMapper());
        }
        return FrameworkResponse.frameworkResponse(dynamicTable, null, 0, "sucess !");
    }

}
