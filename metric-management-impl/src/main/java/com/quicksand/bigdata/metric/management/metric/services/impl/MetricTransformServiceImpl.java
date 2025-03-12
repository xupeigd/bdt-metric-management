package com.quicksand.bigdata.metric.management.metric.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.quicksand.bigdata.metric.management.consts.YamlSegmentKeys;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.metric.dms.MetricCatalogDataManager;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.services.MetricTransformService;
import com.quicksand.bigdata.metric.management.utils.YamlUtil;
import com.quicksand.bigdata.metric.management.yaml.services.YamlService;
import com.quicksand.bigdata.metric.management.yaml.vos.*;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * MetricTransformServiceImpl
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/2 17:29
 * @description
 */
@Service
@Slf4j
public class MetricTransformServiceImpl implements MetricTransformService {

    @Resource
    MetricDataManager metricDataManager;
    @Resource
    YamlService yamlService;
    @Resource
    UserDataManager userDataManager;
    @Resource
    MetricCatalogDataManager metricCatalogDataManager;
    @Resource
    DatasetDataManager datasetDataManager;


    @Override
    public String convertSegmentToYaml(MetricMergeSegment metricMergeSegment) {
        DataSourceSegment dataSourceSegment = metricMergeSegment.getData_source();
        DimensionsSegment dimensionsSegment = DimensionsSegment.builder().dimensions(metricMergeSegment.getDimensions()).build();
        MeasuresSegment measuresSegment = MeasuresSegment.builder().measures(metricMergeSegment.getMeasures()).build();
        MetricSegment.UserMetric metric = metricMergeSegment.getMetric();
        MetricSegment metricSegment = MetricSegment.builder().metric(metric).build();
        return YamlUtil.toYaml(dataSourceSegment) +
                YamlUtil.toYaml(dimensionsSegment) +
                YamlUtil.toYaml(measuresSegment) +
                YamlUtil.toYaml(metricSegment);
    }


    @Override
    public MetricMergeSegment convertYamlToSegment(String yamlContent) {
        MetricMergeSegment metricMergeSegment = MetricMergeSegment.builder().verifySuccess(false).build();        //分段落验证
        for (String segment : yamlContent.split(YamlSegmentKeys.SEGMENT_SEPARATOR)) {
            if (StringUtils.isBlank(segment)) {
                continue;
            }
            Try<JsonNode> obj = YamlUtil.yamlToObject(segment, JsonNode.class);
            Assert.isTrue(obj.isSuccess(), "yaml内容格式化错误");
            Iterator<String> fields = obj.get().fieldNames();
            while (fields.hasNext()) {
                String keys = fields.next();
                switch (keys) {
                    //验证数据源格式信息
                    case YamlSegmentKeys.DATASOURCE:
                        Try<DataSourceSegment> dataSourceSegments = YamlUtil.yamlToObject(segment, DataSourceSegment.class);
                        if (dataSourceSegments.isSuccess()) {
                            metricMergeSegment.setData_source(dataSourceSegments.get());
                        } else {
                            log.error("数据源信息解析异常，segment={}", segment);
                        }
                        break;
                    //验证度量信息
                    case YamlSegmentKeys.MEASURES:
                        Try<MeasuresSegment> measuresSegment = YamlUtil.yamlToObject(segment, MeasuresSegment.class);
                        if (measuresSegment.isSuccess()) {
                            metricMergeSegment.setMeasures(measuresSegment.get().getMeasures());
                        } else {
                            log.error("度量信息解析异常，segment={}", segment);
                        }
                        break;
                    //验证维度信息
                    case YamlSegmentKeys.DIMENSIONS:
                        Try<DimensionsSegment> dimensionsSegments = YamlUtil.yamlToObject(segment, DimensionsSegment.class);
                        if (dimensionsSegments.isSuccess()) {
                            metricMergeSegment.setDimensions(dimensionsSegments.get().getDimensions());
                        } else {
                            log.error("维度信息解析异常，segment={}", segment);
                        }
                        break;
                    //验证指标信息
                    case YamlSegmentKeys.METRIC:
                        Try<MetricSegment> metricSegments = YamlUtil.yamlToObject(segment, MetricSegment.class);
                        if (metricSegments.isSuccess()) {
                            metricMergeSegment.setMetric(metricSegments.get().getMetric());
                        } else {
                            log.error("指标信息解析异常，segment={}", segment);
                        }
                        break;
                    default:
                }
            }
        }
        return metricMergeSegment;
    }

    @Override
    public DatasetSegment transformToDataSet(String yamlContent) {
        MetricMergeSegment metricMergeSegment = this.convertYamlToSegment(yamlContent);
        String name = metricMergeSegment.getData_source().getData_source().getName();
        DatasetDBVO datasetByName = datasetDataManager.findDatasetByName(name);
        DatasetVO datasetVO = JsonUtils.transfrom(datasetByName, DatasetVO.class);
        DatasetSegment datasetSegment = yamlService.getDatasetSegment(datasetVO);
        if (metricMergeSegment.getDimensions().stream().noneMatch(m -> Objects.equals(m.getType(), "time"))) {
            DimensionsSegment.TypeParams day = DimensionsSegment.TypeParams.builder().is_primary(true).time_granularity("day").build();
            DimensionsSegment.UserDimension defaultTimeDim = DimensionsSegment.UserDimension.builder()
                    .expr(String.format("cast('%s' as datetime)", LocalDate.now()))
                    .name(YamlSegmentKeys.DEFAULT_TIME_DIM_DS)
                    .type("time")
                    .type_params(day)
                    .build();
            metricMergeSegment.getDimensions().add(defaultTimeDim);
        }
        //数据源
        datasetSegment.setMeasures(metricMergeSegment.getMeasures().stream()
                .map(f -> JsonUtils.transfrom(f, MeasuresSegment.Measures.class))
                .collect(Collectors.toList()));
        datasetSegment.setDimensions(metricMergeSegment.getDimensions().stream()
                .map(f -> JsonUtils.transfrom(f, DimensionsSegment.Dimension.class))
                .collect(Collectors.toList()));

        return datasetSegment;
    }

    @Override
    public DataSourceSegment transformToDataSoucre(String yamlContent) {
        DatasetSegment datasetSegment = this.transformToDataSet(yamlContent);
        return DataSourceSegment.builder()
                .data_source(datasetSegment)
                .build();
    }

    @Override
    public MetricSegmentVO transformToMetricVo(String yamlContent) {
        MetricMergeSegment metricMergeSegment = this.convertYamlToSegment(yamlContent);
        MetricSegment.Metric metric = JsonUtils.transfrom(metricMergeSegment.getMetric(), MetricSegment.Metric.class);
        return MetricSegmentVO.builder()
                .metric(metric)
                .build();
    }

    @Override
    public String transformToExecuteYaml(String yamlContent) {
        DataSourceSegment dataSourceSegment = transformToDataSoucre(yamlContent);
        MetricSegmentVO metricSegmentVO = transformToMetricVo(yamlContent);
        return YamlUtil.toYamlWithQuotation(dataSourceSegment) + YamlUtil.toYamlWithQuotation(metricSegmentVO);
    }
}
