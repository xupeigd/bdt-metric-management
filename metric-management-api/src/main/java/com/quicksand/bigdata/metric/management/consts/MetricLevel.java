package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

/**
 * MetricLevel
 *
 * @author page
 * @date 2022/7/29
 */
@Getter
public enum MetricLevel {

    /**
     * T1
     */
    T1(0, "T1"),

    /**
     * T2
     */
    T2(1, "T2"),

    /**
     * T3
     */
    T3(2, "T3"),

    ;

    @JsonValue
    final int code;

    final String flag;

    MetricLevel(int code, String flag) {
        this.code = code;
        this.flag = flag;
    }

    public static MetricLevel getByFlag(String flag) {
        for (MetricLevel value : MetricLevel.values()) {
            if (Objects.equals(flag, value.getFlag())) {
                return value;
            }
        }
        return null;
    }

    public static MetricLevel getByCode(Integer code) {
        for (MetricLevel value : MetricLevel.values()) {
            if (Objects.equals(code, value.getCode())) {
                return value;
            }
        }
        return T1;
    }

}
