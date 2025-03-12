package com.quicksand.bigdata.metric.management.apis.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
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
@Table(name = TableNames.TABLE_APIS_REL_INVOKE_APPLY_RECORDS_METRIC,
        indexes = {
        })
@Where(clause = " status = 1 ")
public class RelationOfApplyAndMetricDBVO {

    /**
     * 逻辑Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 申请记录id
     */
    @Column(name = "apply_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '申请记录id' ")
    Integer applyId;

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
     * 关联的指标
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @ManyToOne(targetEntity = MetricDBVO.class)
    @JoinColumn(name = "metric_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    MetricDBVO metric;

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
