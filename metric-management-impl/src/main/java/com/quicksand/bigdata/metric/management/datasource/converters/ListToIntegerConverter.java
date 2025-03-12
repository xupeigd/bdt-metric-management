package com.quicksand.bigdata.metric.management.datasource.converters;

import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ListToIntegerConverter
 *
 * @author page
 * @date 2022/8/5
 */
@Converter
public class ListToIntegerConverter
        implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        return StringUtils.collectionToCommaDelimitedString(attribute);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData)) {
            return new ArrayList<>();
        } else {
            return Arrays.stream(dbData.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
    }

}
