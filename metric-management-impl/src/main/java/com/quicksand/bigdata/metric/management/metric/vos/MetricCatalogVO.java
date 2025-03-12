package com.quicksand.bigdata.metric.management.metric.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.DomainType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * MetricCatalogVO
 *
 * @author page
 * @date 2022/8/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricCatalogVO {

    Integer id;

    String name;

    String code;

    String businessCode;

    /**
     * 目录下指标个数
     */
    Integer metricCount;

    /**
     * 父节点Id
     */
    MetricCatalogVO parent;

    List<MetricCatalogVO> children;

    /**
     * catalog节点类型
     */
    DomainType type;

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
