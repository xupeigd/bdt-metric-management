package com.quicksand.bigdata.metric.management.yaml.services;

import com.quicksand.bigdata.metric.management.consts.YamlSegmentType;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.yaml.vos.DatasetSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.SegmentVO;

/**
 * YamlService
 *
 * @author page
 * @date 2022/8/1
 */
public interface YamlService {

    /**
     * 数据集转换为片段
     *
     * @param datasetVO 数据集vo
     * @return instance of SegmentVO
     */
    SegmentVO transform2Segement(DatasetVO datasetVO);

    DatasetSegment getDatasetSegment(DatasetVO datasetDBVO);

    //MetricMergeSegment getMetricMergeSegment(MetricVO metricVO);

    /**
     * 指标转换为片段
     *
     * @param metricVO 指标vo
     * @return instance of SegmentVO
     */
    SegmentVO transform2Segement(MetricVO metricVO, YamlSegmentType type);

    /**
     * 创建/更新yaml片段
     *
     * @param datasetVO 数据集实体
     * @return instance of SegmentVO
     */
    SegmentVO upsertSegment(DatasetVO datasetVO);

    /**
     * 创建/更新yaml片段
     *
     * @param metricVO 指标实体
     * @return instance of SegmentVO
     */
    SegmentVO upsertSegment(MetricVO metricVO, YamlSegmentType type);

    /**
     * 按照dataset移除片段
     *
     * @param datasetId 数据集Id
     */
    void removeSegmentByDatasetId(int datasetId);

    /**
     * 根据指标id和片段内容获取Segment内容
     *
     * @param type     数据集Id
     * @param metricId 指标Id
     * @return instance of SegmentVO
     */
    SegmentVO getLastEnableSegmentByMetricIdAndType(YamlSegmentType type, Integer metricId);

}
