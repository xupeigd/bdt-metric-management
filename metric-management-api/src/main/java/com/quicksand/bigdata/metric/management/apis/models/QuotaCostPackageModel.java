package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * QuotaCostPackageModel
 *
 * @author page
 * @date 2022/12/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaCostPackageModel {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String flag;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer appId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer metricId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<QuotaCostModel> quotas;

}
