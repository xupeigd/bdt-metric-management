package com.quicksand.bigdata.metric.management.yaml.vos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
public class MetricSegment {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    UserMetric metric;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metric {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String name;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        List<String> owners;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String type;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        TypeParams type_params;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String constraint;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeParams {
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        List<String> measures;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String measure;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String numerator;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String denominator;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String grain_to_date;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String window;
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String expr;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserMetric extends Metric {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String cn_name;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String alias;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String cn_alias;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String metric_code;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String theme;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String business;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<String> tech_owners;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<String> business_owners;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String metric_level;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String data_security_level;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String data_type;

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        String description;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String processing_logic;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<String> statisticPeriods;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String cluster_type;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String metric_aggregation_type;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String metric_accumulative;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String metric_authentication;

    }
}
