package com.quicksand.bigdata.metric.management.metric.vos;

import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * MetricVO
 *
 * @author page
 * @date 2022/8/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricVO {

    Integer id;

    String serialNumber;


    String enName;


    String cnName;

    DatasetVO dataset;


    MetricCatalogVO topicDomain;


    MetricCatalogVO businessDomain;


    MetricLevel metricLevel;


    DataSecurityLevel dataSecurityLevel;


    String enAlias;


    String cnAlias;


    String dateType;


    List<StatisticPeriod> statisticPeriods;


    String processLogic;


    List<UserVO> businessOwners;


    List<UserVO> techOwners;


    PubsubStatus pubsub;


    Integer createUserId;


    Date createTime;


    Integer updateUserId;

    Date updateTime;

    DataStatus status;

    String yamlSegment;

    List<MetricMeasureVO> measures;

    List<MetricDimensionVO> dimensions;

    /**
     * 指标的默认配额
     * <p>
     * 默认 1000L
     */
    Long defaultQuota;

    /**
     * 默认配额的刷新周期
     * <p>
     * 1月 2周 3日 4小时 默认 1月
     */
    Integer defaultQuotaPeriod;

    /**
     * 默认配额刷新cron
     * <p>
     * 默认
     */
    String defaultCronExpress;

}
