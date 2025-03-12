package com.quicksand.bigdata.metric.management.yaml.services.impls;

import com.quicksand.bigdata.metric.management.apis.models.ConditionModel;
import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.consts.IdentifierType;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.datasource.vos.IdentifierVO;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.services.MetricTransformService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricDimensionVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.utils.MetricMysqlSqlDialect;
import com.quicksand.bigdata.metric.management.yaml.dms.SegmentDataManager;
import com.quicksand.bigdata.metric.management.yaml.services.ExplainService;
import com.quicksand.bigdata.metric.management.yaml.services.YamlService;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.ValidationException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ExplainSpliceServiceImpl
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/12/15 11:54
 * @description
 */
@Slf4j
@Service("ExplainSpliceServiceImpl")
@Primary
public class ExplainSpliceServiceImpl implements ExplainService {

    @Value("${vars.metric.join.enable:false}")
    boolean joinEnable;

    @Resource
    MetricFileService metricFileService;
    @Resource
    SegmentDataManager segmentDataManager;
    @Resource
    DatasetDataManager datasetDataManager;
    @Resource
    MetricService metricService;
    @Resource
    MetricTransformService metricTransformService;
    @Resource
    YamlService yamlService;

    /**
     * 校验的临时结果
     */
    ThreadLocal<Map<String, Boolean>> validationResult = new ThreadLocal<>();

    @Override
    public String flag() {
        return "Splice";
    }

    @SneakyThrows
    @Override
    public String expain2Sql(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) {
        if (metrics.stream().allMatch(v -> validation(v, v.getDataset(), dimensions, condition, sorts))) {
            return Try.of(() -> explain(metrics, dimensions, condition, sorts))
                    .andFinally(() -> validationResult.remove())
                    .get();
        }
        return null;
    }

