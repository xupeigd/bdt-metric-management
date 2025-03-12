package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.ApproveState;
import com.quicksand.bigdata.metric.management.consts.ApproveType;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * InvokeApplyDetailModel
 *
 * @author page
 * @date 2022/10/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvokeApplyDetailModel {

    /**
     * id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    /**
     * 应用
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    AppModel app;

    /**
     * 指标
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MetricOverviewModel> metrics;

    /**
     * 申请原因
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;

    /**
     * 审批state
     * <p>
     * 审批状态 0未审核 1批准 2拒绝
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ApproveState approvedState;

    /**
     * 审批意见
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String approvedComment;

    /**
     * 审核用户id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer approvedUserId;

    /**
     * 申请类型
     * <p>
     * 申请类型 0指标调用申请 1其他
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ApproveType approvedType;

    /**
     * 建单用户Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    UserOverviewModel createUser;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date createTime;

    /**
     * 更新用户Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer updateUserId;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date updateTime;

    /**
     * 状态
     * 0删除 1 可用
     *
     * @see DataStatus
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    DataStatus status;

    /**
     * 每日请求次数
     */
    @Schema(description = "每日请求次数")
    Integer qpd;

    /**
     * 峰值期间的每秒查询次数
     */
    @Schema(description = "峰值期间的每秒查询次数")
    Integer qps;

    /**
     * 查询请求的完成时间，前端可以选不同单位填写，后端统一存储单位ms
     */
    @Schema(description = "查询请求的完成时间，前端可以选不同单位填写，后端统一存储单位ms！")
    Integer tp99;

}
