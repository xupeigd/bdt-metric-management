package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * AggregationType
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/16 15:06
 * @description
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AggregationType {
    SUM("sum", "SUM"),
    MIN("min", "MIN"),
    MAX("max", "MAX"),
    AVERAGE("average", "AVERAGE"),
    BOOLEAN("boolean", "BOOLEAN"),
    SUM_BOOLEAN("sum_boolean", "SUM_BOOLEAN"),
    COUNT_DISTINCT("count_distinct", "COUNT_DISTINCT"),
    ;
    final String label;

    @JsonValue
    final String yamlValue;

    AggregationType(String yamlValue, String label) {
        this.label = label;
        this.yamlValue = yamlValue;
    }

    public static AggregationType getByValue(String yamlValue) {
        for (AggregationType aggregationType : AggregationType.values()) {
            if (aggregationType.getYamlValue().equalsIgnoreCase(yamlValue)) {
                return aggregationType;
            }
        }
        return null;
    }

}
