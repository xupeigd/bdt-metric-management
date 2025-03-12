package com.quicksand.bigdata.metric.management.apis.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.GrantType;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RelationOfAppAndMetricDBVO
 *
 * @author page
 * @date 2022/9/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationOfAppAndMetricVO {

    /**
     * 逻辑Id
     */
    Integer id;

    /**
     * 关联的应用Id
     */
    Integer appId;

    /**
     * 关联的指标
     */
    MetricVO metric;

    /**
     * 关联指标
     */
    QuotaVO quota;

    /**
     * 关联类型
     */
    GrantType grantType;

    Date grantStartTime;

    Date grantEndTime;

    /**
     * 创建用户Id
     */
    Integer createUserId;

    /**
     * 创建时间
     */
    Date createTime;

    /**
     * 更新用户Id
     */
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
