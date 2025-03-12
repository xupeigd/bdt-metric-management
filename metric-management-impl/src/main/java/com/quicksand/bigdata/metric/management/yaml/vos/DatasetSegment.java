package com.quicksand.bigdata.metric.management.yaml.vos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DatasetSegmentVO
 *
 * @author page
 * @date 2022/8/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetSegment {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String name;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String description;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    List<String> owners;

    @JsonProperty(value = "sql_table")
    String sqlTable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    MutabilitySeg mutability;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    List<Identifier> identifiers;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    List<MeasuresSegment.Measures> measures;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    List<DimensionsSegment.Dimension> dimensions;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identifier {

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String ref;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String name;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String type;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String expr;

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        List<Identifier> identifiers;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MutabilityParamSeg {

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonProperty("update_cron")
        String updateCron;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonProperty("along")
        String alongColumn;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MutabilitySeg {

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String type;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("type_params")
        MutabilityParamSeg typeParams;

    }


}
