package com.quicksand.bigdata.metric.management.yaml.vos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.models.MetricInsertModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MetricSegmentVO
 *
 * @author zhaoxin3
 * @date 2022/8/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricMergeSegment {

    DataSourceSegment data_source;

    List<DimensionsSegment.UserDimension> dimensions;

    List<MeasuresSegment.UserMeasures> measures;

    MetricSegment.UserMetric metric;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnore
    Boolean verifySuccess;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnore
    String sqlContent;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnore
    MetricDBVO tmpMetric;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnore
    MetricInsertModel metricInsertModel;
}
