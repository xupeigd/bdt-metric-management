package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * MetricSegmentModel
 *
 * @author page
 * @date 2022/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricSegmentModel {

    @Schema(description = "")
    @Min(value = 0L, message = "不存在的指标哦! ", groups = {Update.class})
    @NotNull(message = "必要参数 指标id")
    Integer id;

    /**
     * 数据集Id
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Min(value = 0L, message = "不存在的数据集! ", groups = {Update.class})
    @NotNull(message = "必要参数 数据集id", groups = {Update.class})
    Integer datasetId;

    /**
     * yaml片段
     */
    @Length(min = 8, max = 5120, message = "内容长度在8～5120之间", groups = {Update.class})
    @NotBlank(message = "内容不能为空！", groups = {Update.class})
    String yamlSegment;


}
