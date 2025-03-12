package com.quicksand.bigdata.metric.management.utils;

import org.springframework.beans.BeanUtils;

/**
 * DvAdapter
 *
 * @author page
 * @date 2022/8/1
 */
public class DvAdapter {

    public static <T, Ts> Ts as(T source, Ts target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

}
