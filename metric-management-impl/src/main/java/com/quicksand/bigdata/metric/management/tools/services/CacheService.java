package com.quicksand.bigdata.metric.management.tools.services;

import com.quicksand.bigdata.metric.management.metric.vos.CommonDownListVO;

import java.util.List;

/**
 * CacheService
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/15 16:57
 * @description
 */
public interface CacheService {
//    List<CommonDownListVO> getAppsDownList() ;

    List<CommonDownListVO> getAppMetricsDownList(int appId);
}
