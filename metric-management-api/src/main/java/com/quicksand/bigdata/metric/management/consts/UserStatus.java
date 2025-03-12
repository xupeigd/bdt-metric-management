package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * UserStatus
 *
 * @author page
 * @date 2020/8/18
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    /**
     * 已删除
     */
    DELETE(0),

    /**
     * 冻结
     */
    INACTIVE(1),

    /**
     * 活跃（正常）
     */
    ACTIVE(2),

    ;

    @JsonValue
    final int code;

    public static UserStatus findByCode(Integer code) {
        for (UserStatus us : UserStatus.values()) {
            if (code == us.code) {
                return us;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UserStatus cover(int code) {
        for (UserStatus value : UserStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
