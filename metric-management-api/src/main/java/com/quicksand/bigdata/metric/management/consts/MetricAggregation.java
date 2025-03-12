package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * WhetherStatus
 *
 * @author zhaoxin3
 * @date 2022/2/20
 */
@Getter
@AllArgsConstructor
public enum MetricAggregation {

    /**
     * 原子指标
     */
    ATOMIC(0, "原子指标"),

    /**
     * 衍生指标
     */
    DERIVATIVE(1, "衍生指标"),

    /**
     * 复合指标
     */
    COMBINE(2, "复合指标"),

    ;

    @JsonValue
    final int code;

    final String name;


    public static MetricAggregation findByCode(int code) {
        for (MetricAggregation value : MetricAggregation.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }

    public static MetricAggregation findByName(String name) {
        for (MetricAggregation value : MetricAggregation.values()) {
            if (Objects.equals(value.getName(), name)) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MetricAggregation cover(int code) {
        for (MetricAggregation value : MetricAggregation.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
