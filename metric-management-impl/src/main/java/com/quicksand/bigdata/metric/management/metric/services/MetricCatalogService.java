package com.quicksand.bigdata.metric.management.metric.services;

import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModifyModel;
import com.quicksand.bigdata.metric.management.metric.vos.MetricCatalogVO;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * MetricCatalogService
 *
 * @author page
 * @date 2022-07-31
 */
public interface MetricCatalogService {


    /**
     * 按父级别code查询catalog实体数据
     *
     * @param parentCode 父级code
     * @param mode       模式 0仅当级 1带子 2 带父 可选 默认1
     * @param type
     * @return List of MetricCatalogVO / emptyList
     */
    List<MetricCatalogVO> queryCatalogs(String parentCode, int mode, int type);

    /**
     * 从根开始查询catalog实体数据
     *
     * @param mode 模式 0仅当级 1带子 2 带父 可选 默认1
     * @param type
     * @return List of MetricCatalogVO / emptyList
     */
    List<MetricCatalogVO> queryCatalogs(int mode, int type);

    /**
     * 根据Id查找目录节点
     *
     * @param id 节点主键
     * @return instance of MetricCatalogVO
     */
    MetricCatalogVO findCatalog(int id);

    /**
     * 新增/修改 指标目录节点
     *
     * @param model 修改/创建参数
     * @return instance of MetricCatalogVO
     */
    MetricCatalogVO upsertCatalog(MetricCatalogModifyModel model);

    /**
     * 保存MetricCatalog
     *
     * @param parentCatalog instance of MetricCatalogVO
     */
    void saveMetricCatalog(MetricCatalogVO parentCatalog);

    /**
     * 根据节点名称查找节点
     *
     * @param name 名称
     * @return null/instance of MetricCatalogVO
     */
    MetricCatalogVO findCatalog(String name);

    /**
     * 根据节点业务编码查找节点
     *
     * @param businessCode 名称
     * @return null/instance of MetricCatalogVO
     */
    MetricCatalogVO findCatalogByBusinessCode(String businessCode);

    /**
     * 根据父级点查询业务域
     *
     * @param parentIds 父节点ids
     * @return List of MetricCatalogVO / emptyList
     */
    List<MetricCatalogVO> queryBusinessCatalogsByParentIds(List<Integer> parentIds);

    @Transactional
    void removeCatalogs(Collection<Integer> ids);

}
