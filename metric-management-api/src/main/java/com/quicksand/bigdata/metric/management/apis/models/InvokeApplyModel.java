package com.quicksand.bigdata.metric.management.apis.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * InvokeApplyModel
 *
 * @author page
 * @date 2022/10/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "调用申请Model")
public class InvokeApplyModel {

    /**
     * 应用Id
     */
    @Min(value = 1L, message = "不存在的应用！")
    @Schema(description = "应用Id")
    Integer appId;

    /**
     * 申请的指标Id
     */
    @NotNull(message = "申请的指标不能为空！")
    @NotEmpty(message = "申请的指标不能为空！")
    @Schema(description = "申请的指标Id")
    List<Integer> metricIds;

    /**
     * 申请原因
     */
    @Length(max = 512, message = "申请原因长度不得超过512！")
    @Schema(description = "申请原因")
    String description;

    /**
     * 每日请求次数
     */
    @Min(value = 1L, message = "最小值为1")
    @Max(value = 10000L, message = "最大值为10000")
    @NotNull(message = "每日请求次数不能为空！")
    @Schema(description = "每日请求次数")
    Integer qpd;

    /**
     * 峰值期间的每秒查询次数
     */
    @Min(value = 1L, message = "最小值为1")
    @Max(value = 1000L, message = "最大值为1000")
    @NotNull(message = "峰值期间的每秒查询次数不能为空！")
    @Schema(description = "峰值期间的每秒查询次数")
    Integer qps;

    /**
     * 查询请求的完成时间，前端可以选不同单位填写，后端统一存储单位ms
     */
    @Min(value = 1L, message = "最小值为1")
    @NotNull(message = "查询请求的完成时间不能为空")
    @Schema(description = "查询请求的完成时间，前端可以选不同单位填写，后端统一存储单位ms！")
    Integer tp99;

}
