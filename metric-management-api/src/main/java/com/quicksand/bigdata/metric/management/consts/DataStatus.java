package com.quicksand.bigdata.metric.management.consts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * DataStstus
 * (数据状态)
 *
 * @author page
 * @date 2020/8/18 15:43
 */
@Getter
public enum DataStatus {

    /**
     * 删除
     */
    DISABLE(0),

    /**
     * 正常
     */
    ENABLE(1),

    ;

    @JsonValue
    final int code;

    DataStatus(int code) {
        this.code = code;
    }

    public static DataStatus findByCode(int code) {
        for (DataStatus value : DataStatus.values()) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return DataStatus.ENABLE;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DataStatus cover(int code) {
        for (DataStatus value : DataStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

}
