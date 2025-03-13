package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.vars.http.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * HttpStatusHandler
 *
 * @author page
 * @date 2022/7/26
 */
@Slf4j
@Aspect
@Component
public class HttpStatusAdvice {

    @Resource
    HttpServletResponse httpServletResponse;
    @Resource
    HttpServletRequest httpServletRequest;

    @SuppressWarnings("rawtypes")
    @AfterReturning(value = "execution (public com.quicksand.bigdata.vars.http.model.Response com.quicksand.bigdata.metric.management.*.rests.*.*(..)) ", returning = "retVal")
    public void modifyHttpStatus(Response retVal) {
        if (!httpServletRequest.getRequestURI().startsWith(Vars.PATH_ROOT + "/amis/")) {
            if (null != retVal
                    && StringUtils.hasText(retVal.getCode())) {
                httpServletResponse.getStatus();
                try {
                    int modifyCode = Integer.parseInt(retVal.getCode());
                    httpServletResponse.setStatus(modifyCode);
                } catch (Exception e) {
                    log.warn("HttpStatusAdvice fail ! ", e);
                }
            }
        }
    }

}
