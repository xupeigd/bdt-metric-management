package com.quicksand.bigdata.metric.management.metric.vos;

import com.quicksand.bigdata.metric.management.utils.MapIndex;
import lombok.Data;

import java.math.BigInteger;

/**
 * CommonDownListVO
 * 用于各种下拉列表
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/15 14:21
 * @description
 */
@Data
public class CommonDownListVO {
    Long id;
    String name;
    String desc;

    @MapIndex
    public CommonDownListVO(BigInteger id, String name) {
        this.id = id.longValue();
        this.name = name;
    }

    @MapIndex(1)
    public CommonDownListVO(BigInteger id, String name, String desc) {
        this.id = id.longValue();
        this.name = name;
        this.desc = desc;
    }
}
