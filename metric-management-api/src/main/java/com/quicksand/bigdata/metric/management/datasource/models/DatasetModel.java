package com.quicksand.bigdata.metric.management.datasource.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DatasetModel
 *
 * @author page
 * @date 2022/7/28
 */
@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class DatasetModel {

    @Schema(description = "")
    Integer id;

    @Schema(description = "")
    String name;

    /**
     * 映射表名称
     */
    @Schema(description = "")
    String tableName;

    @Schema(description = "")
    String description;

    // @JsonIgnore
    List<UserOverviewModel> owners;

    @Schema(description = "")
    ClusterInfoModel clusterInfo;

    Integer createUserId;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    Integer updateUserId;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

    /**
     * 可变性
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MutabilityModel mutability;

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
     * 选中字段
     */
    String includedColumns;

}
