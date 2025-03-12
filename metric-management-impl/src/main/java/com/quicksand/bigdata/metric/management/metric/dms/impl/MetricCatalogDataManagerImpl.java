package com.quicksand.bigdata.metric.management.metric.dms.impl;

import com.quicksand.bigdata.metric.management.consts.DomainType;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricCatalogDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricCatalogDataManager;
import com.quicksand.bigdata.metric.management.metric.repos.MetricCatalogAutoRepo;
import com.quicksand.bigdata.metric.management.metric.vos.MetricCatalogGroupVO;
import com.quicksand.bigdata.metric.management.utils.JPAModelMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * MetricCatalogDataManagerImpl
 *
 * @author page
 * @date 2022-07-31
 */
@Slf4j
@Component
public class MetricCatalogDataManagerImpl
        implements MetricCatalogDataManager {

    @Resource
    MetricCatalogAutoRepo metricCatalogAutoRepo;

    @Override
    public List<MetricCatalogDBVO> findByParentCode(String code) {
        return metricCatalogAutoRepo.findAllByParentCodeOrderById(code);
    }

    @Override
    public List<MetricCatalogDBVO> findAllCatalogs() {
        return metricCatalogAutoRepo.findAll(Sort.by("name"));
    }

    @Override
    public MetricCatalogDBVO findById(int id) {
        return metricCatalogAutoRepo.findById(id);
    }

    @Override
    public List<MetricCatalogDBVO> findByParentId(int parentId) {
        return metricCatalogAutoRepo.findByParentId(parentId);
    }

    @Override
    public void saveCatalogs(List<MetricCatalogDBVO> updates) {
        metricCatalogAutoRepo.saveAll(updates);
    }

    @Override
    public MetricCatalogDBVO findByName(String name) {
        return metricCatalogAutoRepo.findByName(name);
    }

    @Override
    public MetricCatalogDBVO findByBusinessCode(String businessCode) {
        return metricCatalogAutoRepo.findByBusinessCode(businessCode);
    }

    @Override
    public List<MetricCatalogGroupVO> findMetricCount() {
        List<Object[]> catalogGroupMetricCount = metricCatalogAutoRepo.findCatalogGroupMetricCount();
        return JPAModelMapUtils.mapping(catalogGroupMetricCount, MetricCatalogGroupVO.class);
    }

    @Override
    public List<MetricCatalogDBVO> findAllByTypeAndParentIds(DomainType type, List<Integer> parentIds) {
        return metricCatalogAutoRepo.findAllByTypeAndParentIdInOrderByName(type, parentIds);
    }

    @Override
    public List<MetricCatalogDBVO> findAllByType(DomainType type) {
        return metricCatalogAutoRepo.findAllByTypeOrderByName(type);
    }

    @Override
    public List<MetricCatalogDBVO> findCatalogs(Collection<Integer> ids) {
        return metricCatalogAutoRepo.findAllById(ids);
    }

}
