package com.quicksand.bigdata.metric.management.metric.services;

import com.quicksand.bigdata.metric.management.consts.AggregationType;
import com.quicksand.bigdata.metric.management.consts.DimType;
import com.quicksand.bigdata.metric.management.utils.MetricYamlContentUtil;
import com.quicksand.bigdata.metric.management.utils.YamlUtil;
import com.quicksand.bigdata.metric.management.yaml.vos.DataSourceSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.DimensionsSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.MeasuresSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricSegment;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * MetricUtil
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/2 17:40
 * @description
 */
public class MetricUtil {
    public static DimType getDimColumnsType(String columnType) {
        Set<String> timeColumnTypeNameSet = new HashSet<>();
        timeColumnTypeNameSet.add("date");
        timeColumnTypeNameSet.add("datetime");
        if (timeColumnTypeNameSet.contains(columnType.toLowerCase(Locale.ROOT))) {
            return DimType.time;
        }
        return DimType.categorical;
    }

    /**
     * @param aggregationType 聚合类型
     * @param columnType      字段类型
     * @author zhao_xin
     * @description 检查聚合方式是否和字段类型冲突
     **/
    public static void checkAggTypeMatchColumnType(AggregationType aggregationType, String columnType) {
        if (Objects.equals(aggregationType, AggregationType.SUM_BOOLEAN) || Objects.equals(aggregationType, AggregationType.BOOLEAN)) {
            Assert.isTrue(!columnType.toUpperCase().contains("DECIMAL"), String.format("DECIMAL类型字段无法使用%s聚合类型", aggregationType.getYamlValue()));
        }
    }

    /**
     * @param dataSourceSegment 数据源
     * @param dimensionsSegment 维度
     * @param measuresSegment   度量
     * @param metricSegment     指标
     * @return java.lang.String
     * @author zhao_xin
     * @description 4块合1
     **/
    public static String getCombineSegment(DataSourceSegment dataSourceSegment, DimensionsSegment dimensionsSegment, MeasuresSegment measuresSegment, MetricSegment metricSegment) {
        String yamlSegment = YamlUtil.toYaml(dataSourceSegment) +
                YamlUtil.toYaml(dimensionsSegment) +
                YamlUtil.toYaml(measuresSegment) +
                YamlUtil.toYaml(metricSegment);
        yamlSegment = MetricYamlContentUtil.decodeYamlContent(yamlSegment);
        return yamlSegment;
    }
}
