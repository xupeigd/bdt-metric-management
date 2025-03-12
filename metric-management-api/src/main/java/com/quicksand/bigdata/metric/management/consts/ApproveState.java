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
public enum ApproveState {

    /**
     * 默认
     * <p>
     * 未审批
     */
    DEFAULT(0),

    /**
     * 批准
     */
    APPROVED(1),

    /**
     * 拒绝
     */
    REJECT(2),

    ;

    @JsonValue
    final int code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ApproveState cover(int code) {
        for (ApproveState value : ApproveState.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
