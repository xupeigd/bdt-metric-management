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
public class MetricInvokeInfoModel {

    /**
     * 接口名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 接口调用地址
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String interfaceUrl;

    /**
     * 请求方法
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String method;

    /**
     * 示例json
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String exampleJson;

    /**
     * 头部参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<InvokeParamModel> headersParameters;

    /**
     * 请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<InvokeParamModel> requestParameters;

}
