// package com.quicksand.bigdata.metric.management.identify.dbvos;
//
// import com.quicksand.bigdata.metric.management.consts.DataStatus;
// import com.quicksand.bigdata.metric.management.consts.PermissionGrantType;
// import com.quicksand.bigdata.metric.management.consts.TableNames;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import org.hibernate.annotations.Where;
// import org.springframework.context.annotation.Lazy;
//
// import javax.persistence.*;
// import java.util.Date;
//
// /**
//  * RelationOfUserAndRoleDBVO
//  *
//  * @author page
//  * @date 2020/8/18 15:57
//  */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Entity
// @Table(name = TableNames.TABLE_REL_USER_PERMISSION)
// @Where(clause = " status = 1 ")
// public class RelationOfUserAndPermissionDBVO {
//
//     /**
//      * 逻辑Id
//      */
//     @Id
//     @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
//     @Column(columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
//     Integer id;
//
//     /**
//      * 用户
//      */
//     @Lazy(value = false)
//     @ManyToOne(targetEntity = UserDBVO.class)
//     @JoinColumn(name = "user_id",
//             referencedColumnName = "id",
//             columnDefinition = "bigint(11) NOT NULL DEFAULT 0 COMMENT '用户Id'",
//             foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//     UserDBVO user;
//
//     /**
//      * 权限
//      */
//     @Lazy(value = false)
//     @ManyToOne(targetEntity = PermissionDBVO.class)
//     @JoinColumn(name = "permission_id",
//             referencedColumnName = "id",
//             columnDefinition = "bigint(11) NOT NULL DEFAULT 0 COMMENT '权限Id'",
//             foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//     PermissionDBVO permission;
//
//     /**
//      * 授权的类型 0撤销授权 1授权
//      */
//     @Enumerated(EnumType.ORDINAL)
//     @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 0 COMMENT '权限的状态 0撤销 1授予' ")
//     PermissionGrantType type;
//
//     @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
//     Integer createUserId;
//
//     /**
//      * 创建时间
//      */
//     @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
//     Date createTime;
//
//
//     @Column(name = "update_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
//     Integer updateUserId;
//
//     /**
//      * 更新时间
//      */
//     @Column(name = "update_time", columnDefinition = " datetime  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
//     Date updateTime;
//
//     /**
//      * 状态
//      * 0 删除 1 可用
//      *
//      * @see DataStatus
//      */
//     @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1 可用' ")
//     @Enumerated(EnumType.ORDINAL)
//     DataStatus status;
//
// }
