package com.quicksand.bigdata.metric.management.metric.vos;

import com.quicksand.bigdata.metric.management.utils.MapIndex;
import lombok.Data;

import java.math.BigInteger;

/**
 * CommonDownListVO
 * 用于各种下拉列表
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/15 14:21
 * @description
 */
@Data
public class AppInvokeDetailVO {
    Long metricId;
    Long quota;
    Long appId;
    String appName;
    String monthInvokeTop;
    String monthInvokeAvg;


    @MapIndex
    public AppInvokeDetailVO(BigInteger metricId, BigInteger quota, BigInteger appId, String appName) {
        this.metricId = metricId.longValue();
        this.quota = quota.longValue();
        this.appId = appId.longValue();
        this.appName = appName;
//        this.monthInvokeTop = "暂无";
//        this.monthInvokeAvg = "暂无";
    }

    @MapIndex(1)
    public AppInvokeDetailVO(Long metricId, Long quota, Long appId, String appName, String monthInvokeTop, String monthInvokeAvg) {
        this.metricId = metricId;
        this.quota = quota;
        this.appId = appId;
        this.appName = appName;
        this.monthInvokeTop = monthInvokeTop;
        this.monthInvokeAvg = monthInvokeAvg;
    }
}
