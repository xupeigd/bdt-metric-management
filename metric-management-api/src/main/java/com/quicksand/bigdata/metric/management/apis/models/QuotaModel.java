package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QuotaModel
 *
 * @author page
 * @date 2022/11/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaModel {

    /**
     * 逻辑主键
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

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
     * app的类型
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer appType;

    /**
     * 配额
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long quota;

    /**
     * 刷新cron
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String refreshCornExpress;

    /**
     * 变更标识
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String exchangeFlag;

}
