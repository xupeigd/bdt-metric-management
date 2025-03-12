package com.quicksand.bigdata.metric.management.datasource.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * DatasetDBVO
 * (数据集主数据)
 *
 * @author page
 * @date 2022/7/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_DATASOURCE_DATASETS,
        indexes = {
                @Index(name = "uniq_name", columnList = "name", unique = true)
        })
@Where(clause = " status = 1 ")
public class DatasetDBVO {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(name = "id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    @Column(length = 256, columnDefinition = " varchar(256) NOT NULL DEFAULT '' COMMENT '名称' ")
    String name;

    @Column(name = "table_name", length = 256, columnDefinition = " varchar(256) NOT NULL DEFAULT '' COMMENT '表名称' ")
    String tableName;

    @Column(columnDefinition = " varchar(255) NOT NULL DEFAULT '' COMMENT '描述' ")
    String description;

    @ElementCollection
    @CollectionTable(name = TableNames.TABLE_DATASOURCE_DATASET_IDENTIFIERS,
            joinColumns = @JoinColumn(name = "dataset_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<IdentifierDBVO> identifiers;

    @ManyToMany(targetEntity = UserDBVO.class)
    @JoinTable(name = TableNames.TABLE_DATASOURCE_REL_DATASET_OWNER,
            joinColumns = {@JoinColumn(name = "dataset_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")},
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<UserDBVO> owners;

    @Lazy(false)
    @ManyToOne(targetEntity = ClusterInfoDBVO.class)
    @JoinColumn(name = "cluster_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '所属数据几圈' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    ClusterInfoDBVO cluster;

    @ElementCollection
    @CollectionTable(name = TableNames.TABLE_DATASOURCE_DATASET_COLUMNS,
            joinColumns = @JoinColumn(name = "dataset_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<DatasetColumnDBVO> columns;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type",
                    column = @Column(name = "mutability",
                            columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据可变性 0亘古不变 1全量变化 2仅追加' ")),
            @AttributeOverride(name = "updateCron",
                    column = @Column(name = "update_corn",
                            columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '变化周期corn' ")),
            @AttributeOverride(name = "alongColumn",
                    column = @Column(name = "along_column",
                            columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '变化周期corn' ")),
    })
    MutabilityDBVO mutability;

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

}
