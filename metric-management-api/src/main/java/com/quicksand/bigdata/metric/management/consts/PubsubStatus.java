package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PubsubStatus
 *
 * @author page
 * @date 2022/7/29
 */
@Getter
@AllArgsConstructor
public enum PubsubStatus {

    /**
     * 下线
     */
    Offline(0, "下线"),

    /**
     * 上线
     */
    Online(1, "上线"),

    ;

    @JsonValue
    final int code;

    final String name;


    public static PubsubStatus findByCode(int code) {
        for (PubsubStatus value : PubsubStatus.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PubsubStatus cover(int code) {
        for (PubsubStatus value : PubsubStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
