package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MetricCatculateResponseModel
 * (指标计算结果model)
 *
 * @author page
 * @date 2022/8/25
 */
@Data
@Builder
@Schema(name = "指标计算结果Model")
@NoArgsConstructor
@AllArgsConstructor
public class MetricCatculateResponseModel {


    /**
     * Metric本身
     */
    @Schema(description = "")
    Integer id;

    /**
     * 转换的sql
     */
    @Schema(description = "")
    String sql;

    /**
     * 结果对象
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "")
    ResultSetModel resultSet;

}
