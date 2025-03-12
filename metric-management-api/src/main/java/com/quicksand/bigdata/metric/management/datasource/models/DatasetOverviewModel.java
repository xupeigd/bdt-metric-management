package com.quicksand.bigdata.metric.management.datasource.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
public class DatasetOverviewModel {

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

    @JsonIgnore
    List<UserOverviewModel> owners;

    @Schema(description = "")
    ClusterInfoModel cluster;

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
     * 状态
     * 0 删除 1 可用
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    DataStatus status;

    /**
     * 可变性
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MutabilityModel mutability;

    @JsonProperty
    public List<String> owners() {
        return !CollectionUtils.isEmpty(owners)
                ? owners.stream().map(UserOverviewModel::getName).collect(Collectors.toList())
                : Collections.emptyList();
    }

}
