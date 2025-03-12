package com.quicksand.bigdata.metric.management.apis.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.GrantType;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
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
 * RelationOfAppAndMetricDBVO
 *
 * @author page
 * @date 2022/9/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_APIS_REL_APP_METRIC,
        indexes = {
        })
@Where(clause = " status = 1 ")
public class RelationOfAppAndMetricDBVO {

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
     * 关联的指标
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @ManyToOne(targetEntity = MetricDBVO.class)
    @JoinColumn(name = "metric_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    //指标逻辑存在，且上线状态
    @Where(clause = " metric.status = 1 and metric.pub_sub=1 ")
    MetricDBVO metric;

    /**
     * 关联指标
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @OneToOne(targetEntity = QuotaDBVO.class)
    @JoinColumn(name = "quota_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '额度Id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    QuotaDBVO quota;

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
     * 关联类型
     */
    @Column(name = "grant_type", columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT 'grant类型 0固定 1临时' ", length = 2)
    @Enumerated(EnumType.ORDINAL)
    GrantType grantType;

    @Column(name = "grant_start_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date grantStartTime;

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
