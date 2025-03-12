package com.quicksand.bigdata.metric.management.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 额度刷新模式
 *
 * @author page
 * @date 2022/9/27
 */
@Getter
@AllArgsConstructor
public enum QuotaRefreshMode {

    /**
     * 一次性
     */
    Disposable(0, "一次性"),

    /**
     * 周期性
     */
    Periodicity(1, "周期性"),
    ;

    final int code;

    final String comment;


}
