package com.quicksand.bigdata.metric.management.ors.jwt;

import com.quicksand.bigdata.metric.management.ors.models.ResignUserModel;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

/**
 * JwtTokenTest
 *
 * @author page
 * @date 2022/8/26
 */
public class JwtTest {

    private static <T> Jwt<T> verfifyToken(String token, Class<T> tClass, @SuppressWarnings("rawtypes") Jwt.SignKeyExchanger signKeyExchanger)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String[] tokens = token.split("\\.");
        Jwt<T> jwt = new Jwt<>();
        jwt.setHeader(Jwt.Jsu.parseTo(new String(Base64.decodeBase64(tokens[0])), Jwt.Header.class, null));
        jwt.setPayload(Jwt.Jsu.parseTo(new String(Base64.decodeBase64(tokens[1])), tClass, null));
        jwt.setSignKeyExchanger(signKeyExchanger);
        if (Objects.equals(tokens[2], jwt.signature())) {
            return jwt;
        }
        throw new NoSuchAlgorithmException();
    }

    public String[] getDohkoKeys() {
        return new String[]{
                "SFVBTEFMQS1ISU8=",
                "JCogJiAmIBYkJCc=",
                "EhUQExATEAsSEhM=",
                "CQoICQgJCAUJCQk=",
                "BAUEBAQEBAIEBAQ=",
                "AgICAgICAgECAgI=",
                "AQEBAQEBAQABAQE=",
                "AAAAAAAAAAAAAAA="
        };
    }

    public String[] getPre() {
        return new String[]{
        };
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSign() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        //key组由验证方服务器提供
        String[] avatarKeys = getPre();
        AvatarSignKeyExchanger avatarSignKeyExchanger = new AvatarSignKeyExchanger(avatarKeys);
        //构建jwt对象
        Jwt.Header header = Jwt.Header.builder()
                .type("JWT")
                .alg(Jwt.SIGNATURE_ALG_MD5_HEADER)
                //签发组织
                .orgination("quicksand")
                //签发平台
                .platform("IOA")
                .iat(System.currentTimeMillis())
                .build();
        ResignUserModel payload = ResignUserModel.builder()
                .id(1)
                .accountId(93088L)
                //登陆名称
                .name("page.hsu.quicksand.com")
                //真实名称
                .userName("Page Hsu")
                .email("page.Hsu.quicksand.com")
                .mobile("17326850012")
                .pk("mobile")
                .expired(3 * 60 * 1000L)
                .mores((List<Jwt.SignKeyPair.DefaultSignKeyPair>)
                        CollectionUtils.arrayToList(
                                new Jwt.SignKeyPair.DefaultSignKeyPair[]{}))
                .build();
        String jwtStr = Jwt.buildToken(header, payload, avatarSignKeyExchanger);
        // Jwt<ResignUserModel> jwtPayload = verfifyToken(jwtStr, ResignUserModel.class, avatarSignKeyExchanger);
        // Assert.assertEquals("page", jwtPayload.getPayload().getName());
        // Assert.assertEquals(jwtStr, jwtPayload.toTokenString());
        System.out.println("--- dohko: ");
        System.out.println("http://metric-web.bigdata.dohko.quicksand.com/api/identify/jwtLogin?token=" + jwtStr);
        System.out.println("--- locahost: ");
        System.out.println("http://127.0.0.1:9909/identify/jwtLogin?token=" + jwtStr);
        System.out.println("--- pre: ");
        System.out.println("https://metric-web-bigdata.pre.quicksand.com/api/identify/jwtLogin?token=" + jwtStr);
        System.out.println("--- prod(办公网): ");
        System.out.println("http://gauss-metric-web.bigdata.quicksand.com/api/identify/jwtLogin?token=" + jwtStr);
        System.out.println("--- prod(公网): ");
        System.out.println("http://gauss-metric-web.bigdata.tfz.quicksand.com/api/identify/jwtLogin?token=" + jwtStr);
        // //分段输出
        // System.out.println("Header：" + Base64.encodeBase64URLSafeString(Jwt.Jsu.toJsonString(jwtPayload.header).getBytes()));
        // System.out.println("Payload：" + Base64.encodeBase64URLSafeString(Jwt.Jsu.toJsonString(jwtPayload.payload).getBytes()));
    }

}