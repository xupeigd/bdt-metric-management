package com.quicksand.bigdata.metric.management.identify.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

/**
 * Permission
 *
 * @author page
 * @date 2020/8/18 15:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_PERMISSIONS,
        indexes = {
                @Index(name = "uniq_name", columnList = "name", unique = true),
                @Index(name = "uniq_code", columnList = "code", unique = true),
        })
@Where(clause = " status = 1 ")
public class PermissionDBVO {

    /**
     * 权限Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 权限code
     */
    @Column(length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '权限code' ")
    String code;
    /**
     * 权限名称
     */
    @Column(length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '权限名称 ' ")
    String name;

    /**
     * 排序
     * <p>
     * 0 最大
     */
    @Column(length = 11, columnDefinition = " int(11) NOT NULL DEFAULT 0 COMMENT '排序' ")
    Integer serial;

    /**
     * 图标
     */
    @Column(columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '图标' ")
    String icon;

    /**
     * 权限的类型
     *
     * @see PermissionType
     */
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT '权限类型 0 非法数据 1 MENU 2 OPERATION ' ")
    @Enumerated(EnumType.ORDINAL)
    PermissionType type;

    /**
     * 父权限的Id
     */
    @Column(name = "parent_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '父权限Id' ")
    Integer parentId;

    /**
     * 所属模块
     */
    @Column(columnDefinition = " varchar(32) NOT NULL DEFAULT 0 COMMENT '所属模块' ")
    String module;

    /**
     * 路径
     * （当且仅当type=1，即Menu时有数据，其余情况为空）
     */
    @Column(columnDefinition = " varchar(255) NOT NULL DEFAULT '' COMMENT '前端页面path' ")
    String path;

    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

    @Column(name = "update_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
    Date updateTime;

    /**
     * 数据状态（逻辑删除）
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 0 COMMENT '数据状态 0 删除 1 可用' ")
    DataStatus status;

}
