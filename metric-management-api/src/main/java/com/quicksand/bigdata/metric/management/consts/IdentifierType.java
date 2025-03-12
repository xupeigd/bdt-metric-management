package com.quicksand.bigdata.metric.management.consts;

import lombok.Getter;

/**
 * IdentifierType
 *
 * @author page
 * @date 2022/8/5
 */
@Getter
public enum IdentifierType {

    /**
     * 主键
     */
    Primary(0, "primary"),

    /**
     * 外键
     */
    Foreign(1, "foreign"),

    /**
     * 唯一
     */
    Unique(2, "unique"),

    ;

    final int code;

    final String flag;

    IdentifierType(int code, String flag) {
        this.code = code;
        this.flag = flag;
    }
}
