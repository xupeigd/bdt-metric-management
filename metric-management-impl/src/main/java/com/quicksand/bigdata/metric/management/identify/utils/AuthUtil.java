package com.quicksand.bigdata.metric.management.identify.utils;

import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

/**
 * AuthUtil
 *
 * @author page
 * @date 2022/8/1
 */
public final class AuthUtil {

    private AuthUtil() {

    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static UserSecurityDetails getUserDetail() {
        Authentication authentication = getAuthentication();
        if (null != authentication
                && null != authentication.getPrincipal()
                && authentication.getPrincipal() instanceof UserSecurityDetails) {
            return (UserSecurityDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static boolean isAdmin() {
        Authentication authentication = getAuthentication();
        if (null == authentication) {
            return false;
        }
        SecurityExpressionRoot securityExpressionRoot = new SecurityExpressionRoot(authentication) {
        };
        return securityExpressionRoot.hasAnyRole("ADMIN");
    }

    public static boolean isAdmin(UserDBVO user) {
        if (null != user && !CollectionUtils.isEmpty(user.getRoles())) {
            for (RoleDBVO role : user.getRoles()) {
                if ("ADMIN".equalsIgnoreCase(role.getCode())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean hasAuthority(String authority) {
        Authentication authentication = getAuthentication();
        if (null == authentication) {
            return false;
        }
        SecurityExpressionRoot securityExpressionRoot = new SecurityExpressionRoot(authentication) {
        };
        return securityExpressionRoot.hasAuthority(authority);
    }

}
