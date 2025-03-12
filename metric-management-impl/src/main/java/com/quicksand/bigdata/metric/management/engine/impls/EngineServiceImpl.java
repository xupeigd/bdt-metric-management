package com.quicksand.bigdata.metric.management.engine.impls;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.quicksand.bigdata.metric.management.consts.ColumnType;
import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.models.TableColumnModel;
import com.quicksand.bigdata.metric.management.datasource.vos.ClusterInfoVO;
import com.quicksand.bigdata.metric.management.engine.EngineService;
import com.quicksand.bigdata.metric.management.metric.models.ResultSetModel;
import com.quicksand.bigdata.query.consts.DsType;
import com.quicksand.bigdata.query.consts.JobState;
import com.quicksand.bigdata.query.consts.QueryMode;
import com.quicksand.bigdata.query.consts.ResultMode;
import com.quicksand.bigdata.query.models.ConnectionInfoModel;
import com.quicksand.bigdata.query.models.QueryReqModel;
import com.quicksand.bigdata.query.models.QueryRespModel;
import com.quicksand.bigdata.query.models.SqlColumnMetaModel;
import com.quicksand.bigdata.query.rests.QueryRestService;
import com.quicksand.bigdata.vars.concurrents.TraceFuture;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * EngineServiceImpl
 *
 * @author page
 * @date 2022/8/12
 */