    @Override
    public boolean validation(MetricVO metric, DatasetVO dataset, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) {
        String vrk = String.format("%d:%d:%s:%s:%s", metric.getId(), dataset.getId(),
                DigestUtils.md5DigestAsHex(StringUtils.collectionToCommaDelimitedString(dimensions).getBytes()),
                null == condition ? "" : DigestUtils.md5DigestAsHex(JsonUtils.toJson(condition).getBytes()),
                CollectionUtils.isEmpty(sorts) ? "" : DigestUtils.md5DigestAsHex(JsonUtils.toJson(sorts).getBytes()));
        Map<String, Boolean> vrc = validationResult.get();
        if (null == vrc) {
            synchronized (this) {
                vrc = validationResult.get();
                if (null == vrc) {
                    vrc = new HashMap<>();
                    validationResult.set(vrc);
                }
            }
        }
        Boolean validation = vrc.get(vrk);
        if (null != validation) {
            return Objects.equals(true, validation);
        }
        Map<String, Boolean> finalVrc = vrc;
        //是否支持外表
        List<IdentifierVO> foreignKeys = CollectionUtils.isEmpty(dataset.getIdentifiers())
                ? Collections.emptyList()
                : dataset.getIdentifiers().stream()
                .filter(v -> Objects.equals(IdentifierType.Foreign, v.getType()))
                .collect(Collectors.toList());
        Map<String, MetricDimensionVO> name2Dimenisons = metric.getDimensions().stream()
                .collect(Collectors.toMap(MetricDimensionVO::getName, Function.identity()));
        boolean hasCrossDataset = dimensions.stream().anyMatch(v -> v.contains("__"));
        if (hasCrossDataset && foreignKeys.isEmpty()) {
            throw new ValidationException("不支持关联纬度！");
        }
        if (!joinEnable
                && dimensions.stream().anyMatch(v -> v.contains("__"))) {
            //v1.1版本不开放
            throw new ValidationException("不支持关联纬度！");
        }
        //先验证正常的纬度
        List<String> notExistDimensions = dimensions.stream()
                .filter(v -> !v.contains("__"))
                .filter(v -> !name2Dimenisons.containsKey(v))
                .collect(Collectors.toList());
        if (!notExistDimensions.isEmpty()) {
            throw new ValidationException(String.format("纬度[%s]不存在！", StringUtils.collectionToCommaDelimitedString(notExistDimensions)));
        }
        if (null != condition) {
            //验证条件纬度
            if (!ConditionModel.validation(condition)) {
                throw new ValidationException("错误的condition！");
            }
            List<String> notExistConditionDimensions = ConditionModel.resolveNames(condition).stream()
                    .filter(v -> !name2Dimenisons.containsKey(v))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(notExistConditionDimensions)) {
                throw new ValidationException(String.format("condition中的纬度[%s]不存在！", StringUtils.collectionToCommaDelimitedString(notExistConditionDimensions)));
            }
        }
        //再验证排序纬度
        if (!CollectionUtils.isEmpty(sorts)) {
            List<String> notExistSortDimensions = sorts.stream().map(SortModel::getName)
                    .filter(v -> !name2Dimenisons.containsKey(v))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(notExistSortDimensions)) {
                throw new ValidationException(String.format("排序中的纬度[%s]不存在！", StringUtils.collectionToCommaDelimitedString(notExistSortDimensions)));
            }
        }
        if (joinEnable) {
            //验证穿越纬度
            List<String> crossDimensions = dimensions.stream()
                    .filter(v -> v.contains("__"))
                    .collect(Collectors.toList());
            if (!crossDimensions.isEmpty()) {
                //todo 一度穿越，二度穿越
            }
        }
        vrc.put(vrk, true);
        return true;
    }

    private String explain(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) throws SqlParseException {
        //执行解析
        String transformSql = metricService.getMetricQuerySql(metrics.get(0).getId());
        // 解析配置
        SqlParser.Config mysqlConfig = SqlParser.configBuilder().setLex(Lex.MYSQL).build();
        // 创建解析器
        SqlParser parser = SqlParser.create(transformSql, mysqlConfig);
        // 解析sql
        SqlNode sqlNode = parser.parseQuery();
        if (sqlNode instanceof SqlSelect) {
            SqlSelect select = (SqlSelect) sqlNode;
            String conditionPart = "";
            if (condition != null) {
                conditionPart = "WHERE " + ConditionModel.toCmdSegement(condition);
            }
            String orderPart = "";
            if (!CollectionUtils.isEmpty(sorts)) {
                orderPart = "ORDER BY " + StringUtils.collectionToCommaDelimitedString(sorts.stream()
                        .map(v -> v.getAsc() ? v.getName() : String.format("%s desc", v.getName()))
                        .collect(Collectors.toList()));
            }
            //组合临时sql用于拼补
            String SpliceSql = String.format("select 1 from table_alias %s %s", conditionPart, orderPart);
            SqlParser parserSplice = SqlParser.create(SpliceSql, mysqlConfig);
            // 解析sql
            SqlNode spliceNode = parserSplice.parseQuery();
            SqlBasicCall whereNode = null;
            if (spliceNode instanceof SqlSelect) {
                SqlNode where = ((SqlSelect) spliceNode).getWhere();
                whereNode = (SqlBasicCall) where;
            }
            SqlNodeList orderNodeList = null;
            if (spliceNode instanceof SqlOrderBy) {
                SqlOrderBy orderByNode = (SqlOrderBy) spliceNode;
                orderNodeList = orderByNode.orderList;
                SqlNode query = orderByNode.query;
                SqlNode where = ((SqlSelect) query).getWhere();
                whereNode = (SqlBasicCall) where;
            }
            HashSet<String> dimSet = new HashSet<>(dimensions);
            spliceSqlSelectNode(select, dimSet, whereNode);
            if (orderNodeList != null) {
                select.setOrderBy(orderNodeList);
            }
            return getSqlStrFromSqlNode(select);

        } else {
            throw new IllegalArgumentException("explain sql 生成sql异常");
        }
    }

    private String getSqlStrFromSqlNode(SqlNode sqlNode) {
        return sqlNode.toSqlString(new MetricMysqlSqlDialect(SqlDialect.EMPTY_CONTEXT)).getSql();
    }

    /**
     * @param sqlSelect 查询体
     * @param dimSet    字段过滤集
     * @param where     查询条件
     * @return
     * @author zhao_xin
     * @description 对查询体，根据过滤集 裁剪掉多余查询字段与聚合字段，并下推查询条件至最内层子查询中
     **/
    private void spliceSqlSelectNode(SqlSelect sqlSelect, HashSet<String> dimSet, SqlBasicCall where) {
        SqlNodeList columnsList = new SqlNodeList(sqlSelect.getParserPosition());
        SqlNodeList groupList = new SqlNodeList(sqlSelect.getParserPosition());
        boolean hasGroupPart = sqlSelect.getGroup() != null;
        //用于where 条件 碰到 select 字段 有as 情况的 字段名替换
        HashMap<String, String> whereColumnMap = new HashMap<>();
        for (SqlNode node : sqlSelect.getSelectList()) {
            //普通字段
            if (node instanceof SqlIdentifier) {
                SqlIdentifier column = (SqlIdentifier) node;
                String columnName = column.names.get(0);
                if (dimSet.contains(columnName)) {
                    columnsList.add(node);
                    groupList.add(node);
                }
            } else if (node instanceof SqlBasicCall) {
                SqlBasicCall basicCall = (SqlBasicCall) node;
                columnsList.add(basicCall);
                if (hasGroupPart && Objects.equals(basicCall.getOperator().getName(), "AS") && basicCall.getOperandList().size() == 2) {
                    SqlNode left = basicCall.getOperandList().get(0);
                    SqlNode right = basicCall.getOperandList().get(1);
                    if (left instanceof SqlIdentifier && right instanceof SqlIdentifier) {
                        SqlIdentifier leftIdentifier = (SqlIdentifier) left;
                        SqlIdentifier rightIdentifier = (SqlIdentifier) right;
                        String leftColumnName = leftIdentifier.names.get(0);
                        String rightColumnName = rightIdentifier.names.get(0);
                        if (dimSet.contains(rightColumnName)) {
                            groupList.add(leftIdentifier);
                        }
                        whereColumnMap.put(rightColumnName, leftColumnName);
                    }
                }
                processSqlBasicCallNode(basicCall, dimSet);
            }
        }
        sqlSelect.setSelectList(columnsList);
        if (hasGroupPart) {
            if (groupList.size() == 0) {
                throw new IllegalArgumentException("explain sql 聚合字段异常");
            }
            sqlSelect.setGroupBy(groupList);
            //最好的方式是将条件下推到最底层，不过expr的方式会导致在最底层维度名称变化(AS) 导致查询条件还未生效
            //整个sql出现group by的层是维度最开始健全的时候，所以把条件放在group by层
            if (whereColumnMap.size() > 0) {
                sqlSelect.setWhere(replaceWhereNode(whereColumnMap, where));
            } else {
                sqlSelect.setWhere(where);
            }
        }
        //检查是否有子查询
        SqlNode from = sqlSelect.getFrom();
        if (from instanceof SqlBasicCall) {
            List<SqlNode> operandList = ((SqlBasicCall) from).getOperandList();
            SqlNode operandFirst = operandList.get(0);
            //有内嵌子sql
            if (operandFirst instanceof SqlSelect) {
                spliceSqlSelectNode((SqlSelect) operandFirst, dimSet, where);
            }
        }
    }

    /**
     * @param sqlBasicCall 外层sql出现的字段表达式
     * @param dimSet       子查询过滤集
     * @return
     * @author zhao_xin
     * @description 将外层sql出现的字段表达式的具体字段，增加到子查询的字段过滤集内
     **/
    private void processSqlBasicCallNode(SqlBasicCall sqlBasicCall, HashSet<String> dimSet) {
        List<SqlNode> operandList = sqlBasicCall.getOperandList();
        for (SqlNode sqlNode : operandList) {
            if (sqlNode instanceof SqlIdentifier) {
                SqlIdentifier column = (SqlIdentifier) sqlNode;
                String columnName = column.names.get(0);
                dimSet.add(columnName);
            } else if (sqlNode instanceof SqlBasicCall) {
                processSqlBasicCallNode((SqlBasicCall) sqlNode, dimSet);
            }
        }
    }

    /**
     * @param whereColumnMap name替换map
     * @param whereNode      where节点
     * @return
     * @author zhao_xin
     * @description 替换where查询中的字段名
     **/
    private SqlBasicCall replaceWhereNode(HashMap<String, String> whereColumnMap, SqlBasicCall whereNode) {
        List<SqlNode> newOperandList = new ArrayList<>();
        List<SqlNode> oldOperandList = whereNode.getOperandList();
        for (SqlNode sqlNode : oldOperandList) {
            if (sqlNode instanceof SqlIdentifier) {
                SqlIdentifier column = (SqlIdentifier) sqlNode;
                String columnName = column.names.get(0);
                if (whereColumnMap.containsKey(columnName)) {
                    SqlIdentifier newColumn = column.setName(0, whereColumnMap.get(columnName));
                    newOperandList.add(newColumn);
                } else {
                    newOperandList.add(sqlNode);
                }
            } else if (sqlNode instanceof SqlBasicCall) {
                newOperandList.add(replaceWhereNode(whereColumnMap, (SqlBasicCall) sqlNode));
            } else {
                newOperandList.add(sqlNode);
            }
        }
        return new SqlBasicCall(whereNode.getOperator(), newOperandList, whereNode.getParserPosition());
    }
}
