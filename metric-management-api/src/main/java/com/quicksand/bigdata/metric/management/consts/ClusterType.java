package com.quicksand.bigdata.metric.management.consts;

import lombok.Getter;

import java.util.Objects;

/**
 * ClusterType
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/9/8 11:10
 * @description
 */
@Getter
public enum ClusterType {
    offline(0, "离线"),
    realtime(1, "实时"),
    ;

    private int code;
    private String flag;

    ClusterType(int code, String flag) {
        this.code = code;
        this.flag = flag;
    }

    public static ClusterType getByFlag(String flag) {
        for (ClusterType value : ClusterType.values()) {
            if (Objects.equals(flag, value.getFlag())) {
                return value;
            }
        }
        return null;
    }

    public static ClusterType getByCode(Integer code) {
        for (ClusterType value : ClusterType.values()) {
            if (Objects.equals(code, value.getCode())) {
                return value;
            }
        }
        return offline;
    }
}
