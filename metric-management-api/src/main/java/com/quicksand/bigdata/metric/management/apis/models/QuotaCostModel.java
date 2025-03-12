package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * QuotaCostModel
 *
 * @author page
 * @date 2022/12/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaCostModel {

    /**
     * 指标Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer metricId;

    /**
     * 应用Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer appId;

    /**
     * 配额
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long quota;

    /**
     * 当日的消耗量
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long dayCost;

    /**
     * 已消耗数量
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long cost;

    /**
     * 最大的QPS
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer maxQps;

    /**
     * 同步mills
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long syncMills;

    /**
     * 日期flag
     * (不传时，表示当前续作)
     * (条陈数据不使用)
     * eg： yyyyy-MM-dd HH:mm:ss
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String dateFlag;

    /**
     * 更新时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

}
