package com.quicksand.bigdata.metric.management.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MapIndex 自定义标记注解，贴在VO的构造器上，用于JPA查询结果的封装,用于标记字段顺序
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/10 18:27
 * @description
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapIndex {
    int value() default 0;
}
