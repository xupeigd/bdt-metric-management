package com.quicksand.bigdata.metric.management.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RoleGrantType
 *
 * @author page
 * @date 2022/9/26
 */
@Getter
@AllArgsConstructor
public enum GrantType {

    FIXED(0, "固定"),

    TEMPORARY(1, "临时性"),

    ;

    /**
     * code
     */
    int code;

    /**
     * comment
     */
    String comment;


}
