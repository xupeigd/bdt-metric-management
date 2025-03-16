package com.quicksand.bigdata.metric.management.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

import java.util.List;
import java.util.Objects;

/**
 * Class SqlUtil
 *
 * @Author: page
 * @Date: 2025/3/16
 * @Description:
 */
@Slf4j
public class SqlParserUtil {

    /**
     * @param sourceSql 源sql
     * @param tableName 模型表全名
     * @return
     * @author zhao_xin
     * @description 为有分区信息的查询添加分区条件
     **/
    public static String transformCalculateSql(String sourceSql, String tableName) {
        String partitionColumns = "pt";
        if (!sourceSql.contains(partitionColumns)) {
            return sourceSql;
        }
        // 解析配置
        SqlParser.Config mysqlConfig = SqlParser.configBuilder().setLex(Lex.MYSQL).build();
        // 创建解析器
        SqlParser parser = SqlParser.create(sourceSql, mysqlConfig);
        // 解析sql
        SqlNode sqlNode = null;
        try {
            sqlNode = parser.parseQuery();
            boolean isDimColumns = false;
            if (sqlNode instanceof SqlSelect) {
                SqlSelect select = (SqlSelect) sqlNode;
                for (SqlNode node : select.getSelectList()) {
                    //普通字段
                    if (node instanceof SqlIdentifier) {
                        SqlIdentifier column = (SqlIdentifier) node;
                        String columnName = column.names.get(0);
                        if (Objects.equals("pt", columnName)) {
                            isDimColumns = true;
                            break;
                        }
                    }
                }
                if (isDimColumns) {
                    String appendSql = String.format("select 1 from %s where %s =(select max(%s) from %s)", tableName, partitionColumns, partitionColumns, tableName);
                    SqlParser appendParser = SqlParser.create(appendSql, mysqlConfig);
                    SqlNode appendNode = appendParser.parseQuery();
                    if (appendNode instanceof SqlSelect) {
                        SqlSelect appendSelect = (SqlSelect) appendNode;
                        addWhereNodeForSelectNode(select, appendSelect.getWhere());
                        return select.toSqlString(new MetricMysqlSqlDialect(SqlDialect.EMPTY_CONTEXT)).getSql();
                    }
                }
            }

        } catch (SqlParseException e) {
            log.error("transformCalculateSql error:{}", e);
        }
        return sourceSql;
    }

    private static void addWhereNodeForSelectNode(SqlSelect sqlSelect, SqlNode where) {
        if (sqlSelect.getGroup() != null) {
            //最好的方式是将条件下推到最底层，不过expr的方式会导致在最底层维度名称变化(AS) 导致查询条件还未生效
            //整个sql出现group by的层是维度最开始健全的时候，所以把条件放在group by层
            sqlSelect.setWhere(where);
            return;
        }
        //检查是否有子查询
        SqlNode from = sqlSelect.getFrom();
        if (from instanceof SqlBasicCall) {
            List<SqlNode> operandList = ((SqlBasicCall) from).getOperandList();
            SqlNode operandFirst = operandList.get(0);
            //有内嵌子sql
            if (operandFirst instanceof SqlSelect) {
                addWhereNodeForSelectNode((SqlSelect) operandFirst, where);
            }
        }
    }

}
