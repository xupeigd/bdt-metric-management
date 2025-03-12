package com.quicksand.bigdata.metric.management.apis.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * QuotaCostDBVO
 *
 * @author page
 * @date 2022/12/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaCostVO {

    /**
     * 逻辑Id
     */
    Integer id;

    /**
     * 关联的应用Id
     */
    Integer appId;

    /**
     * 关联的指标Id
     */
    Integer metricId;

    /**
     * lockdown标识 （正在使用的为空串，失效的为yyyy-MM-dd HH:mm:ss）
     */
    String lockDownFlag;

    /**
     * 额度值
     */
    Long quota;

    /**
     * 消耗值
     */
    Long cost;

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
