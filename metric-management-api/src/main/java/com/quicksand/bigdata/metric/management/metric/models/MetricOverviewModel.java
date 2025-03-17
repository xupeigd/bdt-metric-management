package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetOverviewModel;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * MetricOverviewModel
 *
 * @author page
 * @date 2022-07-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "指标view详情")
public class MetricOverviewModel {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    /**
     * 英文名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String enName;

    /**
     * 中文名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String cnName;

    /**
     * 数据集数据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    DatasetOverviewModel dataset;

    /**
     * 主题域实体
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MetricCatalogModel topicDomain;

    /**
     * 业务域实体
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MetricCatalogModel businessDomain;

    /**
     * 指标等级
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MetricLevel metricLevel;

    /**
     * 数据安全等级
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    DataSecurityLevel dataSecurityLevel;
    /**
     * 技术负责人
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<UserOverviewModel> techOwners;

    @Schema(description = "")
    String serialNumber;

    /**
     * 业务负责人
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<UserOverviewModel> businessOwners;

    /**
     * 上线/下线
     * <p>
     * 1 上线 0 下线
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    PubsubStatus pubsub;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    ClusterType clusterType;
    /**
     * 是否拥有调用权限
     * 0否 1是
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    int invokeEnable;
    /**
     * 信息更新时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

    /**
     * 指标聚合类型
     * 默认
     */
    @Schema(description = "指标聚合类型，0:原子指标，1:衍生指标，2:复合指标")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    MetricAggregation metricAggregationType;

    /**
     * 指标是否可累加
     * 默认
     */
    @Schema(description = "指标是否可累加，0:否，1：是")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    WhetherStatus metricAccumulative;

    /**
     * 指标是否认证
     * 默认
     */
    @Schema(description = "指标是否认证，0:否，1：是")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    WhetherStatus metricAuthentication;

    @JsonProperty
    public int pubsub() {
        return null == pubsub ? -1 : pubsub.getCode();
    }

    @JsonProperty
    public int clusterType() {
        return null == clusterType ? 0 : clusterType.getCode();
    }

    @JsonProperty
    public String name() {
        return null == enName ? "" : enName;
    }

    @JsonProperty
    public int metricAggregationType() {
        return null == metricAggregationType ? 0 : metricAggregationType.getCode();
    }

    @JsonProperty
    public int metricAccumulative() {
        return null == metricAccumulative ? 0 : metricAccumulative.getCode();
    }

    @JsonProperty
    public int metricAuthentication() {
        return null == metricAuthentication ? 0 : metricAuthentication.getCode();
    }

}
