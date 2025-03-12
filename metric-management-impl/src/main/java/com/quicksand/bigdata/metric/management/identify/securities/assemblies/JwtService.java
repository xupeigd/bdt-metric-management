package com.quicksand.bigdata.metric.management.identify.securities.assemblies;

import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.jwt.JwtToken;
import com.quicksand.bigdata.vars.security.vos.UserInfo;
import org.springframework.security.core.Authentication;

import javax.validation.constraints.NotNull;

/**
 * JWTService
 *
 * @author page
 * @date 2020/8/27 15:46
 */
public interface JwtService {

    /**
     * 利用UserVO签发JWT token
     *
     * @param userVO 用户数据
     * @return JWT Token
     */
    JwtToken<UserInfo> signJwtToken(@NotNull UserVO userVO);

    /**
     * 利用Authentication 签发JWT Token
     *
     * @param authentication 签注
     * @return JWT Token
     */
    JwtToken<UserInfo> signJwtToken(@NotNull Authentication authentication);

}
