package com.quicksand.bigdata.metric.management.job.rests.interceptor;

import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.job.core.model.JobUser;
import com.quicksand.bigdata.metric.management.job.core.model.ProxyUser;
import com.quicksand.bigdata.metric.management.job.core.util.I18nUtil;
import com.quicksand.bigdata.metric.management.job.repos.ProxyUserAutoRepo;
import com.quicksand.bigdata.metric.management.job.rests.annotation.PermissionLimit;
import com.quicksand.bigdata.metric.management.job.services.LoginService;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.GatewayConsts;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限拦截
 *
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class PermissionInterceptor
        extends HandlerInterceptorAdapter {

    @Resource
    ProxyUserAutoRepo proxyUserAutoRepo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        // if need login
        boolean needLogin = true;
        boolean needAdminuser = false;
        HandlerMethod method = (HandlerMethod) handler;
        PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
        if (permission != null) {
            needLogin = permission.limit();
            needAdminuser = permission.adminuser();
        }
        if (needLogin) {
            UserSecurityDetails userDetail = AuthUtil.getUserDetail();
            if (null == userDetail || 0 >= userDetail.getId()) {
                GatewayConsts.redirectTo(request, response, "/login");
                return false;
            }
            ProxyUser proxyUser = proxyUserAutoRepo.findByUserId(userDetail.getId());
            if (needAdminuser
                    && !AuthUtil.isAdmin()
                    && (null != proxyUser && 1 != proxyUser.getRole())) {
                throw new RuntimeException(I18nUtil.getString("system_permission_limit"));
            }
            JobUser build = JobUser.builder()
                    .id(userDetail.getId())
                    .username(userDetail.getUsername())
                    .role(null != proxyUser ? proxyUser.getRole() : (AuthUtil.isAdmin() ? 1 : 0))
                    // .role(AuthUtil.isAdmin() ? 1 : (null == proxyUser ? 0 : proxyUser.getRole()))
                    .permission(null == proxyUser ? "" : proxyUser.getPermission())
                    .build();
            request.setAttribute(LoginService.LOGIN_IDENTITY_KEY, build);
        }
        return super.preHandle(request, response, handler);
    }

}
