package com.quicksand.bigdata.metric.management.configs;

import com.quicksand.bigdata.metric.management.identify.securities.assemblies.*;
import com.quicksand.bigdata.metric.management.identify.securities.filters.ReferUrlFilter;
import com.quicksand.bigdata.metric.management.job.services.LoginService;
import com.quicksand.bigdata.vars.security.DefaultVarsSecurityContextPersistenceFilter;
import com.quicksand.bigdata.vars.security.VarsSecurityContextPersistenceFilter;
import com.quicksand.bigdata.vars.security.consts.VarsSecurityConsts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.annotation.Resource;

/**
 * SecurityConfiguration
 *
 * @author page
 * @date 2020/8/20 18:38
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration
        extends WebSecurityConfigurerAdapter {

    @Value("${bdt.management.jobs.enable:true}")
    boolean metricJobEnbale;
    @Value("${bdt.management.jobs.manage.enable:false}")
    boolean metricJobManageEnbale;

    @Resource
    ReferUrlFilter referUrlFilter;
    @Resource
    MetricLogoutHandler metricLogoutHandler;
    @Resource
    IdentifyPasswordEncoder identifyPasswordEncoder;
    @Resource
    IdentifyUserDetailSercvice identifyUserDetailSercvice;
    @Resource
    MetricFormLoginSuccessHandler metricFormLoginSuccessHandler;
    @Resource
    MetricsAuthenticationSuccessFilter metricsAuthenticationSuccessFilter;
    @Resource
    DefaultVarsSecurityContextPersistenceFilter defaultVarsSecurityContextPersistenceFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(identifyUserDetailSercvice)
                .passwordEncoder(identifyPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //放行资源
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //来源url拦截器，用于记录来源url
        http.addFilterBefore(referUrlFilter, ChannelProcessingFilter.class);
        //前置拦截器(用于加载其他来源的token/安全标识)
        http.addFilterBefore(defaultVarsSecurityContextPersistenceFilter, SecurityContextPersistenceFilter.class);
        //虚位过滤器（用于扩展）
        http.addFilterAfter(metricsAuthenticationSuccessFilter, VarsSecurityContextPersistenceFilter.class);

        http.csrf().disable()
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/", "/error", "/health", "/monitor/health").permitAll()
                .antMatchers("/login", "/identify/jwtLogin", "/identify/login").permitAll()
                .antMatchers("/logout", "/identify/logout", "/identify/logout/**").permitAll()
                .antMatchers("/admin-api/**").permitAll();

        if (metricJobEnbale) {
            http.authorizeRequests()
                    .antMatchers("/metric-jobs/api/**")
                    .permitAll();
        } else {
            http.authorizeRequests()
                    .antMatchers("/metric-jobs/api/**")
                    .hasRole("GODNESS");
        }
        if (!metricJobManageEnbale) {
            http.authorizeRequests()
                    .antMatchers("/metric-jobs/")
                    .hasRole("GODNESS");
        }

        http.authorizeRequests().and()
                .formLogin()
                .successHandler(metricFormLoginSuccessHandler);

        http.authorizeRequests().and()
                .logout()
                // .logoutRequestMatcher(new AntPathRequestMatcher("/identify/logout**"))
                .deleteCookies("JSESSIONID")
                .deleteCookies(VarsSecurityConsts.KEY_COOKIES_AUTH)
                .deleteCookies(LoginService.LOGIN_IDENTITY_KEY)
                .invalidateHttpSession(true)
                .addLogoutHandler(metricLogoutHandler);

        http.authorizeRequests()
                .antMatchers("/v2/**", "/v3/**", "**/webjars/**", "/swagger**", "/monitor/**")
                .hasAnyRole("ADMIN", "SYS", "DEV");

        http.authorizeRequests()
                .anyRequest().authenticated();

    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3200")); // 允许的前端域名
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 允许的 HTTP 方法
//        configuration.setAllowedHeaders(Collections.singletonList("*")); // 允许所有请求头
//        configuration.setAllowCredentials(true); // 允许携带凭证（如 Cookie）
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration); // 对所有路径生效
//        return source;
//    }

}