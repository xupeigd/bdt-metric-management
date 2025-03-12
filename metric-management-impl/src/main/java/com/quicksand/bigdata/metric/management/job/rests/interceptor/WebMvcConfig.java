package com.quicksand.bigdata.metric.management.job.rests.interceptor;

import com.quicksand.bigdata.metric.management.job.BasePath;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * web mvc config
 *
 * @author xuxueli 2018-04-02 20:48:20
 */
@Configuration
public class WebMvcConfig
        implements WebMvcConfigurer {

    @Resource
    private PermissionInterceptor permissionInterceptor;
    @Resource
    private CookieInterceptor cookieInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns(BasePath.BASE_PATH + "/**");
        registry.addInterceptor(cookieInterceptor)
                .addPathPatterns(BasePath.BASE_PATH + "/**");
    }

}