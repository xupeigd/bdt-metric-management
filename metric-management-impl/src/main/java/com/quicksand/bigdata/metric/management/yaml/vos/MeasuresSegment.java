package com.quicksand.bigdata.metric.management.yaml.vos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * MeasuresSegmentVO
 *
 * @author zhaoxin3
 * @date 2022/8/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasuresSegment {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<UserMeasures> measures;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Measures {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String name;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String description;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String expr;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String agg;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Boolean create_metric;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserMeasures extends Measures {
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String data_type;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String processing_logic;
    }

}
