package com.quicksand.bigdata.metric.management.identify.dbvos;

import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.metric.management.consts.UserStatus;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * UserDBVO
 *
 * @author page
 * @date 2020/8/18 15:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_USERS,
        indexes = {
                @Index(name = "uniq_name", columnList = "name", unique = true)
        })
@Where(clause = " status <> 0 ")
public class UserDBVO {

    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(name = "id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 名称
     */
    @Column(length = 32, columnDefinition = " varchar(32) NOT NULL DEFAULT '' COMMENT '用户名' ")
    String name;

    /**
     * 密码串
     */
    @Column(length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '密码密文串' ")
    String password;

    /**
     * 邮箱
     */
    @Column(length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT 'Email' ")
    String email;

    /**
     * 移动电话
     */
    @Column(length = 16, columnDefinition = " varchar(16) NOT NULL DEFAULT '' COMMENT 'MobilePhone' ")
    String mobile;

    /**
     * 用户具备的角色
     */
    @NotFound(action = NotFoundAction.IGNORE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(targetEntity = RoleDBVO.class)
    @WhereJoinTable(clause = "status=1 and (grant_type=0 ||  ( grant_start_time <= CURRENT_TIMESTAMP() && grant_end_time >= CURRENT_TIMESTAMP() ) )")
    @JoinTable(name = TableNames.TABLE_REL_USER_ROLE,
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")},
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    List<RoleDBVO> roles;

    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

    /**
     * 更新用户
     */
    @Column(name = "update_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    Integer updateUserId;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
    Date updateTime;

    /**
     * 状态
     * 0删除 1 非活跃 2 活跃
     *
     * @see UserStatus
     */
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态 0删除 1非活跃（锁定/冻结）2活跃' ", length = 2)
    @Enumerated(EnumType.ORDINAL)
    UserStatus status;

}
