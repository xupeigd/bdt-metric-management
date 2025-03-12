package com.quicksand.bigdata.metric.management.metric.dms;

import com.quicksand.bigdata.metric.management.consts.DomainType;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricCatalogDBVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricCatalogGroupVO;

import java.util.Collection;
import java.util.List;

/**
 * MetricCatalogDataManager
 *
 * @author page
 * @date 2022-07-31
 */
public interface MetricCatalogDataManager {

    List<MetricCatalogDBVO> findByParentCode(String code);

    List<MetricCatalogDBVO> findAllCatalogs();

    MetricCatalogDBVO findById(int id);

    List<MetricCatalogDBVO> findByParentId(int parentId);

    void saveCatalogs(List<MetricCatalogDBVO> updates);

    MetricCatalogDBVO findByName(String name);

    MetricCatalogDBVO findByBusinessCode(String businessCode);

    /**
     * 根据节点查询包含指标个数
     *
     * @return null/instance of MetricCatalogVO
     */
    List<MetricCatalogGroupVO> findMetricCount();


    List<MetricCatalogDBVO> findAllByTypeAndParentIds(DomainType type, List<Integer> parentIds);

    List<MetricCatalogDBVO> findAllByType(DomainType type);

    List<MetricCatalogDBVO> findCatalogs(Collection<Integer> ids);

}
