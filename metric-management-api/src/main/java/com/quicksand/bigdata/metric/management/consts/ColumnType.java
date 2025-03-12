package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ColumnType
 *
 * @author page
 * @date 2022-08-01
 */
@Getter
@AllArgsConstructor
public enum ColumnType {

    /**
     * Normal
     */
    Normal(0, "normal", "", "正常"),

    /**
     * 主键
     */
    Primary(1, "primary key", "primary", "主键"),

    /**
     * 外键
     */
    Foreign(2, "foreign key", "foreign", "外键"),

    Unique(3, "unique key", "Unique", "唯一键"),

    ;

    @JsonValue
    final int code;

    final String flag;

    final String yamlKey;

    final String comment;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ColumnType cover(int code) {
        for (ColumnType value : ColumnType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
