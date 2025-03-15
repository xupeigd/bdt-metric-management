package com.quicksand.bigdata.metric.management.utils;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetColumnDBVO;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dbvos.IdentifierDBVO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class DatasetUtil
 *
 * @Author: page
 * @Date: 2025/3/15
 * @Description:
 */
public class DatasetUtil {

    public static List<String> resolvingFields(DatasetDBVO dataset) {
        List<String> fields = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dataset.getIdentifiers())) {
            for (IdentifierDBVO identifier : dataset.getIdentifiers()) {
                if (!fields.contains(identifier.getExpr())) {
                    fields.add(identifier.getExpr());
                }
            }
        }
        if (!CollectionUtils.isEmpty(dataset.getColumns())) {
            for (DatasetColumnDBVO column : dataset.getColumns()) {
                if (Objects.equals(DataStatus.ENABLE, column.getIncluded())
                        && !fields.contains(column.getName())) {
                    fields.add(column.getName());
                }
            }
        }
        return fields;
    }

}
