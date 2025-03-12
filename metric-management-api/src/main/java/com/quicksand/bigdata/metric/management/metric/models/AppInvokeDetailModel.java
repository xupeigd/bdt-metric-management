package com.quicksand.bigdata.metric.management.metric.models;

import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Null;

/**
 * AppInvokeDetailModel
 * 指标被应用使用详情
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/16 17:42
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppInvokeDetailModel {
    /**
     * metricId
     */
    @Schema(description = "")
    @Null(message = "非空参数：metricId", groups = {Update.class})
    @Min(value = 1L, message = "不存在的指标实体", groups = {Update.class})
    Long metricId;
    /**
     * quota
     */
    @Schema(description = "")
    Long quota;

    /**
     * appId
     */
    @Null(message = "非空参数：appId", groups = {Update.class})
    @Min(value = 1L, message = "不存在的应用id", groups = {Update.class})
    @Schema(description = "")
    Long appId;
    /**
     * appName
     */
    @Schema(description = "")
    String appName;
    /**
     * monthInvokeTop
     */
    @Schema(description = "")
    String monthInvokeTop;
    /**
     * monthInvokeAvg
     */
    @Schema(description = "")
    String monthInvokeAvg;
}
