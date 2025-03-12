package com.quicksand.bigdata.metric.management.identify.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * UserLoginModel
 *
 * @author page
 * @date 2020/8/21 16:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginModel {

    @JsonAlias("username")
    @NotEmpty(message = "请提供用户名! ")
    @Length(min = 3, max = 32, message = "用户长度在3～32位之间")
    String name;

    @NotEmpty(message = "密码不能为空! ")
    @Length(min = 3, max = 32, message = "密码长度在3～32位")
    String password;

}