package com.quicksand.bigdata.metric.management.identify.securities.assemblies.impls;

import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * IdentifyPasswordEncoderImpl
 *
 * @author page
 * @date 2020/8/24 11:02
 */
@Component
public class IdentifyPasswordEncoderImpl
        implements IdentifyPasswordEncoder {

    private final static String MD5_SALT = "BDT-METRICS";

    public static void main(String[] args) {
        System.out.println(new IdentifyPasswordEncoderImpl().encode("dingyi"));
    }

    @Override
    public String encode(CharSequence charSequence) {
        return DigestUtils.md5DigestAsHex((charSequence.toString() + "." + MD5_SALT).getBytes());
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(encode(charSequence));
    }

}
