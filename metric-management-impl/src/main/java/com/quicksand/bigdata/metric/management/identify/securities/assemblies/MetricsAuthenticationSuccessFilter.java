package com.quicksand.bigdata.metric.management.identify.securities.assemblies;

import com.quicksand.bigdata.metric.management.identify.securities.vos.IdentifyUserDetails;
import com.quicksand.bigdata.vars.jwt.JwtToken;
import com.quicksand.bigdata.vars.security.vos.SecurityAlgorithmSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * MetricsAuthenticationSuccessFilter
 *
 * @author page
 * @date 2020/8/24 14:32
 */
@Slf4j
@Component
public class MetricsAuthenticationSuccessFilter
        extends GenericFilterBean {

    @Value("${spring.application.name:-IDENTIFY}")
    private String applicationName;

    public static JwtToken.Header genernateHeader(SecurityAlgorithmSetting algorithmSetting,
                                                  IdentifyUserDetails identifyUserDetails, String source) {
        return JwtToken.Header.builder()
                .type("JWT")
                .alg(algorithmSetting.getAlg())
                .signKey(algorithmSetting.getDk())
                .iat(System.currentTimeMillis())
                .platform(source)
                .orgination(source)
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(request, response);
    }

}
