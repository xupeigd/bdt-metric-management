package com.quicksand.bigdata.metric.management.metric.vos;

import com.quicksand.bigdata.metric.management.utils.MapIndex;
import lombok.Data;

import java.math.BigInteger;

/**
 * MetricCatalogGroupVO
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/10 18:08
 * @description
 */
@Data
public class MetricCatalogGroupVO {

    Integer catalogId;
    Integer metricCount;


    @MapIndex
    public MetricCatalogGroupVO(BigInteger catalogId, BigInteger metricCount) {
        this.catalogId = catalogId.intValue();
        this.metricCount = metricCount.intValue();
    }

}
