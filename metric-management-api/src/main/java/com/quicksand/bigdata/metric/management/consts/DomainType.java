package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TopicType
 *
 * @author page
 * @date 2022/7/29
 */
@Getter
@AllArgsConstructor
public enum DomainType {

    /**
     * 主题域
     */
    Topic(0, "主题域"),

    /**
     * 业务域
     */
    Business(1, "业务域"),

    ;

    @JsonValue
    final int code;

    final String name;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DomainType cover(int code) {
        for (DomainType value : DomainType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
