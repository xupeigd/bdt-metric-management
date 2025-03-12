package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MetricInvokeInfoModel
 *
 * @author page
 * @date 2022/10/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricInvokeInfosModel {

    /**
     * 请起头参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<InvokeParamModel> headersParameters;

    /**
     * 可用参数接口
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MetricInvokeInfoModel candidateValueInterface;

    /**
     * 结果查询接口
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MetricInvokeInfoModel resultQueryInterface;

    /**
     * 接口信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MetricInvokeInfoModel> interfacesInfo;


}
