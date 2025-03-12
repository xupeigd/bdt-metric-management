package com.quicksand.bigdata.metric.management.yaml.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.metric.management.consts.YamlSegmentType;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
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
 * SegmentDBVO
 * (Yaml片段实体)
 *
 * @author page
 * @date 2022/8/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_YAML_SEGMENTS)
@Where(clause = " status = 1 ")
public class SegmentDBVO {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(columnDefinition = " bigint(20) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '片段类型 0 dataset 1 metric' ")
    YamlSegmentType type;

    /**
     * 关联的数据集
     */
    @ManyToOne(targetEntity = DatasetDBVO.class)
    @JoinColumn(name = "dataset_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11)  COMMENT '关联数据集id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    DatasetDBVO dataset;

    /**
     * 关联的指标
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(targetEntity = MetricDBVO.class)
    @JoinColumn(name = "metric_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11) DEFAULT 0 COMMENT '关联指标Id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    MetricDBVO metric;

    /**
     * 片段内容的Md5
     */
    @Column(name = "content_md5", columnDefinition = " varchar(32) NOT NULL DEFAULT '' COMMENT '片段Md5' ")
    String contentMd5;

    /**
     * 片段内容
     */
    @Column(columnDefinition = " text DEFAULT NULL COMMENT '片段内容' ")
    String content;

    /**
     * 版本
     */
    @Column(columnDefinition = " int(10) NOT NULL DEFAULT 1 COMMENT '版本' ")
    Integer version;

    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

//    @Column(name = "update_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
//    Integer updateUserId;

    /**
     * 更新用户
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @ManyToOne(targetEntity = UserDBVO.class)
    @JoinColumn(name = "update_user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    UserDBVO updateUser;

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
