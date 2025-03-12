package com.quicksand.bigdata.metric.management.metric.repos;

import com.quicksand.bigdata.metric.management.consts.DomainType;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricCatalogDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MetricCatalogAutoRepo
 *
 * @author page
 * @date 2022-07-31
 */
@Repository
public interface MetricCatalogAutoRepo
        extends JpaRepository<MetricCatalogDBVO, Integer> {

    /**
     * 按父节点的code查询节点数据
     *
     * @param code 父节点的code
     * @return list of MetricCatalogDBVO
     */
    List<MetricCatalogDBVO> findAllByParentCodeOrderById(String code);

    /**
     * 按id检索
     *
     * @param id 主键
     * @return instance of MetricCatalogDBVO
     */
    MetricCatalogDBVO findById(int id);

    /**
     * 按父节点的id查询节点数据
     *
     * @param parentId 父节点的id
     * @return list of MetricCatalogDBVO
     */
    List<MetricCatalogDBVO> findByParentId(int parentId);

    /**
     * 按name检索
     *
     * @param name 名称字段
     * @return instance of MetricCatalogDBVO
     */
    MetricCatalogDBVO findByName(String name);

    /**
     * 按业务编码检索
     *
     * @param businessCode 业务编码字段
     * @return instance of MetricCatalogDBVO
     */
    MetricCatalogDBVO findByBusinessCode(String businessCode);

    /**
     * 获取各个目录下指标个数
     *
     * @return object[]
     */
    @Query(value = "SELECT business_id,COUNT(1)   from t_metric_metrics where status =1 GROUP by business_id  " +
            "UNION  all  " +
            "SELECT topic_id ,COUNT(1)   from t_metric_metrics where status =1 GROUP by topic_id", nativeQuery = true)
    List<Object[]> findCatalogGroupMetricCount();

    /**
     * 按父节点的ids和类型查询节点数据
     *
     * @param type 目录类型
     * @return list of MetricCatalogDBVO
     */
    List<MetricCatalogDBVO> findAllByTypeAndParentIdInOrderByName(DomainType type, List<Integer> parentIds);

    /**
     * 按类型查询数据
     *
     * @param type 目录类型
     * @return list of MetricCatalogDBVO
     */
    List<MetricCatalogDBVO> findAllByTypeOrderByName(DomainType type);

}
