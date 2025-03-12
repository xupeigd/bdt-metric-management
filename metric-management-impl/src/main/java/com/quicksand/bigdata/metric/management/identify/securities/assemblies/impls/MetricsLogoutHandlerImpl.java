package com.quicksand.bigdata.metric.management.identify.securities.assemblies.impls;

import com.quicksand.bigdata.metric.management.identify.securities.assemblies.MetricLogoutHandler;
import com.quicksand.bigdata.metric.management.identify.securities.vos.IdentifyUserDetails;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.OperationLogUtils;
import com.quicksand.bigdata.vars.security.consts.VarsSecurityConsts;
import com.quicksand.bigdata.vars.security.service.VarsSecurityJudger;
import com.quicksand.bigdata.vars.security.service.VarsSecurityPersistenceService;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MetricsLogoutHandlerImpl
 *
 * @author page
 * @date 2020/8/25 09:40
 */
@Slf4j
@Component
public class MetricsLogoutHandlerImpl
        implements MetricLogoutHandler {

    final static String LOGOUTSUCCESS_REDIRECT_URL = "/login?logout";
    @Resource
    VarsSecurityJudger varsSecurityJudger;
    @Resource
    OperationLogService operationLogService;
    @Resource
    VarsSecurityPersistenceService varsSecurityPersistenceService;

    private void cleanAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (null != authentication) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserSecurityDetails) {
                UserSecurityDetails usd = (UserSecurityDetails) principal;
                usd.setConsensus(false);
                usd.setValidation(false);
                //移除token
                if (!StringUtils.isEmpty(usd.getTokenKey())) {
                    //删除共享的token
                    varsSecurityPersistenceService.remove(usd.getTokenKey());
                }
                log.info("user logout! userName:{}`userId:{}`", usd.getId(), usd.getName());
                //记退出日志
                operationLogService.log(OperationLogUtils.buildLogoutLog(authentication));
                //移除cookie
                if (varsSecurityJudger.useCookie()) {
                    Cookie cookie = new Cookie(VarsSecurityConsts.KEY_COOKIES_AUTH, null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
            //标记退出
            if (principal instanceof IdentifyUserDetails) {
                ((IdentifyUserDetails) principal).setLogout(true);
            }
        }
    }

    @SneakyThrows
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        cleanAuthentication(request, response, authentication);
        request.getSession().invalidate();
        //GatewayConsts.redirectTo(request, response, LOGOUTSUCCESS_REDIRECT_URL);
    }

}
