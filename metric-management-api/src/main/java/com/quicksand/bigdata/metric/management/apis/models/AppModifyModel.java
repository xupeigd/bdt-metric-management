package com.quicksand.bigdata.metric.management.apis.models;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * AppModifyModel
 *
 * @author page
 * @date 2022/9/28
 */
@Data
@Schema
public class AppModifyModel {

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    @Length(min = 1, max = 64, message = "应用名称长度必须在1 ~ 64 之间! ", groups = {Insert.class, Update.class})
    @NotBlank(message = "应用名称不能为空! ", groups = {Insert.class, Update.class})
    String name;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    @Length(max = 1024, message = "应用描述长度必须不能超过 1024! ", groups = {Insert.class, Update.class})
    String description;

    /**
     * 应用类型(0:数据产品，1:业务产品)
     * 默认0
     */
    @Schema(description = "应用类型 (0:数据产品，1:业务产品)")
    @Min(value = 0L, message = "指标等级在0-1范围内", groups = {Insert.class, Update.class})
    @Max(value = 1L, message = "指标等级在0-1范围内", groups = {Insert.class, Update.class})
    @NotNull(message = "应用类型! ", groups = {Insert.class, Update.class})
    Integer appType;

}
