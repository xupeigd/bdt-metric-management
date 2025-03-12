package com.quicksand.bigdata.metric.management.metric.vos;

import com.quicksand.bigdata.metric.management.consts.AggregationType;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * MetricMeasureVO
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/23 16:32
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricMeasureVO {

    Integer id;

    /**
     * 度量名称
     */
    String name;

    /**
     * 度量说明
     */
    String description;

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
     * 聚合方法
     */
    AggregationType agg;


    /**
     * 加工逻辑
     */
    String processingLogic;


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
