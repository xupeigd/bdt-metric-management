package com.quicksand.bigdata.metric.management.apis.vos;

import com.quicksand.bigdata.metric.management.consts.ApproveState;
import com.quicksand.bigdata.metric.management.consts.ApproveType;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * InvokeApplyRecordDBVO
 *
 * @author page
 * @date 2022/10/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvokeApplyRecordVO {

    /**
     * 逻辑Id
     */
    Integer id;

    /**
     * 对应的应用
     */
    AppVO app;

    /**
     * 指标列表
     */
    List<MetricVO> metrics;

    /**
     * 申请原因
     */
    String description;

    /**
     * 审批state
     * 审批状态 0未审核 1批准 2拒绝
     *
     * @see ApproveState
     */
    ApproveState approvedState;

    /**
     * 审批意见
     */
    String approvedComment;

    /**
     * 审核用户id
     */
    Integer approvedUserId;

    /**
     * 审核时间
     */
    Date approvedTime;

    /**
     * 申请类型
     */
    ApproveType approvedType;

    /**
     * 建单用户
     */
    UserVO createUser;

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
     * 0删除 1 可用
     *
     * @see DataStatus
     */
    DataStatus status;

    /**
     * 每日请求次数
     */
    Integer qpd;

    /**
     * 峰值期间的每秒查询次数
     */
    Integer qps;

    /**
     * 查询请求的完成时间，前端可以选不同单位填写，后端统一存储单位ms
     */
    Integer tp99;

}
