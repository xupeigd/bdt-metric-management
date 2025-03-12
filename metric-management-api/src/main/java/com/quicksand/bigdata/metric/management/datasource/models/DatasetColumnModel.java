package com.quicksand.bigdata.metric.management.datasource.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.Insert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * DatasetColumnModel
 *
 * @author page
 * @date 2022/7/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetColumnModel {

    /**
     * 数据集
     */
    @JsonIgnore
    DatasetOverviewModel dataset;

    /**
     * 序列号，表内顺序
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    Integer serial;

    /**
     * 字段名称
     */
    @NotBlank(message = "必要参数 字段名称", groups = {Insert.class})
    String name;

    /**
     * 类型
     * <p>
     * db/dwh 定义
     */
    @NotBlank(message = "必要参数 字段类型", groups = {Insert.class})
    String type;

    /**
     * 注释
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    String comment;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    Integer createUserId;

    /**
     * 创建时间
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer updateUserId;

    /**
     * 更新时间
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

    /**
     * 状态
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    DataStatus status;

    @JsonProperty
    public String tableName() {
        return null == dataset ? "" : dataset.getTableName();
    }

}
