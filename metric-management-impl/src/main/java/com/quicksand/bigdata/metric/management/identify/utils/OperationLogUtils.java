package com.quicksand.bigdata.metric.management.identify.utils;

import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OperationLogUtils
 *
 * @author page
 * @date 2020/8/25 22:58
 */
public final class OperationLogUtils {

    static OperationLogDBVO createAuthenticationBaseLog(Authentication authentication) {
        OperationLogDBVO operationLogDBVO = new OperationLogDBVO();
        Object principal = null == authentication ? null : authentication.getPrincipal();
        if (principal instanceof UserSecurityDetails) {
            UserSecurityDetails userSecurityDetails = (UserSecurityDetails) principal;
            operationLogDBVO.setUserId(userSecurityDetails.getId());
        } else {
            operationLogDBVO.setUserId(0);
        }
        if (null != authentication
                && authentication.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) authentication.getDetails();
            operationLogDBVO.setIp(webAuthenticationDetails.getRemoteAddress());
            operationLogDBVO.setAddress("未知");
        } else {
            operationLogDBVO.setIp("0.0.0.0");
            operationLogDBVO.setAddress("未知");
        }
        operationLogDBVO.setOperationTime(new Date());
        operationLogDBVO.setType(OperationLogType.DEFAULT);
        return operationLogDBVO;
    }

    static OperationLogDBVO createUserIdBaseLog(Authentication authentication, int userId) {
        OperationLogDBVO operationLogDBVO = new OperationLogDBVO();
        operationLogDBVO.setUserId(userId);
        operationLogDBVO.setOperationTime(new Date());
        operationLogDBVO.setType(OperationLogType.DEFAULT);
        Object details = null == authentication ? null : authentication.getDetails();
        if (details instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) details;
            operationLogDBVO.setIp(webAuthenticationDetails.getRemoteAddress());
            operationLogDBVO.setAddress("未知");
        } else {
            operationLogDBVO.setIp("0.0.0.0");
            operationLogDBVO.setAddress("未知");
        }
        return operationLogDBVO;
    }

    public static OperationLogDBVO buildCreateUserLog(Authentication authentication, String name, String email, String password,
                                                      List<RoleVO> roles) {
        OperationLogDBVO operationLogDBVO = createAuthenticationBaseLog(authentication);
        operationLogDBVO.setDetail(String.format("创建用户，用户名:%s,邮箱:%s,角色:%s", name, email,
                StringUtils.collectionToDelimitedString(roles.stream().map(RoleVO::getName).collect(Collectors.toList()), ",")));
        return operationLogDBVO;
    }

    public static OperationLogDBVO buildUserModifyLog(Authentication authentication, UserVO userVO) {
        OperationLogDBVO operationLogDBVO = createAuthenticationBaseLog(authentication);
        operationLogDBVO.setDetail(String.format("修改用户数据，数据:%s", JsonUtils.toString(userVO)));
        return operationLogDBVO;
    }

    public static OperationLogDBVO buildLoginLog(Authentication authentication, String name, UserVO userVO, boolean jwtSigned) {
        OperationLogDBVO log = createUserIdBaseLog(authentication, userVO.getId());
        log.setDetail(String.format("登陆，用户名:%s,用户Id:%s,jwt签发:%s", name, userVO.getId(), jwtSigned));
        return log;
    }

    public static OperationLogDBVO buildLogoutLog(Authentication authentication) {
        OperationLogDBVO log = createAuthenticationBaseLog(authentication);
        log.setType(OperationLogType.LOGOUT);
        log.setDetail("登出系统。");
        return log;
    }

    public static OperationLogDBVO buildLoginLog(Authentication authentication) {
        OperationLogDBVO log = createAuthenticationBaseLog(authentication);
        log.setType(OperationLogType.LOGIN);
        log.setDetail("登陆系统（Form Login）");
        return log;
    }
}
