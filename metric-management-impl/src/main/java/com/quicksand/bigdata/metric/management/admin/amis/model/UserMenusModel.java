package com.quicksand.bigdata.metric.management.admin.amis.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Class UserMenusModel
 *
 * @Author: ap100
 * @Date: 2024/9/9
 * @Description:
 */
@Data
public class UserMenusModel {

    @JsonAlias("name")
    @JsonProperty("name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String userName;

    /**
     * 头像
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String avatar;

    /**
     * 邮箱
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    SettingModel.NavContainer menus;

}
