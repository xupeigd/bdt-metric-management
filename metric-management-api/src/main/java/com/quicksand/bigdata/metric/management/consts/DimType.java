package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * DimType
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/22 16:36
 * @description
 */
@Getter
public enum DimType {
    time("time", "时间类型维度"),
    categorical("categorical", "分类类型维度"),

    ;

    final String label;

    @JsonValue
    final String yamlValue;

    DimType(String yamlValue, String label) {
        this.label = label;
        this.yamlValue = yamlValue;
    }

    public static DimType getByValue(String yamlValue) {
        for (DimType dimType : DimType.values()) {
            if (dimType.getYamlValue().equalsIgnoreCase(yamlValue)) {
                return dimType;
            }
        }
        return null;
    }
}
