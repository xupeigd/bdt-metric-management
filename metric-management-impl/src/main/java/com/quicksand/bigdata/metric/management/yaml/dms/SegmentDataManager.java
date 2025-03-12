package com.quicksand.bigdata.metric.management.yaml.dms;

import com.quicksand.bigdata.metric.management.consts.YamlSegmentType;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;

import java.util.List;

/**
 * SegmentDataManager
 *
 * @author page
 * @date 2022/8/1
 */
public interface SegmentDataManager {

    /**
     * 根据datasetId查找segment list
     *
     * @param datasetId 数据集Id
     * @return list of SegmentDBVO / empty list
     */
    List<SegmentDBVO> findSegmentByDatasetId(int datasetId);


    /**
     * 根据metricId查找segment list
     *
     * @param type     片段类型
     * @param metricId 指标id
     * @return list of SegmentDBVO / empty list
     */
    List<SegmentDBVO> findSegmentByMetricId(YamlSegmentType type, int metricId);

    /**
     * 批量更新segments
     *
     * @param segments list of SegmentDBVO
     */
    void updateSegments(List<SegmentDBVO> segments);

    /**
     * 创建segment并返回保存后的对象
     *
     * @param segment instance of SegmentDBVO
     * @return instance of SegmentDBVO
     */
    SegmentDBVO createSegment(SegmentDBVO segment);


    /**
     * 根据metricId查找所有版本的segment list
     *
     * @param type     片段类型
     * @param metricId 指标id
     * @return list of SegmentDBVO / empty list
     */
    List<SegmentDBVO> findAllVersionSegmentByMetricId(YamlSegmentType type, int metricId);

}
