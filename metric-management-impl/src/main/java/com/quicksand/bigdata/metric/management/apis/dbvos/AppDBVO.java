package com.quicksand.bigdata.metric.management.apis.dbvos;

import com.quicksand.bigdata.metric.management.consts.AppType;
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
 * AppDBVO
 *
 * @author page
 * @date 2022/9/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_APIS_APPS,
        indexes = {
                @Index(name = "uniq_name", columnList = "name", unique = true)
        })
@Where(clause = " status = 1 ")
public class AppDBVO {

    /**
     * 内部应用
     */
    public static final int TYPE_INNER = 0;
    /**
     * 外部应用
     */
    public static final int TYPE_OUTER = 1;

    /**
     * 逻辑Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 应用名称
     */
    @Column(columnDefinition = " VARCHAR(255) NOT NULL DEFAULT '' COMMENT '应用名称' ")
    String name;

    /**
     * App类型 0内部应用 1外部应用
     */
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 1 COMMENT 'App类型 0内部应用 1外部应用' ")
    Integer type;

    /**
     * 描述
     */
    @Column(columnDefinition = " TEXT COMMENT '应用描述' ")
    String description;

    /**
     * 负责人
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @ManyToOne(targetEntity = UserDBVO.class)
    @JoinColumn(name = "owner_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '用户Id' ")
    UserDBVO owner;

    /**
     * 调用的token对象
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(false)
    @ManyToOne(targetEntity = AuthTokenDBVO.class)
    @JoinColumn(name = "token_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT 'tokenId' ")
    AuthTokenDBVO token;

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
     * 应用类型(0:数据产品，1:业务产品)
     * 默认0
     */
    @Column(name = "app_type", columnDefinition = " tinyint DEFAULT 0 NOT NULL  COMMENT '应用类型(0:数据产品，1:业务产品)' ")
    AppType appType;

}