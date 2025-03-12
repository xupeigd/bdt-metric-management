package com.quicksand.bigdata.metric.management.identify.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quicksand.bigdata.metric.management.consts.UserStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UserModel
 *
 * @author page
 * @date 2020/8/18
 * <p>
 */
@Data
public class UserDetailsModel {

    /*
     * {
     * "id": 123,
     * "name": "page",
     * "email": "page.quicksand.com",
     * "createTime": "2020-08-01 12:00:00",
     * "updateTime": "2020-08-01 12:00:00",
     * "lastLoginTime": "2020-0-15 09:35:35",
     * "status": 0,
     * "roleIds": [
     * 10
     * ]
     * }
     */

    /**
     * 用户id
     */
    Integer id;
    /**
     * 用户名
     */
    String name;
    /**
     * 邮箱
     */
    String email;
    /**
     * 电话
     */
    String mobile;
    /**
     * 创建日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    /**
     * 更新日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
    /**
     * 最后登陆时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date lastLoginTime;
    /**
     * 状态
     * <p>
     * 0 删除 1 非活跃（停用） 2 活跃
     * {@link UserStatus}
     */
    Integer userStatus;
    /**
     * 角色
     */
    List<RoleOverviewModel> roles;

}
