package com.quicksand.bigdata.metric.management.metric.dms;

import com.quicksand.bigdata.metric.management.consts.DomainType;
import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.models.MetricQueryModel;
import com.quicksand.bigdata.metric.management.metric.vos.AppInvokeDetailVO;
import com.quicksand.bigdata.metric.management.metric.vos.CommonDownListVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import java.util.Collection;
import java.util.List;

/**
 * MetricDataManager
 *
 * @author page
 * @date 2022-07-27
 */
public interface MetricDataManager {

    MetricDBVO findByMetricId(int metricId);

    void updateMetric(MetricDBVO metricDBVO);

    MetricDBVO saveMetric(MetricDBVO metricDBVO);

    List<MetricDBVO> findMetricByName(@Nullable String name, @Null Integer datasetId);

    Page<MetricDBVO> queryAllMetricsByConditions(MetricQueryModel queryModel, Pageable pageable);

    List<MetricDBVO> findMetricsByDomain(DomainType domainType, int domainId);

    List<MetricDBVO> findBySerialNumber(String serialNumber);

    List<MetricDBVO> findByMetricIds(Collection<Integer> ids);

    List<CommonDownListVO> findAppSurplusMetricsList(int appId);

    Page<AppInvokeDetailVO> findAppInvokeInfoByMetricId(int metricId, Pageable pageable);

    List<MetricDBVO> findByMetricsByPubsubState(PubsubStatus pubsubStatus);

    String getMaxSerialNumberByTopicAndBusiness(Integer topicId, Integer businessId);

    List<MetricDBVO> findByEnNameOrCnName(String enName, String cnName);

    MetricDBVO findByEnName(String name);

    MetricDBVO findMetricByEnNameOrSerialNumber(String name, String serialNumber);

}
