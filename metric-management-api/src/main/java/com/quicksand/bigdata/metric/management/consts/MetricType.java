package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

/**
 * MetricType
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/18 11:52
 * @description 指标类型
 */
@Getter
public enum MetricType {
    measure_proxy("measure_proxy", "度量代理指标"),
    expr("expr", "表达式指标"),
    ratio("ratio", "比率指标"),
    cumulative("cumulative", "积累指标"),

    ;

    final String label;

    @JsonValue
    final String yamlValue;

    MetricType(String yamlValue, String label) {
        this.label = label;
        this.yamlValue = yamlValue;
    }

    public static MetricType getByValue(String yamlValue) {
        for (MetricType metricType : MetricType.values()) {
            if (Objects.equals(yamlValue, metricType.getYamlValue())) {
                return metricType;
            }
        }
        return null;
    }
}
