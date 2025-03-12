package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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
public class MetricYamlBuilderModel {


    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "必要参数 维度字段列表", groups = {Insert.class})
    @NotEmpty(message = "必要参数 维度字段列表", groups = {Insert.class})
    @Valid
    List<MetricColumnsModel> dimColumns;

    @Schema(description = "")
    @NotNull(message = "必要参数 指标字段列表", groups = {Insert.class})
    @NotEmpty(message = "必要参数 指标字段列表", groups = {Insert.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Valid
    List<MetricColumnsModel> measureColumns;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "")
    @NotNull(message = "必要参数 指标基础信息baseInfo", groups = {Update.class, Insert.class})
    @Valid
    MetricModifyModel baseInfo;

}
