package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ApproveState
 * (审批状态)
 *
 * @author page
 * @date 2022/10/11
 */
@Getter
@AllArgsConstructor
public enum ApproveType {
    /**
     * 指标调用申请
     */
    DEFAULT(0, "指标调用申请"),

    /**
     * 其他
     */
    OTHER(1, "其他申请");

    @JsonValue
    final int code;

    final String flag;


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ApproveType cover(int code) {
        for (ApproveType value : ApproveType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
