package com.quicksand.bigdata.metric.management.ors.jwt;

import lombok.AllArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * AvatarSignKeyExchanger
 * (可变密钥兑换器)
 *
 * @author page
 * @date 2022/8/26
 */
@AllArgsConstructor
public class AvatarSignKeyExchanger
        implements Jwt.SignKeyExchanger<String, String> {

    @NotEmpty
    String[] avatars;

    int getSum(long iat) {
        String iatStr = String.valueOf(iat);
        char[] iatSum = new char[iatStr.length()];
        iatStr.getChars(0, iatStr.length(), iatSum, 0);
        int sum = 0;
        for (char c : iatSum) {
            sum += c;
        }
        return Math.abs(sum);
    }

    int getAvatarIndex(long iat) {
        return Math.abs(getSum(iat) % avatars.length);
    }

    @Override
    public Jwt.SignKeyPair<String, String> exchange(String uniqFlag, String alg, String signKey, long iat) {
        return new Jwt.SignKeyPair.DefaultSignKeyPair(signKey, avatars[getAvatarIndex(iat)]);
    }

}