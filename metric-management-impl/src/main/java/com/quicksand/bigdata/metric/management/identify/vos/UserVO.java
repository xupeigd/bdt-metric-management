package com.quicksand.bigdata.metric.management.identify.vos;

import com.quicksand.bigdata.metric.management.consts.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * UserVO
 *
 * @author page
 * @date 2020/8/18 17:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    /**
     * 用户id
     */
    Integer id;

    /**
     * 名称
     */
    String name;

    /**
     * 密码串
     */
    String password;

    /**
     * 邮箱
     */
    String email;

    /**
     * 移动电话
     */
    String mobile;

    /**
     * 创建时间
     */
    Date createTime;

    /**
     * 更新时间
     */
    Date updateTime;

    /**
     * 状态
     * 0删除 1 非活跃 2 活跃
     *
     * @see UserStatus
     */
    UserStatus status;

    Integer createUserId;

    Integer updateUserId;

    /**
     * 用户具备的角色
     */
    List<RoleVO> roles;

}
