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
public enum AppType {

    /**
     * 数据产品
     */
    data(0, "数据产品"),

    /**
     * 业务产品
     */
    business(1, "业务产品");

    @JsonValue
    final int code;

    final String name;


    public static AppType findByCode(int code) {
        for (AppType value : AppType.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }

    public static AppType findByName(String name) {
        for (AppType value : AppType.values()) {
            if (Objects.equals(value.getName(), name)) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AppType cover(int code) {
        for (AppType value : AppType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
