package com.quicksand.bigdata.metric.management.consts;

import lombok.Getter;

import java.util.Objects;

/**
 * StatisticPeriod
 *
 * @author page
 * @date 2022/7/29
 */
@Getter
public enum StatisticPeriod {

    /**
     * 年
     */
    Year(0, "年"),

    /**
     * 月
     */
    Month(1, "月"),

    /**
     * 周
     */
    Week(2, "周"),

    /**
     * 日
     */
    Day(3, "日"),

    /**
     * 季
     */
    Quarter(4, "季"),


    ;
    final int code;
    final String cn;

    StatisticPeriod(int code, String cn) {
        this.code = code;
        this.cn = cn;
    }

    public static StatisticPeriod findByCn(String cn) {
        for (StatisticPeriod value : StatisticPeriod.values()) {
            if (Objects.equals(cn, value.getCn())) {
                return value;
            }
        }
        return null;
    }

}
