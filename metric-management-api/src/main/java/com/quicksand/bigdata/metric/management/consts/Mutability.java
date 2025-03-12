package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Mutability
 *
 * @author page
 * @date 2022/8/18
 */
@Getter
@AllArgsConstructor
public enum Mutability {

    /**
     * 不可变
     */
    Immutable(0, "immutable"),

    /**
     * 全量变更
     */
    FullMutation(1, "full_mutation"),

    /**
     * 只是追加
     */
    AppendOnly(2, "append_only"),

    ;

    @JsonValue
    final int code;

    final String flag;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Mutability cover(int code) {
        for (Mutability value : Mutability.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
