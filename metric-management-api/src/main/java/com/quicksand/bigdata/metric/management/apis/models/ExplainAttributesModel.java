package com.quicksand.bigdata.metric.management.apis.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ExplainAttributesModel
 *
 * @author page
 * @date 2022/10/20
 */
@Schema(name = "解析属性model")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainAttributesModel {

    /**
     * 指标Ids
     * (逻辑必填)
     */
    // @NotEmpty(message = "请提供需要解析的指标Id")
    // @NotNull(message = "请提供需要解析的指标Id")
    @Schema(description = "")
    List<Integer> metricIds;

    /**
     * 指标的英文名称/编号
     * （可选）
     * （直达management时该参数不生效）
     */
    @Schema(description = "")
    List<String> metrics;

    /**
     * 纬度
     */
    @Schema(description = "")
    @NotEmpty(message = "请提供需要解析的纬度")
    @NotNull(message = "请提供需要解析的纬度")
    List<String> dimensions;

    /**
     * 条件
     */
    @Schema(description = "")
    ConditionModel condition;

    /**
     * 排序
     */
    @Schema(description = "")
    List<SortModel> sorts;

}
