package com.quicksand.bigdata.metric.management.metric.models;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

/**
 * MetricCatalogModifyModel
 *
 * @author page
 * @date 2022/7/29
 */
@Data
@Builder
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class MetricCatalogAmisModifyModel {

    @Data
    public static final class MetricCatalogSelected {
        Integer id;
    }

    /**
     * id
     */
    @Schema(description = "")
    @Min(value = 1L, message = "不存在的目录实体！", groups = {Update.class})
    @Null(groups = {Insert.class})
    Integer id;

    /**
     * 名称
     */
    @Schema(description = "")
    @NotBlank(message = "名称不能为空！", groups = {Insert.class, Update.class})
    String name;

    /**
     * 业务编码
     */
    @Schema(description = "")
    @NotBlank(message = "业务编码不能为空！", groups = {Insert.class, Update.class})
    String businessCode;

    /**
     * 数据状态
     */
    @Schema(description = "")
    @Null(message = "不支持的参数！", groups = {Insert.class, Update.class})
    Integer status;

    /**
     * 父目录
     */
    MetricCatalogSelected parent;

}
