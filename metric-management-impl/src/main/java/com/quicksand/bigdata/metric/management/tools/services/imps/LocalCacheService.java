package com.quicksand.bigdata.metric.management.tools.services.imps;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.quicksand.bigdata.metric.management.apis.services.AppService;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.vos.CommonDownListVO;
import com.quicksand.bigdata.metric.management.tools.services.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * LocalCache
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/15 16:55
 * @description
 */
@Service
@Slf4j
public class LocalCacheService implements CacheService {

    public static final LoadingCache<String, List<CommonDownListVO>> downListCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<CommonDownListVO>>() {
                @Override
                public List<CommonDownListVO> load(String key) {
                    return Collections.emptyList();
                }
            });

    @Resource
    MetricDataManager metricDataManager;
    @Resource
    AppService appService;

//    @Override
//    public List<CommonDownListVO> getAppsDownList() {
//        String key = "appsDownList";
//        List<CommonDownListVO> commonDownListVOS = new ArrayList<>();
//        try {
//            commonDownListVOS = downListCache.get(key);
//        } catch (ExecutionException e) {
//            log.error("getMetricsDownList 缓存取值异常:" + e);
//            return commonDownListVOS;
//        }
//        if (CollectionUtils.isEmpty(commonDownListVOS)) {
//            synchronized (downListCache) {
//                commonDownListVOS = appService.findUserApps();
//                downListCache.put(key, commonDownListVOS);
//            }
//        }
//        return commonDownListVOS;
//    }

    @Override
    public List<CommonDownListVO> getAppMetricsDownList(int appId) {
        String key = "metricsDownList_" + appId;
        List<CommonDownListVO> commonDownListVOS = new ArrayList<>();
        try {
            commonDownListVOS = downListCache.get(key);
        } catch (ExecutionException e) {
            log.error("getMetricsDownList 缓存取值异常:" + e);
            // return commonDownListVOS;
        }
        if (CollectionUtils.isEmpty(commonDownListVOS)) {
            commonDownListVOS = metricDataManager.findAppSurplusMetricsList(appId);
            synchronized (downListCache) {
                downListCache.put(key, commonDownListVOS);
            }
        }
        return commonDownListVOS;
    }
}
