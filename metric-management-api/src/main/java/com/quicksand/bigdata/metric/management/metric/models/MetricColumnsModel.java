package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetColumnModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * MetricColumnsModel
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/16 15:02
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricColumnsModel {

    @Schema(description = "")
    @NotNull(message = "必要参数 字段信息", groups = {Insert.class})
    @Valid
    DatasetColumnModel columnModel;

    @Schema(description = "")
    @NotBlank(message = "必要参数 字段说明", groups = {Insert.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;

    @Schema(description = "")
    @NotBlank(message = "必要参数 指标字段列表", groups = {Update.class})
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    String aggregationType;

}
