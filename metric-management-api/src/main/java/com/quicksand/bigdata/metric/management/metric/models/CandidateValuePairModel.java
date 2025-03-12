package com.quicksand.bigdata.metric.management.metric.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CandidateValuePairModel
 * (候选值model)
 *
 * @author page
 * @date 2022/10/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateValuePairModel {

    /**
     * 名称
     */
    String name;

    /**
     * 候选值（去重）
     */
    List<String> values;

}
