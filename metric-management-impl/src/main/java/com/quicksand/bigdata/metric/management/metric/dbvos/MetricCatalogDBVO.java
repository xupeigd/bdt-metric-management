package com.quicksand.bigdata.metric.management.metric.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.DomainType;
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
 * MetricCatalogDBVO
 *
 * @author page
 * @date 2022-07-31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_METRIC_CATALOGS,
        indexes = {
                @Index(name = "uniq_code", columnList = "code", unique = true),
                @Index(name = "uniq_name", columnList = "name", unique = true),
        })
@Where(clause = " status = 1 ")
public class MetricCatalogDBVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    @Column(length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '名称' ")
    String name;

    @Column(length = 32, columnDefinition = " varchar(32) NOT NULL DEFAULT '' COMMENT '编码 3位/级别 eg：001001001' ")
    String code;

    /**
     * 父节点Id
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(targetEntity = MetricCatalogDBVO.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id",
            referencedColumnName = "id",
            columnDefinition = "bigint(11) NOT NULL DEFAULT 0 COMMENT '父节点主键'",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    MetricCatalogDBVO parent;

    /**
     * catalog节点类型
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '类型 0 主题域 1 业务域' ")
    DomainType type;

    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

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
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1 可用' ")
    DataStatus status;

    /**
     * 业务编码
     * 用于自动模式下生成域下的唯一编码
     *
     * @see DataStatus
     */
    @Column(name = "business_code", length = 32, columnDefinition = " varchar(32) NOT NULL DEFAULT '' COMMENT '业务编码，用户生成指标唯一编码' ")
    String businessCode;
}
