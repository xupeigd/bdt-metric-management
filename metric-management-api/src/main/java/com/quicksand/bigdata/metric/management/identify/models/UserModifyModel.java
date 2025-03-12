package com.quicksand.bigdata.metric.management.identify.models;


import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * UserCreateModel
 *
 * @author page
 * @date 2020/8/18 13:55
 */
@Data
public class UserModifyModel {

    @Data
    public static final class RoleSelected {

        Integer id;

    }


    Integer id;

    /**
     * 用户名称
     */
    @NotBlank(message = "用户名不能为空！", groups = {Insert.class, Update.class})
    @Length(min = 2, max = 8, message = "用户名长度必须在2～8位之间", groups = {Insert.class, Update.class})
    String name;
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空！", groups = {Insert.class})
    @Email(message = "不合法的邮箱地址！", groups = {Insert.class, Update.class})
    String email;

    /**
     * 角色
     */
    List<RoleSelected> roles;
    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空！", groups = {Insert.class})
    @Length(min = 4, max = 16, message = "密码长度必须在4～16之间 ！", groups = {Insert.class, Update.class})
    String password;

    String mobile;

    Integer userStatus;

}
