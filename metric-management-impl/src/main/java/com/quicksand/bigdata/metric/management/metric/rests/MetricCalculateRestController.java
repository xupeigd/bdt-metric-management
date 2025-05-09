package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.engine.EngineService;
import com.quicksand.bigdata.metric.management.engine.impls.EngineServiceImpl;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.CandidateValuePairModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatculateResponseModel;
import com.quicksand.bigdata.metric.management.metric.models.ResultSetModel;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.utils.SqlParserUtil;
import com.quicksand.bigdata.metric.management.yaml.services.ExplainService;
import com.quicksand.bigdata.query.consts.ResultMode;
import com.quicksand.bigdata.query.models.QueryRespModel;
import com.quicksand.bigdata.query.models.SqlColumnMetaModel;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.service.VarsSecurityUtilService;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlUtil;
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
import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MetricCalculateRestController
 *
 * @author page
 * @date 2022/8/25
 */
@Slf4j
@Validated
@RestController
@Tag(name = "指标配额Apis")
public class MetricCalculateRestController
        implements MetricCalculateRestService {

    @Resource
    MetricDataManager metricDataManager;
    @Resource
    MetricService metricService;
    @Resource
    EngineService engineService;
    @Resource
    ExplainService explainService;
    @Resource
    VarsSecurityUtilService varsSecurityUtilService;

    @Operation(description = "试算指标")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CALCULATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<MetricCatculateResponseModel> calculate(@Parameter(name = "指标Id")
                                                            @Min(value = 1L, message = "不存在的指标！")
                                                            @Max(value = Integer.MAX_VALUE, message = "不存在的指标！")
                                                            @PathVariable("metricId") int metricId) {
        MetricDBVO metric = metricDataManager.findByMetricId(metricId);
        if (null == metric) {
            return Response.response(HttpStatus.NOT_FOUND, "指标不存在或已被删除！");
        }
        String transformSql = metricService.getMetricQuerySql(metricId);
        //获取sql
        if (!StringUtils.hasText(transformSql)) {
            //不重新获取
            return Response.response(HttpStatus.INTERNAL_SERVER_ERROR, "指标状态异常：不存在或已被删除！");
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
        ResultSetModel resultSetModel = null;
        if (AuthUtil.hasAuthority("OP_METRICS_CALCULATE_ACTION") || varsSecurityUtilService.isAnonymousUser(AuthUtil.getAuthentication())) {
            resultSetModel = engineService.commonQuery(metric.getDataset().getCluster(),
                    String.format(" %s limit 10", transformSql.replaceAll("\\n", " ")),
                    new EngineServiceImpl.Mapper<ResultSetModel>() {

                        @Override
                        public ResultSetModel applySuccess(QueryRespModel r) {
                            return ResultSetModel.builder()
                                    .columnMetas(EngineServiceImpl.Mapper.coverColumnMetas(r))
                                    .state(r.getState().getCode())
                                    .msg(r.getResultSet().getMsg())
                                    .resultMode(r.getReq().getResultMode().getCode())
                                    .columns(r.getResultSet().getColumns())
                                    .rows(r.getResultSet().getRows())
                                    .build();
                        }

                        @Override
                        public ResultSetModel applyDefault(QueryRespModel r) {
                            return ResultSetModel.builder()
                                    .state(r.getState().getCode())
                                    .msg(r.getResultSet().getMsg())
                                    .build();
                        }
                    });
        }
        return Response.ok(MetricCatculateResponseModel.builder()
                .id(metricId)
                .sql(transformSql.replaceAll(" *\\n +,", ",\\\n "))
                .resultSet(resultSetModel)
                .build());
    }

    @Operation(description = "探测纬度候选值")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CALCULATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Override
    public Response<List<CandidateValuePairModel>> candidateValues(@Parameter(name = "指标Id")
                                                                   @Min(value = 1L, message = "不存在的指标！")
                                                                   @Max(value = Integer.MAX_VALUE, message = "不存在的指标！")
                                                                   @PathVariable("metricId") int metricId,
                                                                   @Parameter(name = "纬度，多选，半角逗号分隔")
                                                                   @Validated @NotEmpty(message = "探索纬度不能为空")
                                                                   @RequestParam("dimensions") List<String> dimensions) {
        MetricDBVO metric = metricDataManager.findByMetricId(metricId);
        if (null == metric) {
            return Response.response(HttpStatus.NOT_FOUND, "指标不存在或已被删除！");
        }
        String executeSql = explainService.expain2Sql(Collections.singletonList(JsonUtils.transfrom(metric, MetricVO.class)), dimensions, null, Collections.emptyList());
        log.info("candidateValues metricId:{}, dimensions:[{}],sql:[{}]", metricId, StringUtils.collectionToCommaDelimitedString(dimensions), executeSql);
        List<CandidateValuePairModel> candidateValuePairModels = engineService.commonQuery(metric.getDataset().getCluster(), executeSql, new EngineServiceImpl.Mapper<List<CandidateValuePairModel>>() {
            @Override
            public List<CandidateValuePairModel> applySuccess(QueryRespModel queryRespModel) {
                Map<String, CandidateValuePairModel> valuse = new HashMap<>();
                //读取头文件
                Map<Integer, SqlColumnMetaModel> columnMetas = queryRespModel.getResultSet().getColumnMetas().stream()
                        .filter(v -> !Objects.equals(metric.getEnName(), v.getName()))
                        .collect(Collectors.toMap(SqlColumnMetaModel::getIndex, Function.identity()));
                if (Objects.equals(ResultMode.Column, queryRespModel.getReq().getResultMode())) {
                    for (int i = 0; i < queryRespModel.getResultSet().getColumns().size(); i++) {
                        SqlColumnMetaModel sqlColumnMetaModel = columnMetas.get(i);
                        if (null != sqlColumnMetaModel) {
                            valuse.put(sqlColumnMetaModel.getName(), CandidateValuePairModel.builder()
                                    .name(sqlColumnMetaModel.getName())
                                    .values(new ArrayList<>(queryRespModel.getResultSet().getColumns()
                                            .get(i)
                                            .stream()
                                            .map(String::valueOf)
                                            .collect(Collectors.toSet())))
                                    .build());
                        }
                    }
                } else {
                    columnMetas.forEach((k, v) -> {
                        CandidateValuePairModel cpv = valuse.getOrDefault(v.getName(), CandidateValuePairModel.builder()
                                .name(v.getName())
                                .values(new ArrayList<>())
                                .build());
                        valuse.put(cpv.getName(), cpv);
                        for (List<?> row : queryRespModel.getResultSet().getRows()) {
                            String ret = String.valueOf(row.get(v.getIndex()));
                            if (!cpv.getValues().contains(ret)) {
                                cpv.getValues().add(ret);
                            }
                        }
                    });
                }
                return new ArrayList<>(valuse.values());
            }
        });
        if (null == candidateValuePairModels) {
            Response<List<CandidateValuePairModel>> response = Response.response(HttpStatus.INTERNAL_SERVER_ERROR, "查询引擎异常，请稍后重试。");
            response.setDebugMessage("QERN");
            response.setDebugMessage(executeSql);
            return response;
        }
        return Response.ok(candidateValuePairModels);
    }




}
