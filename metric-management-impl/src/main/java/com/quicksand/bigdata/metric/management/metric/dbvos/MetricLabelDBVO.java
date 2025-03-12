package com.quicksand.bigdata.metric.management.metric.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
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
 * MetricLabelDBVO
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/20 16:15
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_METRIC_LABELS,
        indexes = {
                @Index(name = "idx_t_metric_user_labels_user_id", columnList = "user_id, status")
        })
@Where(clause = " status = 1")
public class MetricLabelDBVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 标签名称
     */
    @Column(name = "name", length = 40, columnDefinition = " VARCHAR(40) NOT NULL DEFAULT '' COMMENT '标签名称' ")
    String name;


    /**
     * 指标ID
     */
    @ManyToOne(targetEntity = UserDBVO.class)
    @Lazy(false)
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11)  COMMENT '用户id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    UserDBVO user;


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
