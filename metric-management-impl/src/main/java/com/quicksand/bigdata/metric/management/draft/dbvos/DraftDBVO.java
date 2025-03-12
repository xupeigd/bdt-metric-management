package com.quicksand.bigdata.metric.management.draft.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.DraftType;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

/**
 * DrafDBVO
 *
 * @author page
 * @date 2022/8/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_DRAFT_DRAFTS,
        indexes = {
                @Index(name = "uniq_flag_user_id", unique = true, columnList = "flag,user_id")
        })
@Where(clause = " status = 1 ")
public class DraftDBVO {

    /**
     * 草稿主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 识别flag
     * (模块自定义)
     */
    @Column(length = 32, columnDefinition = " varchar(32) not null default '' comment '识别flag' ")
    String flag;

    /**
     * 草稿类型
     * <p>
     * 0 dataset 1 metric
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 1 COMMENT '草稿类型 0 数据集 1 指标' ")
    DraftType type;

    /**
     * 前端的json
     */
    @Column(columnDefinition = " text comment '草稿json' ")
    String content;

    /**
     * 创建用户
     */
    @ManyToOne(targetEntity = UserDBVO.class)
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    UserDBVO user;

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
