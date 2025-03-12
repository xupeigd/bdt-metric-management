package com.quicksand.bigdata.metric.management.yaml.services;

import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricMergeSegment;

import java.util.List;

/**
 * VarsMetricSegmentService
 * (指标通用的片段处理服务)
 *
 * @author page
 * @date 2022/10/21
 */
public interface VarsMetricSegmentService {

    /**
     * 直接转换为片段内容
     *
     * @param metrics 同数据集的指标
     * @param dataset 数据集信息
     * @return MetricSegment.Metric
     */
    MetricMergeSegment cover2Segment(List<MetricVO> metrics, DatasetDBVO dataset);

}
