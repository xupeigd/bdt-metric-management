package com.quicksand.bigdata.metric.management.ors.jwt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * JwtToken
 *
 * @author page
 * @date 2022/7/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Jwt<T> {

    public static final String SIGNATURE_ALG_MD5_HEADER = "MD5";

    public static final String SIGNATURE_ALG_HS256_HEADER = "HS256";

    private static final String SIGNATURE_ALG_MD5 = "Md5";

    private static final String SIGNATURE_ALG_HS256 = "HmacSHA256";
    /**
     * 头部
     */
    Header header;
    /**
     * 载荷
     */
    T payload;
    /**
     * 验证签名
     */
    String verifySignature;
    @JsonIgnore
    transient SignKeyExchanger<?, ?> signKeyExchanger;

    public static <T> String buildToken(Header header, T payload, @SuppressWarnings("rawtypes") SignKeyExchanger signKeyExchanger)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Jwt<T> jwt = new Jwt<>();
        jwt.setHeader(header);
        jwt.setPayload(payload);
        jwt.setSignKeyExchanger(signKeyExchanger);
        return jwt.toTokenString();
    }


    public String toTokenString() throws NoSuchAlgorithmException, InvalidKeyException {
        return String.format("%s.%s.%s", Base64.encodeBase64URLSafeString(Jsu.toJsonString(header).getBytes()),
                Base64.encodeBase64URLSafeString((null == payload ? "{}" : Jsu.toJsonString(payload)).getBytes()), signature());
    }

    protected String signature() throws NoSuchAlgorithmException, InvalidKeyException {
        if (StringUtils.hasText(this.verifySignature)) {
            return verifySignature;
        }
        String algType = header.getAlg();
        //暂时只支持MD5，HS256
        if (SIGNATURE_ALG_MD5_HEADER.equalsIgnoreCase(algType)
                || SIGNATURE_ALG_HS256_HEADER.equalsIgnoreCase(algType)) {
            SignKeyPair<?, ?> realSignKeyPair = signKeyExchanger.exchange(header.getOrgination(), algType, header.getSignKey(), header.iat);
            return verifySignature = (SIGNATURE_ALG_MD5_HEADER.equalsIgnoreCase(algType)
                    ? signatureByMd5(realSignKeyPair)
                    : signatureByHs256(realSignKeyPair));
        }
        throw new NoSuchAlgorithmException();
    }

    private String signatureByHs256(SignKeyPair<?, ?> realSignKeyPair) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SIGNATURE_ALG_HS256);
        mac.init(new SecretKeySpec(realSignKeyPair.value().toString().getBytes(), SIGNATURE_ALG_HS256));
        return Base64.encodeBase64URLSafeString(
                mac.doFinal(
                        String.format("%s.%s", Base64.encodeBase64URLSafeString(Jsu.toJsonString(header).getBytes()),
                                Base64.encodeBase64URLSafeString((null == payload ? "{}" : Jsu.toJsonString(payload)).getBytes())
                        ).getBytes())
        );
    }

    private String signatureByMd5(SignKeyPair<?, ?> realSignKeyPair) {
        return Base64.encodeBase64URLSafeString(
                DigestUtils.md5DigestAsHex(
                        String.format("%s.%s.%s", Base64.encodeBase64URLSafeString(Jsu.toJsonString(header).getBytes()),
                                Base64.encodeBase64URLSafeString((null == payload ? "{}" : Jsu.toJsonString(payload)).getBytes()),
                                realSignKeyPair.value().toString()).getBytes()
                ).getBytes()
        );
    }

    /**
     * 签名算法对
     *
     * @param <K> key
     * @param <V> value
     */
    public interface SignKeyPair<K, V> {

        K key();

        V value();

        @Getter
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        class DefaultSignKeyPair
                implements SignKeyPair<String, String> {

            @JsonInclude(JsonInclude.Include.NON_NULL)
            String key;

            @JsonInclude(JsonInclude.Include.NON_NULL)
            String value;

            @Override
            public String key() {
                return key;
            }

            @Override
            public String value() {
                return value;
            }
        }
    }

    /**
     * 签名key兑换器
     */
    public interface SignKeyExchanger<K, V> {

        /**
         * 兑换
         *
         * @param uniqFlag 唯一标识(用于区分不同的group)
         * @param alg      算法
         * @param signKey  算法key/签名key
         * @param iat      签发时间戳
         * @return 真实的key
         */
        SignKeyPair<K, V> exchange(String uniqFlag, String alg, String signKey, long iat);

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {

        /**
         * 类型
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String type;

        /**
         * 签名算法
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String alg;

        /**
         * 签名Key的区分key
         * <p>
         * (默认为null/"",使用认证系统的默认值)
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String signKey;

        /**
         * 签发组织
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String orgination;

        /**
         * 签发平台
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String platform;

        /**
         * 签发时间iat(时间戳)
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long iat;

    }

    protected static class Jsu {

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