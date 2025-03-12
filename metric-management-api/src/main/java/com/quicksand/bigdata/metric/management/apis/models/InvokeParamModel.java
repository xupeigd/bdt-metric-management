package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InvokeParamModel
 * (调用参数model)
 *
 * @author page
 * @date 2022/10/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvokeParamModel {

    /**
     * 名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 类型
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type;

    /**
     * 描述
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;

    /**
     * 是否必传
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    boolean isRequired;

    /**
     * 候选值
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String candidateValues;

}
