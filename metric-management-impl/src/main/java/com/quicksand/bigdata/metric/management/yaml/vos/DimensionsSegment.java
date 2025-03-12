package com.quicksand.bigdata.metric.management.yaml.vos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * DimensionsSegmentVO
 *
 * @author zhaoxin3
 * @date 2022/8/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionsSegment {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<UserDimension> dimensions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class Dimension {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String name;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String type;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        TypeParams type_params;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String expr;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Boolean is_partition;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeParams {
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        Boolean is_primary;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String time_granularity;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String time_format;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDimension extends Dimension {
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String data_type;

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String description;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String test;
    }
}
