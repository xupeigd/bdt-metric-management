package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * RoleType
 *
 * @author page
 * @date 2020/8/19 15:00
 */
@Getter
public enum RoleType {

    /**
     * 公共
     */
    PUBLIC(0),

    /**
     * 私有-个人
     */
    PERSON(1),

    /**
     * 私有-个人继承
     */
    PERSON_EXT(2),

    /**
     * 私有-组织
     */
    ORGANIZATION(3),

    ;

    @JsonValue
    private int code;

    RoleType(int code) {
        this.code = code;
    }


}
