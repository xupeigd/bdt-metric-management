package com.quicksand.bigdata.metric.management.datasource.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

/**
 * DatasetModifyModel
 *
 * @author page
 * @date 2022/7/28
 */
@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class DatasetModifyModel {

    /**
     * 主键id
     */
    @Min(value = 1L, message = "不存在的数据集! ", groups = {Update.class})
    @NotNull(message = "不存在的数据集! ", groups = {Update.class})
    @Null(message = "非法参数！", groups = {Insert.class})
    Integer id;

    /**
     * 集群Id
     */
    @Schema(description = "")
    @Min(value = 1L, message = "不存在的数据集! ", groups = {Update.class})
    @NotNull(message = "数据集Id不能为空! ", groups = {Update.class})
    Integer cluster;

    /**
     * 名称
     */
    @Schema(description = "")
    @Length(min = 4, max = 32, message = "名称长度必须在4 ~ 32 之间! ", groups = {Insert.class, Update.class})
    @NotBlank(message = "名称不能为空! ", groups = {Insert.class, Update.class})
    String name;

    /**
     * 数据表名称
     */
    @Schema(description = "")
    @Length(min = 4, max = 64, message = "名称长度必须在4 ~ 64 之间! ", groups = {Insert.class, Update.class})
    @NotBlank(message = "表名称不能为空! ", groups = {Insert.class, Update.class})
    String tableName;

    /**
     * 负责人(单个)
     */
    Integer owner;

    /**
     * 主键字段
     */
    @Schema(description = "")
    String primaryKey;

    /**
     * 外键字段
     */
    @Schema(description = "")
    List<String> foreignKeys;

    /**
     * 描述信息
     */
    @Schema(description = "")
    @Length(max = 255, message = "描述信息不能超过255！", groups = {Insert.class, Update.class})
    String description;

    /**
     * 可变性
     * （暂时传null）
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MutabilityModel mutability;

    /**
     * 选中字段
     */
    String includedColumns;

}
