package com.quicksand.bigdata.metric.management.utils;

import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.vavr.control.Try;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PageableUtil
 *
 * @author page
 * @date 2020/8/19 09:24
 */
public class PageableUtil {

    public static <R, T> PageImpl<R> page2page(org.springframework.data.domain.Page<T> page, Class<R> rClas, @Nullable Function<T, R> mapFunction) {
        if (null == page) {
            return PageImpl.buildEmptyPage(0, 0);
        }
        List<R> rs = CollectionUtils.isEmpty(page.getContent())
                ? new ArrayList<>()
                : page.getContent().stream()
                .map(v ->
                        Try.of(() -> null == mapFunction ?
                                JsonUtils.transfrom(v, rClas)
                                : mapFunction.apply(v)).getOrNull()
                ).filter(Objects::nonNull)
                .collect(Collectors.toList());
        return PageImpl.build(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements(), rs);
    }

}
