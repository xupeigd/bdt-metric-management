package com.quicksand.bigdata.metric.management.metric.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.DimType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

/**
 * MetricDimensionVO
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/23 16:31
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDimensionVO {

    Integer id;

    /**
     * 维度名称
     */
    String name;

    /**
     * 维度说明
     */
    String description;

    /**
     * 维度类型
     */
    @Enumerated(EnumType.STRING)
    DimType type;

    /**
     * 对应指标
     */
    MetricVO metricVO;


    /**
     * 字段的真实类型，与真实的数据库/数仓 一致
     */
    String dataType;

    /**
     * 计算表达式
     */
    String expr;


    /**
     * 是否为主时间维度
     */
    Boolean isPrimary;


    /**
     * 时间粒度
     */
    String timeGranularity;


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
