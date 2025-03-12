package com.quicksand.bigdata.metric.management.metric.models;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

/**
 * MetricInsertModel
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/9/7 16:30
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "指标新建或修改实体")
public class MetricInsertModel {

    @Schema(description = " 主键id,只有更新时传入")
    @Null(message = "非空参数：id", groups = {Insert.class})
    @Min(value = 1L, message = "不存在的指标实体", groups = {Update.class})
    Integer id;

    /**
     * yaml片段
     */
    @Schema(description = " yaml片段")
    @Length(min = 8, max = 5120)
    @NotBlank(message = "yaml片段不能为空", groups = {Insert.class, Update.class})
    String yamlSegment;
}
