package com.quicksand.bigdata.metric.management.datasource.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.Mutability;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MutabilityModel
 *
 * @author page
 * @date 2022/8/18
 */
@Data
@Builder
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class MutabilityModel {

    /**
     * 可变类型
     */
    @Schema(description = "")
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Mutability type;

    /**
     * 变化周期corn
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String updateCron;

    /**
     * 变化标识列
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String alongColumn;

}
