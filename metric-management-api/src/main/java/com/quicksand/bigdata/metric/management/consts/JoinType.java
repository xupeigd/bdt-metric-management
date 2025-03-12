package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JoinType
 *
 * @author page
 * @date 2022/7/29
 */
@Getter
@AllArgsConstructor
public enum JoinType {

    /**
     * LeftJoin
     */
    LeftJoin(0, "LJ", "Left Join"),

    /**
     * RightJoin
     */
    RightJoin(1, "RJ", "Right Join"),

    /**
     * InnerJoin
     */
    InnerJoin(2, "IJ", "Inner Join"),

    /**
     * OuterJoin
     */
    OuterJoin(3, "OJ", "Outer Join"),

    /**
     * CrossJoin
     */
    CrossJoin(4, "CJ", "Cross Join"),

    ;

    final int code;

    final String shortKey;

    @JsonValue
    final String flag;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static JoinType cover(int code) {
        for (JoinType value : JoinType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
