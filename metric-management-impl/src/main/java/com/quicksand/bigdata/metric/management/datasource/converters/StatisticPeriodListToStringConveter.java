package com.quicksand.bigdata.metric.management.datasource.converters;

import com.google.common.collect.Lists;
import com.quicksand.bigdata.metric.management.consts.StatisticPeriod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * StatisticPeriodListToStringConveter
 *
 * @author page
 * @date 2022/8/4
 */
@Converter
public class StatisticPeriodListToStringConveter
        implements AttributeConverter<List<StatisticPeriod>, String> {

    @Override
    public String convertToDatabaseColumn(List<StatisticPeriod> attribute) {
        return CollectionUtils.isEmpty(attribute)
                ? ""
                : StringUtils.collectionToCommaDelimitedString(attribute.stream().map(StatisticPeriod::getCn).collect(Collectors.toList()));
    }

    @Override
    public List<StatisticPeriod> convertToEntityAttribute(String dbData) {
        return StringUtils.hasText(dbData)
                ? Arrays.stream(StringUtils.commaDelimitedListToStringArray(dbData))
                .map(StatisticPeriod::findByCn)
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                : Lists.newArrayList();
    }

}
