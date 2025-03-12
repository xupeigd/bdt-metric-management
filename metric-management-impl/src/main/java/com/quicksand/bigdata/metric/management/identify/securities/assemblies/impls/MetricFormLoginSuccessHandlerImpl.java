package com.quicksand.bigdata.metric.management.identify.securities.assemblies.impls;

import com.quicksand.bigdata.metric.management.identify.securities.assemblies.JwtService;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.MetricFormLoginSuccessHandler;
import com.quicksand.bigdata.metric.management.identify.securities.filters.ReferUrlFilter;
import com.quicksand.bigdata.metric.management.identify.securities.vos.IdentifyUserDetails;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.OperationLogUtils;
import com.quicksand.bigdata.vars.http.TraceId;
import com.quicksand.bigdata.vars.jwt.JwtToken;
import com.quicksand.bigdata.vars.security.consts.VarsSecurityConsts;
import com.quicksand.bigdata.vars.security.service.VarsSecurityJudger;
import com.quicksand.bigdata.vars.security.service.VarsSecurityPersistenceService;
import com.quicksand.bigdata.vars.security.vos.UserInfo;
import com.quicksand.bigdata.vars.util.GatewayConsts;
import com.quicksand.bigdata.vars.util.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * MetricsFormLoginSuccessHandlerImpl
 * (表单登陆成功后置处理器)
 *
 * @author page
 * @date 2020/8/27 15:07
 */
@Slf4j
@Component
public class MetricFormLoginSuccessHandlerImpl
        implements MetricFormLoginSuccessHandler {

    @Value("${vars.security.auth.expired.seconds:1800}")
    int authExpiredSeconds;
    @Resource
    JwtService jwtService;
    @Resource
    VarsSecurityJudger varsSecurityJudger;
    @Resource
    OperationLogService operationLogService;
    @Resource
    VarsSecurityPersistenceService varsSecurityPersistenceService;

    public static String makeMjToken(IdentifyUserDetails identifyUserDetails) {
        return new BigInteger(JsonUtils.toJsonString(identifyUserDetails).getBytes()).toString(16);
    }

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        operationLogService.log(OperationLogUtils.buildLoginLog(authentication));
        //表单登陆的由spring-security提供，因而需要签发对应的jwt，并写入cookie
        JwtToken<UserInfo> jwtToken = jwtService.signJwtToken(authentication);
        if (null != jwtToken) {
            String tokenStr = jwtToken.toTokenString();
            Object principal = authentication.getPrincipal();
            IdentifyUserDetails identifyUserDetails = (IdentifyUserDetails) principal;
            identifyUserDetails.setValidation(true);
            identifyUserDetails.setConsensus(true);
            identifyUserDetails.setTokenKey(tokenStr);
            //暂存对象
            varsSecurityPersistenceService.saveUserDetails(tokenStr, identifyUserDetails, authExpiredSeconds * 1000L);
            if (varsSecurityJudger.useCookie()) {
                //设置cookie
                Cookie cookie = new Cookie(VarsSecurityConsts.KEY_COOKIES_AUTH, tokenStr);
                cookie.setPath("/");
                cookie.setMaxAge(-1);
                response.addCookie(cookie);
            }
        }
        if (!StringUtils.hasText(TraceId.excavate())) {
            // 重定向至登陆前的请求位置
            String referUrl = (String) request.getSession().getAttribute(ReferUrlFilter.REFER_URL);
            if (StringUtils.hasText(referUrl)) {
                request.getSession().removeAttribute(ReferUrlFilter.REFER_URL);
                // response.sendRedirect(referUrl);
                GatewayConsts.redirectTo(request, response, referUrl);
            }
        }

    }

}
