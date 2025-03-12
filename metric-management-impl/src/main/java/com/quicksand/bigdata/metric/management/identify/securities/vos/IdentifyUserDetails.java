package com.quicksand.bigdata.metric.management.identify.securities.vos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import lombok.Data;

/**
 * IdentifyUserDetails
 *
 * @author page
 * @date 2020/8/24 11:13
 */
@Data
public class IdentifyUserDetails
        extends UserSecurityDetails {

    /**
     * 密码 （加密以后的）
     */
    @JsonIgnore
    private String password;
    /**
     * 是否已经写入cookie
     */
    @JsonIgnore
    private boolean cookieWirted;
    /**
     * 标记退出
     */
    @JsonIgnore
    private boolean logout;

}
