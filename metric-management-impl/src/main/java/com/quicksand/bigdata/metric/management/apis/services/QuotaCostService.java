package com.quicksand.bigdata.metric.management.apis.services;

import com.quicksand.bigdata.metric.management.apis.models.QuotaCostModel;
import com.quicksand.bigdata.metric.management.apis.vos.QuotaCostVO;

import java.util.List;

/**
 * QuotaCostService
 *
 * @author page
 * @date 2022/12/14
 */
public interface QuotaCostService {

    /**
     * fetch当前消耗的配额数据
     *
     * @param appId    appId
     * @param metricId 指标id
     * @return list of QuotaCostVO
     */
    List<QuotaCostVO> fetchCurQuotaCosts(Integer appId, Integer metricId);

    /**
     * @param models
     */
    void syncQuotaCosts(List<QuotaCostModel> models);

}
