package com.quicksand.bigdata.metric.management.datasource.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.ColumnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ColumnInfoModel
 *
 * @author page
 * @date 2022/7/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnModel {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    /**
     * 序列号，表内顺序
     */
    Integer serial;

    /**
     * 字段名称
     */
    String name;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    ColumnType columnType;

    /**
     * 类型
     * <p>
     * db/dwh 定义
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type;


    /**
     * 注释
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String comment;


}
