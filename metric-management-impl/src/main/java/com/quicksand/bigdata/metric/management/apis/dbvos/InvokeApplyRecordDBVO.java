package com.quicksand.bigdata.metric.management.apis.dbvos;

import com.quicksand.bigdata.metric.management.consts.ApproveState;
import com.quicksand.bigdata.metric.management.consts.ApproveType;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
@Entity
@Table(name = TableNames.TABLE_APIS_INVOKE_APPLY_RECORDS)
@Where(clause = " status = 1 ")
public class InvokeApplyRecordDBVO {

    /**
     * 逻辑Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 对应的应用
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @ManyToOne(targetEntity = AppDBVO.class)
    @JoinColumn(name = "app_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id' ")
    AppDBVO app;

    /**
     * 指标列表
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @ManyToMany(targetEntity = MetricDBVO.class)
    @JoinTable(name = TableNames.TABLE_APIS_REL_INVOKE_APPLY_RECORDS_METRIC,
            joinColumns = {@JoinColumn(name = "app_id")},
            inverseJoinColumns = {@JoinColumn(name = "metric_id")},
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @WhereJoinTable(clause = " status = 1 ")
    List<MetricDBVO> metrics;

    /**
     * 申请原因
     */
    @Column(columnDefinition = "text COMMENT '申请原因'")
    String description;

    /**
     * 审批state
     * 审批状态 0未审核 1批准 2拒绝
     *
     * @see ApproveState
     */
    @Column(name = "approved_state", columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT '审批状态 0未审核 1批准 2拒绝' ")
    @Enumerated(EnumType.ORDINAL)
    ApproveState approvedState;

    /**
     * 审批意见
     */
    @Column(name = "approved_comment", columnDefinition = "text COMMENT '审批意见' ")
    String approvedComment;

    /**
     * 审核用户id
     */
    @Column(name = "approved_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    Integer approvedUserId;

    /**
     * 更新时间
     */
    @Column(name = "approved_time", columnDefinition = " datetime  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间' ")
    Date approvedTime;


    /**
     * 申请类型
     * 申请类型 1指标申请调用 2其他
     *
     * @see ApproveType
     */
    @Column(name = "approved_type", columnDefinition = "tinyint NOT NULL DEFAULT 1 COMMENT '申请类型 1指标申请调用 2其他'")
    @Enumerated(EnumType.ORDINAL)
    ApproveType approvedType;

    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @ManyToOne(targetEntity = UserDBVO.class)
    @JoinColumn(name = "create_user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    UserDBVO createUser;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

    /**
     * 更新用户Id
     */
    @Column(name = "update_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    Integer updateUserId;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = " datetime  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
    Date updateTime;

    /**
     * 状态
     * 0删除 1 可用
     *
     * @see DataStatus
     */
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1 可用' ")
    @Enumerated(EnumType.ORDINAL)
    DataStatus status;


    /**
     * 每日请求次数
     */
    @Min(value = 1L, message = "最小值为1")
    @Max(value = 10000L, message = "最大值为10000")
    @NotNull(message = "每日请求次数不能为空！")
    @Schema(description = "每日请求次数")
    Integer qpd;

    /**
     * 峰值期间的每秒查询次数
     */
    @Min(value = 1L, message = "最小值为1")
    @Max(value = 1000L, message = "最大值为1000")
    @NotNull(message = "峰值期间的每秒查询次数不能为空！")
    @Schema(description = "峰值期间的每秒查询次数")
    Integer qps;

    /**
     * 查询请求的完成时间，前端可以选不同单位填写，后端统一存储单位ms
     */
    @Min(value = 1L, message = "最小值为1")
    @NotNull(message = "查询请求的完成时间不能为空")
    @Schema(description = "查询请求的完成时间，前端可以选不同单位填写，后端统一存储单位ms！")
    Integer tp99;


}
