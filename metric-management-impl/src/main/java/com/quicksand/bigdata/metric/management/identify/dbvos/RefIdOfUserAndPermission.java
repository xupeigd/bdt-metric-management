// package com.quicksand.bigdata.metric.management.identify.dbvos;
//
// import lombok.Data;
//
// import javax.persistence.Column;
// import javax.persistence.Embeddable;
// import java.io.Serializable;
//
// /**
//  * RefIdOfUserAndPermission
//  *
//  * @author page
//  * @date 2020/8/19 14:40
//  */
// @Data
// @Embeddable
// public class RefIdOfUserAndPermission
//         implements Serializable {
//
//     /**
//      * 用户id
//      */
//     @Column(name = "user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '用户Id' ")
//     Integer userId;
//
//     /**
//      * 权限Id
//      */
//     @Column(name = "permission_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '权限Id' ")
//     Integer permissionId;
//
// }
