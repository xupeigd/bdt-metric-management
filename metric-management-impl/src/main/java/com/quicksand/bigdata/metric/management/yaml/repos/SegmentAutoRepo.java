package com.quicksand.bigdata.metric.management.yaml.repos;

import com.quicksand.bigdata.metric.management.consts.YamlSegmentType;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SegmentAutoRepo
 *
 * @author page
 * @date 2022/8/1
 */
@Repository
public interface SegmentAutoRepo
        extends JpaRepository<SegmentDBVO, Long> {

    /**
     * 根据数据集Id查找片段
     *
     * @param type      YamlSegmentType
     * @param datasetId 数据集合Id
     * @return list of SegmentDBVO / empty list
     */
    List<SegmentDBVO> findAllByTypeAndDatasetIdOrderByVersionDesc(YamlSegmentType type, int datasetId);

    /**
     * 根据数据集Id查找片段
     *
     * @param type     YamlSegmentType
     * @param metricId 指标Id
     * @return list of SegmentDBVO / empty list
     */
    List<SegmentDBVO> findAllByTypeAndMetricIdOrderByVersionDesc(YamlSegmentType type, int metricId);


    /**
     * 根据数据集Id查找片段
     *
     * @param type     YamlSegmentType
     * @param metricId 指标Id
     * @return list of SegmentDBVO / empty list
     */
    @Query(value = " SELECT * from t_yaml_segments where type= :type and metric_id = :metricId ORDER  by version desc",
            nativeQuery = true)
    List<SegmentDBVO> findAllVersionByTypeAndMetricId(int type, int metricId);

}
