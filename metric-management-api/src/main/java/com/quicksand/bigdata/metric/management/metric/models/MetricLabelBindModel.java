package com.quicksand.bigdata.metric.management.metric.models;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

/**
 * MetricLabelBind
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/27 17:48
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "指标与标签绑定关系实体")
public class MetricLabelBindModel {

    /**
     * id
     */
    @Schema(description = "指标id,必须值")
    @Min(value = 1L, message = "不存在的目录实体！", groups = {Insert.class, Update.class})
    @NotNull(groups = {Insert.class, Update.class})
    Integer metricId;

    /**
     * label id
     */
    @Schema(description = "标签id,解绑必须值")
    @Min(value = 1L, message = "不存在的标签实体！", groups = {Update.class})
    @NotNull(groups = {Update.class})
    @Null(groups = {Insert.class})
    Integer labelId;

    /**
     * label names
     */
    @Schema(description = "标签name列表,绑定必须值")
    @NotNull(groups = {Insert.class})
    @Null(groups = {Update.class})
    List<String> labelNames;
}
