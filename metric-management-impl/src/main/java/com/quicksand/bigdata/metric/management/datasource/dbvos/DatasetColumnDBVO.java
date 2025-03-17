package com.quicksand.bigdata.metric.management.datasource.dbvos;

import com.quicksand.bigdata.metric.management.consts.ColumnType;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
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
@Embeddable
@Table(name = TableNames.TABLE_DATASOURCE_DATASET_COLUMNS,
        indexes = {
                @Index(name = "uniq_dataset_name", columnList = "dataset_id,name", unique = true)
        })
@Where(clause = " status = 1 ")
public class DatasetColumnDBVO {

    /**
     * 序列号，表内顺序
     */
    @Column(columnDefinition = " int(4) NOT NULL DEFAULT 0 COMMENT '序列号，表内顺序' ")
    Integer serial;

    /**
     * 字段名称
     */
    @Column(columnDefinition = " VARCHAR(64) NOT NULL DEFAULT '' COMMENT '字段名称' ")
    String name;

    /**
     * 数据表名称
     * (冗余)
     */
    @Column(name = "table_name", length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '表名称' ")
    String tableName;

    /**
     * 类型
     * <p>
     * db/dwh 定义
     */
    @Column(columnDefinition = " VARCHAR(16) NOT NULL DEFAULT '' COMMENT '类型：db/dwh 定义' ")
    String type;

    /**
     * 列类型
     * <p>
     * 0 normal 1 主键 2 外键
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "column_type", columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '字段类型 0 normal 1 pk 2 fk' ")
    ColumnType columnType;

    /**
     * 注释
     */
    @Column(columnDefinition = "VARCHAR(255) NOT NULL DEFAULT '' COMMENT '注释/描述' ")
    String comment;

    @Column(name = "create_user_id", columnDefinition = "bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

    @Column(name = "update_user_id", columnDefinition = "bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    Integer updateUserId;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = "datetime  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
    Date updateTime;

    /**
     * 状态
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1 可用' ")
    DataStatus status;

    /**
     * 选中状态表示可用使用（控制可用）
     */
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '是否选中 0否 1是' ")
    DataStatus included;

}
