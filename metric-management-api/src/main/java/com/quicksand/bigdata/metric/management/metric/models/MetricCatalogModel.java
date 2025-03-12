package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicksand.bigdata.metric.management.consts.DomainType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MetricTopicModel
 *
 * @author page
 * @date 2022/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricCatalogModel {

    /**
     * id
     */
    Integer id;

    /**
     * 名称
     */
    String name;

    /**
     * 层级编码
     * <p>
     * （3位一级，目前开放9位）
     */
    String code;

    /**
     * 目录下指标个数
     */
    Integer metricCount;

    /**
     * 父节点Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MetricCatalogModel parent;

    /**
     * 下级
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MetricCatalogModel> children;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    DomainType type;
    /**
     * 目录业务编码
     */
    String businessCode;

    @JsonProperty
    public int type() {
        return null == type ? -1 : type.getCode();
    }

}
