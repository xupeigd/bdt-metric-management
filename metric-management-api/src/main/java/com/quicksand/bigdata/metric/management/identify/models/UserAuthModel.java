package com.quicksand.bigdata.metric.management.identify.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.vars.jwt.JwtToken;
import com.quicksand.bigdata.vars.security.vos.UserInfo;
import lombok.Data;

/**
 * UserAuthModel
 *
 * @author page
 * @date 2020/8/21 16:50
 */
@Data
public class UserAuthModel {

    int id;

    String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    JwtToken<UserInfo> jwtToken;

}
