package com.quicksand.bigdata.metric.management.metric.models;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

/**
 * MetricLabelModifyModel
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/22 15:42
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "指标标签实体")
public class MetricLabelModifyModel {
    /**
     * id
     */
    @Schema(description = "标签id,更新时传入")
    @Min(value = 1L, message = "不存在的目录实体！", groups = {Update.class})
    @Null(groups = {Insert.class})
    Integer id;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称", required = true)
    @Length(min = 1, max = 20, message = "标签名称长度必须在1～20之间", groups = {Insert.class, Update.class})
    @NotBlank(message = "标签名称不能为空", groups = {Insert.class, Update.class})
    String name;
}
