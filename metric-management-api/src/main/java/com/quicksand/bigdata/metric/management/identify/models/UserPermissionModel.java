package com.quicksand.bigdata.metric.management.identify.models;

import lombok.Data;

/**
 * UserPermissionModel
 *
 * @author page
 * @date 2020/8/18 14:26
 */
@Data
public class UserPermissionModel {

    /*
     * {
     *       "id": 123,
     *       "code": "up0"
     *       "roleId": 123
     *}
     */

    /**
     * 权限Id权限Id
     */
    Integer id;
    /**
     * 权限code
     */
    String code;
    /**
     * 赋予的角色Id（若为针对个人赋予，该值为0）
     */
    Integer roleId;

}
