package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.vars.concurrents.TraceFuture;
import com.quicksand.bigdata.vars.security.service.AppRequestDuplicateChecker;
import com.quicksand.bigdata.vars.security.vos.AppRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author page
 * @date 2022/11/7
 */
@Slf4j
@Service
public class AppRequestDuplicateCheckerImpl
        implements AppRequestDuplicateChecker {

    static final String DUPLICATE_KEY = "VARS:ARD:%s";

    @Resource
    RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public boolean duplcate(AppRequestVO req, String traceId) {
        if (null == req || !StringUtils.hasText(req.getSignKey())
                || System.currentTimeMillis() >= req.getSignMills() + 60 * 1000L) {
            return true;
        }
        traceId = StringUtils.hasText(traceId) ? traceId : "DEFAULT";
        String duplcateKey = String.format(DUPLICATE_KEY, req.getSignKey());
        //先进行放置
        stringRedisTemplate.opsForZSet().add(duplcateKey, traceId, System.currentTimeMillis());
        Long size = stringRedisTemplate.opsForZSet().size(duplcateKey);
        TraceFuture.run(() -> stringRedisTemplate.expire(duplcateKey, 30L, TimeUnit.MINUTES));
        if (null != size && 1L == size) {
            return false;
        }
        return true;
    }

}
