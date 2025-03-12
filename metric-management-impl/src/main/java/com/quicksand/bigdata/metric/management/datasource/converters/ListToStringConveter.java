package com.quicksand.bigdata.metric.management.datasource.converters;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

/**
 * ListToStringConveter
 *
 * @author page
 * @date 2022/8/4
 */
@Converter
public class ListToStringConveter
        implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return CollectionUtils.isEmpty(attribute) ? "" : StringUtils.collectionToCommaDelimitedString(attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return StringUtils.hasText(dbData) ? Lists.newArrayList(StringUtils.commaDelimitedListToStringArray(dbData)) : Lists.newArrayList();
    }

}
