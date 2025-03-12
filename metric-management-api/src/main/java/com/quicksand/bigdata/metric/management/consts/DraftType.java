package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DraftType
 *
 * @author page
 * @date 2022-08-02
 */
@Getter
@AllArgsConstructor
public enum DraftType {

    /**
     * 数据集
     */
    Dataset(0),

    /**
     * 指标
     */
    Metric(1),

    ;

    @JsonValue
    final int code;

    public static DraftType find(int type) {
        switch (type) {
            case 0:
                return Dataset;
            case 1:
                return Metric;
            default:
        }
        return Dataset;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DraftType cover(int code) {
        for (DraftType value : DraftType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
