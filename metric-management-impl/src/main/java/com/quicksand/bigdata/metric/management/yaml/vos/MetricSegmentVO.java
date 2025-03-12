package com.quicksand.bigdata.metric.management.yaml.vos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class MetricSegmentVO {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    MetricSegment.Metric metric;
}
