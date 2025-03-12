package com.quicksand.bigdata.metric.management.utils;

import org.apache.calcite.sql.dialect.MysqlSqlDialect;

public class MetricMysqlSqlDialect extends MysqlSqlDialect {
    public MetricMysqlSqlDialect(Context context) {
        super(context);
    }

    // 下发sql时，去除编码前缀,直接拼接原字符。
    @Override
    public void quoteStringLiteral(StringBuilder buf, String charsetName, String val) {
        buf.append(this.literalQuoteString);
        buf.append(val.replace(this.literalEndQuoteString, this.literalEscapedQuote));
        buf.append(this.literalEndQuoteString);
    }
}