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
public enum WhetherStatus {

    /**
     * 否
     */
    No(0, "否"),

    /**
     * 是
     */
    Yes(1, "是"),

    ;

    @JsonValue
    final int code;

    final String name;


    public static WhetherStatus findByCode(int code) {
        for (WhetherStatus value : WhetherStatus.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }

    public static WhetherStatus findByName(String name) {
        for (WhetherStatus value : WhetherStatus.values()) {
            if (Objects.equals(value.getName(), name)) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WhetherStatus cover(int code) {
        for (WhetherStatus value : WhetherStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
