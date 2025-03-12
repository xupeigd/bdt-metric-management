package com.quicksand.bigdata.metric.management.metric.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

/**
 * RelationOfLabelAndMetricDBVO
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/24 10:22
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_METRIC_REL_LABEL_Metric,
        indexes = {
                @Index(name = "idx_t_metric_rel_label_metric_label", columnList = "status, label_id"),
                @Index(name = "idx_t_metric_rel_label_metric_metric_id", columnList = "status, metric_id")
        })
@Where(clause = " status = 1")
public class RelationOfLabelAndMetricDBVO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 标签ID
     */
    @ManyToOne(targetEntity = MetricLabelDBVO.class)
    @JoinColumn(name = "label_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11)  COMMENT '标签id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    MetricLabelDBVO label;


    /**
     * 指标ID
     */
    @ManyToOne(targetEntity = MetricDBVO.class)
    @JoinColumn(name = "metric_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11)  COMMENT '指标id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    MetricDBVO metric;

    /**
     * 状态
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1 可用' ")
    DataStatus status;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;


    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = " datetime  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
    Date updateTime;
}
