package com.quicksand.bigdata.metric.management.identify.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.GrantType;
import com.quicksand.bigdata.metric.management.consts.PermissionGrantType;
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
 * RelationOfRoleAndPermissionDBVO
 *
 * @author page
 * @date 2020/8/18 16:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_REL_ROLE_PERMISSION,
        indexes = {
                @Index(name = "uniq_role_permission", columnList = "role_id, permission_id", unique = true)
        })
@Where(clause = " status = 1 ")
public class RelationOfRoleAndPermissionDBVO {

    /**
     * 逻辑Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(value = false)
    @ManyToOne(targetEntity = RoleDBVO.class)
    @JoinColumn(name = "role_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11) not null default 0 comment '数据集Id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    RoleDBVO role;

    @NotFound(action = NotFoundAction.IGNORE)
    @Lazy(value = false)
    @ManyToOne(targetEntity = PermissionDBVO.class)
    @JoinColumn(name = "permission_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    PermissionDBVO permissionDBVO;

    @Column(name = "type", columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '类型 0收回 1授予' ", length = 2)
    @Enumerated(EnumType.ORDINAL)
    PermissionGrantType type;

    @Column(name = "permission_grant_type", columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT 'grant类型 0固定 1临时' ", length = 2)
    @Enumerated(EnumType.ORDINAL)
    GrantType grantType;

    @Column(name = "grant_start_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date grantStartTime;

    @Column(name = "grant_end_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date grantEndTime;

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

    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    @Column(name = "update_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    Integer updateUserId;

}
