package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ExplainGroupModel
 *
 * @author page
 * @date 2022/10/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainGroupModel {

    /**
     * explain的指标Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Integer> metricsIds;

    /**
     * explain的指标名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> metricsNames;

    /**
     * 纬度
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> dimensions;

    /**
     * 条件
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ConditionModel condition;

    /**
     * 排序
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<SortModel> sorts;

    /**
     * 编译完成的sql
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String sql;

    /**
     * 数据集id
     */
    Integer datasetId;

    /**
     * 集群Id
     */
    Integer clusterId;

}
