package com.quicksand.bigdata.metric.management.apis.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.GrantType;
import com.quicksand.bigdata.metric.management.consts.QuotaRefreshMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * QuotaDBVO
 * 额度值
 *
 * @author page
 * @date 2022/9/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaVO {

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
     * 刷新模式 0一次性 1周期性
     */
    QuotaRefreshMode mode;

    /**
     * 额度值
     */
    Long quota;

    /**
     * 刷新cron表达式
     */
    String cronExpress;

    /**
     * 额度类型 0固定 1临时(带开闭周期)
     */
    GrantType grantType;

    /**
     * grant开始时间
     */
    Date grantStartTime;

    /**
     * grant结束时间
     */
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
