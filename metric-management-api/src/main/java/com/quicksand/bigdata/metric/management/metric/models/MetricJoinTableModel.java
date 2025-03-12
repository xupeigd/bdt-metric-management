package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicksand.bigdata.metric.management.consts.JoinType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MetricJoinTableModel
 *
 * @author page
 * @date 2022/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricJoinTableModel {

    /**
     *
     */
    String name;

    /**
     * 别名
     */
    String alisa;

    /**
     * 关联类型
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    JoinType joinType;

    /**
     * 关联条件
     */
    String joinConditions;


    @JsonProperty
    public String joinTYpe() {
        return null == joinType ? "" : joinType.getFlag();
    }

}
