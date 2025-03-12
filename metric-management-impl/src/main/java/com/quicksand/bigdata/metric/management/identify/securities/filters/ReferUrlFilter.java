package com.quicksand.bigdata.metric.management.identify.securities.filters;

import com.quicksand.bigdata.vars.util.GatewayConsts;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * ReferUrlFilter
 *
 * @author page
 * @date 2022/7/25
 */
@Component
public class ReferUrlFilter
        extends HttpFilter {

    public static final String REFER_URL = "refer";

    public static final String EXECLUDED_URL_SUFFIX_LOGIN = "login";
    public static final String EXECLUDED_URL_SUFFIX_LOGOUT = "/logout";


    boolean isResources(String uri) {
        return uri.contains(".css")
                || uri.contains(".js")
                || uri.contains(".ico")
                || uri.contains(".jpg")
                || uri.contains(".png")
                || uri.contains("woff2")
                || uri.endsWith("/error");
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String reqUrl = request.getRequestURI();
        if (StringUtils.hasText(reqUrl)
                && !isResources(reqUrl)
                && !reqUrl.endsWith(EXECLUDED_URL_SUFFIX_LOGIN)
                && !reqUrl.endsWith(EXECLUDED_URL_SUFFIX_LOGOUT)) {
            //判非资源
            request.getSession().setAttribute(REFER_URL, reqUrl);
        }
        String refUrl = request.getParameter(REFER_URL);
        if (StringUtils.hasText(refUrl)
                && !isResources(refUrl)
                && null == request.getSession().getAttribute(REFER_URL)) {
            request.getSession().setAttribute(REFER_URL, refUrl);
        }
        if (Objects.equals("/login", request.getRequestURI())
                && RequestMethod.GET.name().equals(request.getMethod())
                && StringUtils.hasText(request.getParameter(REFER_URL))) {
            GatewayConsts.redirectTo(request, response, "/login");
            return;
        }
        if (EXECLUDED_URL_SUFFIX_LOGOUT.equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod())) {
            response.sendRedirect("/identify/logout");
        } else {
            super.doFilter(request, response, chain);
        }
    }

}
