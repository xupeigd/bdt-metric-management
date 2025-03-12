package com.quicksand.bigdata.metric.management.ors.apis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * AppRequestVO
 *
 * @author page
 * @date 2022/11/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppRequestModel {

    /**
     * AppId
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    /**
     * AppName
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 调用参数
     * （不得使用特殊class）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ValuePair> params = new ArrayList<>();

    /**
     * 签名mills
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long signMills = 0L;

    /**
     * 签名段(key)
     * （Md5）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String signKey;

    /**
     * 签名段(value)
     * (Md5)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String signValue;

    public static AppRequestModel from(String headerValue) throws IOException {
        String jsonStr = new String(Base64.getDecoder().decode(headerValue.getBytes(StandardCharsets.UTF_8)));
        return JSUtil.parseTo(jsonStr, AppRequestModel.class, null);
    }

    /**
     * 擦除sign，并签署
     *
     * @param token app的token
     */
    public String signKey(String token) {
        signKey = null;
        String tmpSignValue = signValue;
        signValue = null;
        List<ValuePair> tmpParams = params;
        params = null;
        signKey = DigestUtils.md5DigestAsHex(String.format("%s.%s", JSUtil.toJsonString(this), token).getBytes(StandardCharsets.UTF_8));
        params = tmpParams;
        signValue = tmpSignValue;
        return signKey;
    }

    public String signValue(String token) {
        if (null == signKey || "".equals(signKey.trim())) {
            signKey(token);
        }
        signValue = null;
        signValue = DigestUtils.md5DigestAsHex(String.format("%s.%s", JSUtil.toJsonString(this), token).getBytes(StandardCharsets.UTF_8));
        return signValue;
    }

    public String toHeaderValue() {
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(ValuePair::cleanValue);
        }
        return Base64.getEncoder().encodeToString(JSUtil.toJsonString(this).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 校验签名是否相符
     *
     * @param token app的token
     * @return true/false
     */
    public boolean validationSignKey(String token) {
        String reqSign = signKey;
        signKey = null;
        boolean equals = Objects.equals(reqSign, signKey(token));
        if (!equals) {
            signKey = reqSign;
        }
        return equals;
    }

    public boolean validationSignValue(String token) {
        String regSign = signValue;
        signValue = null;
        boolean equals = Objects.equals(regSign, signValue(token));
        if (!equals) {
            signValue = regSign;
        }
        return equals;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValuePair {

        @JsonProperty("n")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String name;

        @JsonProperty("v")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Object value;

        void cleanValue() {
            value = null;
        }

    }

    protected static class JSUtil {

        private static final ThreadLocal<ObjectMapper> OBJECT_MAPPER_THREAD_LOCAL = new ThreadLocal<>();

        private static ObjectMapper getOrCreateObjectMapper() {
            ObjectMapper objectMapper = OBJECT_MAPPER_THREAD_LOCAL.get();
            if (null == objectMapper) {
                objectMapper = new ObjectMapper();
                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                objectMapper.setVisibility(objectMapper.getVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
                OBJECT_MAPPER_THREAD_LOCAL.set(objectMapper);
            }
            return objectMapper;
        }

        public static <T> String toJsonString(T object) {
            ObjectMapper objectMapper = getOrCreateObjectMapper();
            try {
                return objectMapper.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return "{}";
        }

        public static <T> T parseTo(String jsonStr, Class<T> tClass, Void nul) throws IOException {
            ObjectMapper objectMapper = getOrCreateObjectMapper();
            return objectMapper.readValue(jsonStr.getBytes(), tClass);
        }

        public void removeObjectMapper() {
            OBJECT_MAPPER_THREAD_LOCAL.remove();
        }

    }

}