@Slf4j
@Service
public class EngineServiceImpl
        implements EngineService {

    public static final String SQL_SHOW_TABLES = "SHOW TABLES";
    public static final String SQL_DESC_TABLE = "select COLUMN_NAME,COLUMN_TYPE,COLUMN_KEY,COLUMN_COMMENT from INFORMATION_SCHEMA.Columns where table_name='%s' and table_schema='%s'";

    public static final LoadingCache<String, List<TableColumnModel>> COLUMNS_CACHES = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<TableColumnModel>>() {
                @Override
                public List<TableColumnModel> load(String key) throws Exception {
                    return Collections.emptyList();
                }
            });
    /**
     * 表缓存
     */
    private static final LoadingCache<String, List<String>> TABLES_CACHES = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) {
                    return Collections.emptyList();
                }
            });

    @Value("${bdt.management.engine.result.mode:0}")
    int resultMode;
    @Value("${bdt.management.engine.request.mode:0}")
    int requestMode;

    @Resource
    QueryRestService queryRestService;

    private static ConnectionInfoModel cover2ConnectionInfo(ClusterInfoDBVO clusterInfo) {
        return ConnectionInfoModel.builder()
                .name(clusterInfo.getName())
                .userName(clusterInfo.getUserName())
                .password(clusterInfo.getPassword())
                .defaultDatabase(clusterInfo.getDefaultDatabase())
                .address(clusterInfo.getAddress())
                .type(DsType.findByFlag(clusterInfo.getType()))
                .defaultSchema(clusterInfo.getDefaultSchema())
                .comment(clusterInfo.getComment())
                .build();
    }

    private static ConnectionInfoModel cover2ConnectionInfo(ClusterInfoVO clusterInfo) {
        return ConnectionInfoModel.builder()
                .name(clusterInfo.getName())
                .userName(clusterInfo.getUserName())
                .password(clusterInfo.getPassword())
                .defaultDatabase(clusterInfo.getDefaultDatabase())
                .address(clusterInfo.getAddress())
                .type(DsType.findByFlag(clusterInfo.getType()))
                .defaultSchema(clusterInfo.getDefaultSchema())
                .comment(clusterInfo.getComment())
                .build();
    }

    private static boolean isEndPoint(QueryRespModel resp) {
        return JobState.Cancel.getCode() <= resp.getState().getCode();
    }

    private static boolean querySuccess(QueryRespModel resp) {
        return null != resp
                && Objects.equals(JobState.Success, resp.getState())
                && null != resp.getResultSet();
    }

    private static boolean resultExist(QueryRespModel resp) {
        return querySuccess(resp)
                && (!CollectionUtils.isEmpty(resp.getResultSet().getColumns())
                || !CollectionUtils.isEmpty(resp.getResultSet().getRows()));
    }

    @SuppressWarnings("UnusedReturnValue")
    private QueryReqModel supplyCommonProperties(QueryReqModel queryReqModel) {
        queryReqModel.setResultMode(1 == resultMode ? ResultMode.Row : ResultMode.Column);
        queryReqModel.setMode(0 == requestMode ? QueryMode.Async : QueryMode.Sync);
        queryReqModel.setSyncMills(30 * 1000L);
        queryReqModel.setAsyncMills(30 * 1000L);
        return queryReqModel;
    }

    private QueryRespModel queryAndWait(QueryReqModel queryReq) throws Exception {
        TraceFuture.run(() -> log.info("EngineService queryAndWait start ! request:{}`", JsonUtils.toJsonString(queryReq)));
        Response<QueryRespModel> queryResp = queryRestService.query(queryReq);
        if (!Objects.equals(String.valueOf(HttpStatus.OK.value()), queryResp.getCode())) {
            throw new Exception("queryRestService not aviable !");
        }
        if (Objects.equals(QueryMode.Sync, queryReq.getMode())) {
            TraceFuture.run(() -> log.info("EngineService queryAndWait complete ! resp:{}`", JsonUtils.toJsonString(queryResp.getData())));
            return queryResp.getData();
        } else {
            int maxReq = 10;
            Long asyncMills = queryReq.getAsyncMills();
            QueryRespModel ret = null;
            if (10 * 1000L > asyncMills) {
                Try.run(() -> TimeUnit.MILLISECONDS.sleep(asyncMills));
                ret = queryRestService.getResp(queryResp.getData().getId()).getData();
            } else {
                long preStepMills = asyncMills / maxReq;
                for (int si = 0; si < maxReq; si++) {
                    Try.run(() -> TimeUnit.MILLISECONDS.sleep(preStepMills));
                    Response<QueryRespModel> response = queryRestService.getResp(queryResp.getData().getId());
                    if (Objects.equals(String.valueOf(HttpStatus.OK.value()), response.getCode())) {
                        QueryRespModel resp = response.getData();
                        ret = resp;
                        if (isEndPoint(resp)) {
                            break;
                        }
                    }
                }
            }
            QueryRespModel finalRet = ret;
            TraceFuture.run(() -> log.info("EngineService queryAndWait complete ! resp:{}`", JsonUtils.toJsonString(finalRet)));
            return ret;
        }
    }

    @Override
    public List<String> queryTables(ClusterInfoDBVO clusterInfo) throws Exception {
        List<String> caches = TABLES_CACHES.get(String.valueOf(clusterInfo.getId()));
        if (CollectionUtils.isEmpty(caches)) {
            QueryReqModel queryReq = QueryReqModel.builder()
                    .connectionInfo(cover2ConnectionInfo(clusterInfo))
                    .templateSql(SQL_SHOW_TABLES)
                    .build();
            supplyCommonProperties(queryReq);
            queryReq.setMode(QueryMode.Sync);
            QueryRespModel respModel = queryAndWait(queryReq);
            Assert.notNull(respModel, "查询引擎调用异常！");
            Assert.isTrue(Objects.equals(JobState.Success, respModel.getState()), "无法连接数据集群！");
            if (resultExist(respModel)) {
                if (Objects.equals(ResultMode.Column, queryReq.getResultMode())) {
                    caches = respModel.getResultSet().getColumns().get(0)
                            .stream()
                            .map(v -> (String) v)
                            .collect(Collectors.toList());
                } else {
                    caches = respModel.getResultSet().getRows()
                            .stream()
                            .map(l -> CollectionUtils.isEmpty(l) ? "" : (String) l.get(0))
                            .filter(StringUtils::hasText)
                            .collect(Collectors.toList());
                }
            }
            if (!CollectionUtils.isEmpty(caches)) {
                synchronized (TABLES_CACHES) {
                    if (CollectionUtils.isEmpty(TABLES_CACHES.get(String.valueOf(clusterInfo.getId())))) {
                        TABLES_CACHES.put(String.valueOf(clusterInfo.getId()), caches);
                    } else {
                        caches = TABLES_CACHES.get(String.valueOf(clusterInfo.getId()));
                    }
                }
            }
        }
        return caches;
    }

    private int nameIndex(List<SqlColumnMetaModel> metas, DsType dsType) {
        if (!CollectionUtils.isEmpty(metas)) {
            for (SqlColumnMetaModel meta : metas) {
                if ((Objects.equals(DsType.Mysql, dsType)
                        || Objects.equals(DsType.StarRocks, dsType)
                        || Objects.equals(DsType.Doris, dsType))
                        && ("Field".equalsIgnoreCase(meta.getName())
                        || "COLUMN_NAME".equalsIgnoreCase(meta.getName()))) {
                    return meta.getIndex();
                }
            }
        }
        return -1;
    }

    private int typeIndex(List<SqlColumnMetaModel> metas, DsType dsType) {
        if (!CollectionUtils.isEmpty(metas)) {
            for (SqlColumnMetaModel meta : metas) {
                if ((Objects.equals(DsType.Mysql, dsType)
                        || Objects.equals(DsType.StarRocks, dsType)
                        || Objects.equals(DsType.Doris, dsType))
                        && ("Type".equalsIgnoreCase(meta.getName())
                        || "COLUMN_TYPE".equalsIgnoreCase(meta.getName()))) {
                    return meta.getIndex();
                }
            }
        }
        return -1;
    }

    private int columnTypeIndex(List<SqlColumnMetaModel> metas, DsType dsType) {
        if (!CollectionUtils.isEmpty(metas)) {
            for (SqlColumnMetaModel meta : metas) {
                if ((Objects.equals(DsType.Mysql, dsType)
                        || Objects.equals(DsType.StarRocks, dsType)
                        || Objects.equals(DsType.Doris, dsType))
                        && ("Key".equalsIgnoreCase(meta.getName())
                        || "COLUMN_KEY".equalsIgnoreCase(meta.getName()))) {
                    return meta.getIndex();
                }
            }
        }
        return -1;
    }

    private int columnCommentIndex(List<SqlColumnMetaModel> metas, DsType dsType) {
        if (!CollectionUtils.isEmpty(metas)) {
            for (SqlColumnMetaModel meta : metas) {
                if ((Objects.equals(DsType.Mysql, dsType)
                        || Objects.equals(DsType.StarRocks, dsType)
                        || Objects.equals(DsType.Doris, dsType))
                        && ("COLUMN_COMMENT".equalsIgnoreCase(meta.getName()))) {
                    return meta.getIndex();
                }
            }
        }
        return -1;
    }

    private ColumnType cover2ColumnType(String keyType, DsType dsType) {
        if (Objects.equals(DsType.Mysql, dsType)) {
            switch (keyType) {
                case "PRI":
                    return ColumnType.Primary;
                case "UNI":
                    return ColumnType.Unique;
                case "MUL":
                    return ColumnType.Foreign;
                default:
                    return ColumnType.Normal;
            }
        }
        return ColumnType.Normal;
    }

    @Override
    public List<TableColumnModel> queryColumInfos(ClusterInfoDBVO clusterInfo, String tableName, String keyword) throws Exception {
        String cacheKey = String.format("%d_%s_%s", clusterInfo.getId(), tableName, StringUtils.hasText(keyword) ? keyword : "");
        List<TableColumnModel> caches = COLUMNS_CACHES.get(cacheKey);
        if (!CollectionUtils.isEmpty(caches)) {
            return caches;
        } else {
            QueryReqModel queryReq = QueryReqModel.builder()
                    .connectionInfo(cover2ConnectionInfo(clusterInfo))
                    .templateSql(String.format(SQL_DESC_TABLE, tableName, clusterInfo.getDefaultDatabase()))
                    .build();
            supplyCommonProperties(queryReq);
            queryReq.setMode(QueryMode.Sync);
            QueryRespModel respModel = queryAndWait(queryReq);
            if (resultExist(respModel)) {
                assert null != respModel;
                // 为了兼容性，只取name与类型，其他忽略
                com.quicksand.bigdata.query.models.ResultSetModel resultSet = respModel.getResultSet();
                assert null != resultSet;
                DsType curDsType = queryReq.getConnectionInfo().getType();
                int nameIndex = nameIndex(resultSet.getColumnMetas(), curDsType);
                int typeIndex = typeIndex(resultSet.getColumnMetas(), curDsType);
                int columnTypeIndex = columnTypeIndex(resultSet.getColumnMetas(), curDsType);
                int columnCommentIndex = columnCommentIndex(resultSet.getColumnMetas(), curDsType);
                if (0 > nameIndex
                        || 0 > typeIndex
                        || 0 > columnTypeIndex
                        || 0 > columnCommentIndex) {
                    throw new Exception("db/dwh not support ! ");
                }
                caches = new ArrayList<>();
                if (Objects.equals(ResultMode.Column, queryReq.getResultMode())) {
                    List<?> nameValues = resultSet.getColumns().get(nameIndex);
                    List<?> typeValues = resultSet.getColumns().get(typeIndex);
                    List<?> columnTypeValues = resultSet.getColumns().get(columnTypeIndex);
                    List<?> columnCommentValues = resultSet.getColumns().get(columnCommentIndex);
                    for (int i = 0; i < nameValues.size(); i++) {
                        String columnComment = String.valueOf(columnCommentValues.get(i));
                        TableColumnModel tableColumnModel = TableColumnModel.builder()
                                .name(String.valueOf(nameValues.get(i)))
                                .type(String.valueOf(typeValues.get(i)))
                                .columnType(cover2ColumnType(String.valueOf(columnTypeValues.get(i)), curDsType))
                                .serial(i + 1)
                                .comment(!StringUtils.hasText(columnComment) || "null".equalsIgnoreCase(columnComment) ? "" : columnComment)
                                .build();
                        caches.add(tableColumnModel);
                    }
                } else {
                    for (int i = 0; i < resultSet.getRows().size(); i++) {
                        List<?> rows = resultSet.getRows().get(i);
                        String columnComment = String.valueOf(rows.get(columnCommentIndex));
                        TableColumnModel tableColumnModel = TableColumnModel.builder()
                                .name(String.valueOf(rows.get(nameIndex)))
                                .type(String.valueOf(rows.get(typeIndex)))
                                .columnType(cover2ColumnType(String.valueOf(rows.get(columnTypeIndex)), curDsType))
                                .serial(i + 1)
                                .comment(!StringUtils.hasText(columnComment) || "null".equalsIgnoreCase(columnComment) ? "" : columnComment)
                                .build();
                        caches.add(tableColumnModel);
                    }
                }
                if (StringUtils.hasText(keyword)) {
                    caches = caches.stream()
                            .filter(v -> v.getName().contains(keyword))
                            .collect(Collectors.toList());
                }
                if (!CollectionUtils.isEmpty(caches)) {
                    synchronized (COLUMNS_CACHES) {
                        if (CollectionUtils.isEmpty(COLUMNS_CACHES.get(cacheKey))) {
                            COLUMNS_CACHES.put(cacheKey, caches);
                        } else {
                            caches = COLUMNS_CACHES.get(cacheKey);
                        }
                    }
                }
            }
            return caches;
        }
    }

    @Override
    public <T> T commonQuery(ClusterInfoDBVO clusterInfo, String sql, Mapper<T> mapper) {
        return Try.of(() -> {
                    QueryReqModel queryReq = QueryReqModel.builder()
                            .connectionInfo(cover2ConnectionInfo(clusterInfo))
                            .templateSql(sql)
                            .build();
                    supplyCommonProperties(queryReq);
                    QueryRespModel resp = queryAndWait(queryReq);
                    return mapper.apply(resp);
                })
                .onFailure(ex -> log.error(String.format("commonQuery error ! sql:【%s】", sql), ex))
                .getOrNull();
    }

    @Override
    public <T> T commonQuery(ClusterInfoVO clusterInfo, String sql, Mapper<T> mapper) {
        return Try.of(() -> {
                    QueryReqModel queryReq = QueryReqModel.builder()
                            .connectionInfo(cover2ConnectionInfo(clusterInfo))
                            .templateSql(sql)
                            .build();
                    supplyCommonProperties(queryReq);
                    QueryRespModel resp = queryAndWait(queryReq);
                    return mapper.apply(resp);
                })
                .onFailure(ex -> log.error(String.format("commonQuery error ! sql:【%s】", sql), ex))
                .getOrNull();
    }

    public abstract static class Mapper<R>
            implements Function<QueryRespModel, R> {

        /**
         * 转换column meta数据
         *
         * @param queryRespModel 查询响应
         * @return list of ResultSetModel.ColumnMetaModel
         */
        public static List<ResultSetModel.ColumnMetaModel> coverColumnMetas(QueryRespModel queryRespModel) {
            return queryRespModel.getResultSet().getColumnMetas()
                    .stream()
                    .map(v -> JsonUtils.transfrom(v, ResultSetModel.ColumnMetaModel.class))
                    .collect(Collectors.toList());
        }

        public abstract R applySuccess(QueryRespModel queryRespModel);

        public R applyDefault(QueryRespModel queryRespModel) {
            return null;
        }

        @Override
        public R apply(QueryRespModel queryRespModel) {
            if (resultExist(queryRespModel)) {
                return applySuccess(queryRespModel);
            }
            return applyDefault(queryRespModel);
        }
    }

}
