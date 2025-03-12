package com.quicksand.bigdata.metric.management.datasource.vos;

import com.quicksand.bigdata.metric.management.consts.ColumnType;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * DatasetColumnDBVO
 * (数据集字段)
 *
 * @author page
 * @date 2022/7/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetColumnVO {

    /**
     * 序列号，表内顺序
     */
    Integer serial;

    /**
     * 字段名称
     */
    String name;

    /**
     * 数据表名称
     * (冗余)
     */
    String tableName;

    /**
     * 类型
     * <p>
     * db/dwh 定义
     */
    String type;

    /**
     * 列类型
     * <p>
     * 0 normal 1 主键 2 外键
     */
    ColumnType columnType;

    /**
     * 注释
     */
    String comment;


    Integer createUserId;

    /**
     * 创建时间
     */
    Date createTime;

    Integer updateUserId;

    /**
     * 更新时间
     */
    Date updateTime;

    /**
     * 状态
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    DataStatus status;

}
