package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.vars.security.service.AppRequestFiller;
import com.quicksand.bigdata.vars.security.vos.AppRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author page
 * @date 2022/11/7
 */
@Slf4j
@Service
public class AppRequestFillerImpl
        implements AppRequestFiller {

    @Resource
    HttpServletRequest httpServletRequest;

    @Override
    public void fill(AppRequestVO appRequestVO) {
    }

}
