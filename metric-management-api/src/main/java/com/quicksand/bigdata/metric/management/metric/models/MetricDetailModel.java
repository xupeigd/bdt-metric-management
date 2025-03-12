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

import java.util.List;

/**
 * MetricDetailModel
 *
 * @author page
 * @date 2022/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "指标详情")
public class MetricDetailModel {

    @Schema(description = "")
    Integer id;

    /**
     * 指标编号
     */
    @Schema(description = "")
    String serialNumber;

    /**
     * 指标名称
     */
    @Schema(description = "")
    String enName;

    /**
     * 指标中文名称
     */
    @Schema(description = "")
    String cnName;

    /**
     * 主题域实体
     */
    @Schema(description = "")
    MetricCatalogModel topicDomain;

    /**
     * 业务域实体
     */
    @Schema(description = "")
    MetricCatalogModel businessDomain;

    /**
     * 指标级别
     * <p>
     * T0，T1
     */
    @Schema(description = "")
    MetricLevel metricLevel;

    /**
     * 数据安全级别
     * <p>
     * 0/1/2
     */
    //@JsonIgnore
    @Schema(description = "")
    DataSecurityLevel dataSecurityLevel;

    /**
     * 指标别名
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    String enAlias;

    /**
     * 指标中文别名
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    String cnAlias;

    /**
     * 描述
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    String description;

    /**
     * 数据类型
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    String dataType;

    /**
     * 统计周期
     */
    //@JsonIgnore
    @Schema(description = "")
    List<StatisticPeriod> statisticPeriods;

    /**
     * 加工逻辑
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    String processLogic;

    /**
     * 数据集数据/所在模型
     */
    @Schema(description = "")
    DatasetOverviewModel dataset;

    /**
     * 业务负责人
     */
    @Schema(description = "")
    List<UserOverviewModel> businessOwners;

    /**
     * 技术负责人
     */
    @Schema(description = "")
    List<UserOverviewModel> techOwners;

//    /**
//     * 纬度列数据
//     */
//    List<DatasetColumnModel> dimensions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(description = "")
    DataStatus status;
    /**
     * 上线/下线
     * <p>
     * 1 上线 0 下线
     */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(description = "")
    PubsubStatus pubsub;

    List<MetricColumnsModel> dimColumns;

    List<MetricColumnsModel> measureColumns;

    //    @JsonProperty
//    public List<Integer> statisticPeriods() {
//        if (!CollectionUtils.isEmpty(statisticPeriods)) {
//            return statisticPeriods.stream().map(StatisticPeriod::getCode).collect(Collectors.toList());
//        }
//        return Collections.emptyList();
//    }
    String yamlSegment;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Schema(description = "")
    ClusterType clusterType;

    /**
     * 指标的默认配额
     * <p>
     * 默认 1000L
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long defaultQuota;

    /**
     * 默认配额的刷新周期
     * <p>
     * 1月 2周 3日 4小时 默认 1月
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer defaultQuotaPeriod;

    /**
     * 默认配额刷新cron
     * <p>
     * 默认
     */
    @Schema(description = "默认配额刷新cron")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String defaultCronExpress;


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
    public Integer metricLevel() {
        return null == metricLevel ? -1 : metricLevel.getCode();
    }

    @JsonProperty
    public int dataSecurityLevel() {
        return null == dataSecurityLevel ? -1 : dataSecurityLevel.getCode();
    }

    @JsonProperty
    public int pubsub() {
        return null == pubsub ? -1 : pubsub.getCode();
    }

    @JsonProperty
    public int clusterType() {
        return null == clusterType ? 0 : clusterType.getCode();
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
