package com.quicksand.bigdata.metric.management.metric.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * MetricQuotaModel
 *
 * @author page
 * @date 2022/12/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricQuotaModel {

    /**
     * 默认配额
     * <p>
     * 默认 1000L
     */
    @Min(value = -1L, message = "默认配额不得小于0 ！")
    Long quota = 1000L;

    /**
     * 刷新周期
     * （默认 月）
     * <p>
     * 1月 2周 3日 4小时
     */
    @Min(value = 1L, message = "刷新周期 1月 2周 3日 4小时")
    @Max(value = 4L, message = "刷新周期 1月 2周 3日 4小时")
    Integer period = 1;

}
