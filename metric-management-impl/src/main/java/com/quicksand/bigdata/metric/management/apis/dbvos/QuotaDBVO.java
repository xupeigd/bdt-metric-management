package com.quicksand.bigdata.metric.management.apis.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.GrantType;
import com.quicksand.bigdata.metric.management.consts.QuotaRefreshMode;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
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
@Entity
@Table(name = TableNames.TABLE_APIS_QUOTAS,
        indexes = {
        })
@Where(clause = " status = 1 ")
public class QuotaDBVO {

    /**
     * 逻辑Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 关联的应用Id
     */
    @Column(name = "app_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id' ")
    Integer appId;

    /**
     * 关联的指标Id
     */
    @Column(name = "metric_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id' ")
    Integer metricId;

    /**
     * 刷新模式 0一次性 1周期性
     */
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT '刷新模式 0一次性 1周期性' ", length = 2)
    @Enumerated(EnumType.ORDINAL)
    QuotaRefreshMode mode;

    /**
     * 额度值
     */
    @Column(columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '额度值' ")
    Long quota;

    /**
     * 关联的流程单
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @OneToOne(targetEntity = InvokeApplyRecordDBVO.class)
    @JoinColumn(name = "apply_record_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '申请记录Id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    InvokeApplyRecordDBVO applyRecord;

    /**
     * 刷新cron表达式
     */
    @Column(name = "cron_express", columnDefinition = "varchar(255) NOT NULL DEFAULT '' COMMENT '刷新cron表达式' ")
    String cronExpress;

    /**
     * 额度类型 0固定 1临时(带开闭周期)
     */
    @Column(name = "grant_type", columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT 'grant类型 0固定 1临时' ", length = 2)
    @Enumerated(EnumType.ORDINAL)
    GrantType grantType;

    /**
     * grant开始时间
     */
    @Column(name = "grant_start_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date grantStartTime;

    /**
     * grant结束时间
     */
    @Column(name = "grant_end_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date grantEndTime;

    /**
     * 创建用户Id
     */
    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

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
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1可用' ")
    @Enumerated(EnumType.ORDINAL)
    DataStatus status;

}
