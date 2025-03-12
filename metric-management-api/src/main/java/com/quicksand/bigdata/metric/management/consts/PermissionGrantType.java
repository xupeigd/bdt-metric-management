package com.quicksand.bigdata.metric.management.consts;

import lombok.Getter;

/**
 * PermissionGrantType
 * （权限授予状态）
 *
 * @author page
 * @date 2020/8/19 15:05
 */
@Getter
public enum PermissionGrantType {

    /**
     * 收回
     */
    REVOKE(0),

    /**
     * 授予
     */
    GRANT(1),

    ;

    final int code;

    PermissionGrantType(int code) {
        this.code = code;
    }

}
