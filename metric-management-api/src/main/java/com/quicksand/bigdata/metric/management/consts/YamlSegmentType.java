package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * YamlSegmentType
 * (yaml片段类型)
 *
 * @author page
 * @date 2022/8/1
 */
@Getter
public enum YamlSegmentType {

    Dataset(0, "dataset", "数据集"),

    Metric(1, "metric", "指标"),

    Sql(2, "sql", "SQL"),

    ;

    @JsonValue
    final int code;

    final String flag;

    final String comment;

    YamlSegmentType(int code, String flag, String comment) {
        this.code = code;
        this.flag = flag;
        this.comment = comment;
    }

}
