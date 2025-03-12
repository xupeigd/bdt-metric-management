package com.quicksand.bigdata.metric.management.utils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * JPAModelMap
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/10 18:24
 * @description JPA查询结果映射工具类
 */
public final class JPAModelMapUtils {
    private JPAModelMapUtils() {

    }

    /**
     * 说明: 将JPA查询的List<Object[]>结果集封装到自定义对象中
     *
     * @param list     待处理数据
     * @param clazz    待映射对象类型，该对象的构造方法需要结合@Sign注解使用
     * @param mapIndex 待映射对象构造器上的注解值，用于匹配不同的结果集映射
     * @param <T>      待映射对象泛型
     * @return 映射后的结果集
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> mapping(List<Object[]> list, Class<T> clazz, int mapIndex) {
        if (list != null) {
            Constructor<?>[] constructors = clazz.getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(MapIndex.class)) {
                    if (mapIndex == constructor.getAnnotation(MapIndex.class).value()) {
                        List<Object> result = new ArrayList<>(list.size());
                        try {
                            for (Object[] data : list) {
                                result.add(constructor.newInstance(data));
                            }
                            return (List<T>) result;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            throw new RuntimeException("\"" + clazz.getName() + "\" constructor need use annotation \"" + MapIndex.class.getName() + "\"");
        }
        return null;
    }

    /**
     * 说明: 将JPA查询的List<Object[]>结果集封装到自定义对象中
     * 为默认的注解值提供一个便捷的方法，不用传递注解参数0
     */
    public static <T> List<T> mapping(List<Object[]> list, Class<T> clazz) {
        return mapping(list, clazz, 0);
    }
}
