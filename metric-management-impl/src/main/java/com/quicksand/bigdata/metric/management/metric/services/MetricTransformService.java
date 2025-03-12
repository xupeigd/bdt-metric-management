package com.quicksand.bigdata.metric.management.metric.services;

import com.quicksand.bigdata.metric.management.yaml.vos.DataSourceSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.DatasetSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricMergeSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricSegmentVO;

/**
 * MetricTransformService
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/2 17:29
 * @description
 */
public interface MetricTransformService {
    /**
     * 将用户配置yaml转换为MetricMergeSegment
     *
     * @param yamlContent yaml字符串
     * @return MetricMergeSegment
     */
    MetricMergeSegment convertYamlToSegment(String yamlContent);


    /**
     * 将MetricMergeSegment转化为用户配置yaml
     *
     * @param metricMergeSegment 实体
     * @return String
     */
    String convertSegmentToYaml(MetricMergeSegment metricMergeSegment);


    /**
     * 将用户配置yaml转化为DatasetSegment
     *
     * @param yamlContent 用户配置yaml内容
     * @return String
     */
    DatasetSegment transformToDataSet(String yamlContent);


    /**
     * 将用户配置yaml转化为DataSourceSegment
     *
     * @param yamlContent 用户配置yaml内容
     * @return String
     */
    DataSourceSegment transformToDataSoucre(String yamlContent);

    /**
     * 将用户配置yaml转化为MetricSegmentVO
     *
     * @param yamlContent 用户配置yaml内容
     * @return String
     */
    MetricSegmentVO transformToMetricVo(String yamlContent);

    /**
     * 将用户配置yaml转化为标准执行yaml
     *
     * @param yamlContent 用户配置yaml内容
     * @return String
     */
    String transformToExecuteYaml(String yamlContent);


}
