package com.quicksand.bigdata.metric.management.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * YamlUtil
 *
 * @author page
 * @date 2022/8/3
 */
@Slf4j
public class YamlUtil {

    private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_THREAD_LOCAL = new ThreadLocal<>();

    static ObjectMapper getOrCreateGson() {
        ObjectMapper objMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
        if (null == objMapper) {
            objMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.SPLIT_LINES));
            OBJECT_MAPPER_THREAD_LOCAL.set(objMapper);
        }
        return objMapper;
    }

    private static void clean() {
        OBJECT_MAPPER_THREAD_LOCAL.remove();
    }

    public static <T> String toYaml(T t) {
        return Try.of(() -> getOrCreateGson()
                        .writeValueAsString(t)
                        .replaceAll("\"", ""))
                .onFailure(ex -> {
                    clean();
                    log.error("toYaml error! ", ex);
                })
                .onSuccess(s -> clean())
                .get();
    }

    public static <T> String toYamlWithQuotation(T t) {
        return Try.of(() -> getOrCreateGson()
                        .writeValueAsString(t))
                .onFailure(ex -> {
                    clean();
                    log.error("toYaml error! ", ex);
                })
                .onSuccess(s -> clean())
                .get();
    }

    public static <T> Try<T> yamlToObject(String yamlStr, Class<T> tClass) {
        return Try.of(() -> getOrCreateGson().readValue(yamlStr, tClass))
                .onFailure(ex -> {
                    clean();
                    log.error("yamlToObject error! ", ex);
                })
                .onSuccess(s -> clean());
    }

}
