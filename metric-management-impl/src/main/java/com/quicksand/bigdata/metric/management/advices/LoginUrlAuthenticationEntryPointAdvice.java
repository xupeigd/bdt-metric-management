package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.vars.http.TraceId;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.GatewayConsts;
import com.quicksand.bigdata.vars.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LoginUrlAuthenticationEntryPointAdvice
 *
 * @author page
 * @date 2022/8/3
 */
@Slf4j
@Aspect
@Component
public class LoginUrlAuthenticationEntryPointAdvice {

    @Around(value = "execution (public * org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint.commence(..)) && args(request,response,authException)")
    public void modifyHttpStatus(ProceedingJoinPoint joinPoint, HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws Throwable {
        if (!StringUtils.hasText(request.getHeader(TraceId.FLAG))) {
            GatewayConsts.redirectTo(request, response, "/login");
        } else {
            if (authException instanceof InsufficientAuthenticationException) {
                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.setStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
                response.getWriter().write(JsonUtils.toJsonString(Response.response(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "未登陆/授权信息已过期，请重新登陆！")));
            } else {
                joinPoint.proceed();
            }
        }
    }

}
