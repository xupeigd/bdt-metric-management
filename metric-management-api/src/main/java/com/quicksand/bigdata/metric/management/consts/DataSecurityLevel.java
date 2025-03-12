package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

/**
 * MetricSecurityLevel
 * (数据安全级别)
 *
 * @author page
 * @date 2022/7/29
 */
@Getter
public enum DataSecurityLevel {

    /**
     * L1
     */
    L1(0, "L1"),

    /**
     * L2
     */
    L2(1, "L2"),

    /**
     * L3
     */
    L3(2, "L3"),
    /**
     * L4
     */
    L4(3, "L4"),

    ;

    @JsonValue
    final int code;

    final String flag;

    DataSecurityLevel(int code, String flag) {
        this.code = code;
        this.flag = flag;
    }

    public static DataSecurityLevel getByCode(Integer code) {
        for (DataSecurityLevel value : DataSecurityLevel.values()) {
            if (Objects.equals(code, value.getCode())) {
                return value;
            }
        }
        return L1;
    }

    public static DataSecurityLevel getByFlag(String flag) {
        for (DataSecurityLevel value : DataSecurityLevel.values()) {
            if (Objects.equals(flag, value.getFlag())) {
                return value;
            }
        }
        return null;
    }
}
