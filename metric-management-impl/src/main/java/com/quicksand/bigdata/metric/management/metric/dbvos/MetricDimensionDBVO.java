package com.quicksand.bigdata.metric.management.metric.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.DimType;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

/**
 * MetricDimensionDBVO
 *
 * @author page
 * @date 2022/8/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Table(name = TableNames.TABLE_METRIC_DIMENSIONS,
        indexes = {
                @Index(name = "idx_metric_id", columnList = "metric_id"),
                //@Index(name = "uniq_dimension_name", columnList = "metric_id,name", unique = true)
        })
@Where(clause = " status = 1")
public class MetricDimensionDBVO {


    /**
     * 维度名称
     */
    @Column(length = 256, columnDefinition = " VARCHAR(256) NOT NULL DEFAULT '' COMMENT '维度名称' ")
    String name;

    /**
     * 维度说明
     */
    @Column(length = 256, columnDefinition = "varchar(256)  NULL DEFAULT '' COMMENT '描述'")
    String description;

    /**
     * 维度类型
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 24, columnDefinition = " VARCHAR(24) NOT NULL DEFAULT 'categorical' COMMENT '维度类型' ")
    DimType type;

//    /**
//     * 对应指标
//     */
//    @ManyToOne(targetEntity = MetricDBVO.class)
//    @JoinColumn(name = "metric_id",
//            referencedColumnName = "id",
//            columnDefinition = " bigint(11)  COMMENT '指标id' ",
//            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    MetricDBVO metricDBVO;


    /**
     * 字段的真实类型，与真实的数据库/数仓 一致
     */
    @Column(name = "data_type", length = 32, columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '字段的真实类型，与真实的数据库/数仓 一致' ")
    String dataType;

    /**
     * 计算表达式
     */
    @Column(name = "expr", length = 256, columnDefinition = " VARCHAR(256) NOT NULL DEFAULT '' COMMENT '计算表达式' ")
    String expr;


    /**
     * 是否为主时间维度
     */
    @Column(name = "is_primary", columnDefinition = " TINYINT(1)  NULL DEFAULT 0 COMMENT '是否为主时间维度' ")
    Boolean isPrimary;


    /**
     * 时间粒度
     */
    @Column(name = "time_granularity", columnDefinition = " VARCHAR(12)  NULL DEFAULT 'day' COMMENT '更新时间' ")
    String timeGranularity;


    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

    @Column(name = "update_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    Integer updateUserId;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = " datetime  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
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
     * 维度字段所在表名
     */
    @Column(name = "table_Name", length = 256, columnDefinition = " VARCHAR(256)  NULL DEFAULT '' COMMENT '维度字段所在表名' ")
    String tableName;


}
