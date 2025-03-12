package com.quicksand.bigdata.metric.management.identify.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.RoleType;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * RoleDBVO
 *
 * @author page
 * @date 2020/8/18 15:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_ROLES,
        indexes = {
                @Index(name = "uniq_name", columnList = "name", unique = true),
                @Index(name = "uniq_code", columnList = "code", unique = true),
        })
@Where(clause = " status = 1 ")
public class RoleDBVO {

    /**
     * 角色id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(name = "id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 角色名称
     */
    @Column(length = 64, columnDefinition = " VARCHAR(64) NOT NULL DEFAULT '' COMMENT '角色名称' ")
    String name;

    /**
     * 角色code
     */
    @Column(length = 64, columnDefinition = " VARCHAR(64) NOT NULL DEFAULT '' COMMENT '角色code' ")
    String code;

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
     * 角色的类型
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 1 COMMENT '角色类型 0 公共 1 个人 2 组织' ")
    RoleType type;

    /**
     * 状态
     * 0删除 1 可用
     *
     * @see DataStatus
     */
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1 可用' ")
    @Enumerated(EnumType.ORDINAL)
    DataStatus status;

    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    @Column(name = "update_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    Integer updateUserId;

    /**
     * 发放权限
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(targetEntity = PermissionDBVO.class,
            fetch = FetchType.EAGER)
    @WhereJoinTable(clause = "status=1 and type=1 and (permission_grant_type=0 ||  ( grant_start_time <= CURRENT_TIMESTAMP() && grant_end_time >= CURRENT_TIMESTAMP() ) )")
    @JoinTable(name = TableNames.TABLE_REL_ROLE_PERMISSION,
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")},
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<PermissionDBVO> permissions;

    /**
     * 回收权限
     * （回收权限一般只出现在个人角色，其余角色不存在回收权限）
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(targetEntity = PermissionDBVO.class,
            fetch = FetchType.EAGER)
    @WhereJoinTable(clause = "status=1 and type=0 and (permission_grant_type=0 ||  ( grant_start_time <= CURRENT_TIMESTAMP() && grant_end_time >= CURRENT_TIMESTAMP() ) )")
    @JoinTable(name = TableNames.TABLE_REL_ROLE_PERMISSION,
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")},
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    List<PermissionDBVO> revokePermissions;

}
