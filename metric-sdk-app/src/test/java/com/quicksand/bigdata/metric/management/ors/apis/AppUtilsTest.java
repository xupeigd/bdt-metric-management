package com.quicksand.bigdata.metric.management.ors.apis;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author page
 * @date 2022/11/7
 */
public class AppUtilsTest {

    @Test
    public void sign() throws IOException, ParseException {
        String signToken = "HelloToken";
        Date curDate = new Date();
        AppRequestModel reqModel = AppRequestModel.builder()
                .id(1)
                .name("LBT_v1.1")
                .signMills(System.currentTimeMillis())
                .params(Arrays.asList(
                        AppRequestModel.ValuePair.builder()
                                .name("K0")
                                .value("V0")
                                .build(),
                        AppRequestModel.ValuePair.builder()
                                .name("D0")
                                .value(curDate)
                                .build()))
                .build();
        reqModel.signValue(signToken);
        String headerValue = reqModel.toHeaderValue();
        System.out.println("signKey: " + reqModel.signKey);
        System.out.println("signValue: " + reqModel.signValue);
        System.out.println("Header Value: " + headerValue);
        AppRequestModel requestModel = AppRequestModel.from(headerValue);
        System.out.println(requestModel);
        System.out.println("validation:" + requestModel.validationSignKey(signToken));
        Assert.assertTrue(requestModel.validationSignKey(signToken));
        //填充value
        Map<String, AppRequestModel.ValuePair> name2Vp = requestModel.getParams().stream().collect(Collectors.toMap(AppRequestModel.ValuePair::getName, v -> v));
        name2Vp.get("K0").setValue("V0");
        name2Vp.get("D0").setValue(curDate);
        System.out.println("validation(fill values):" + requestModel.validationSignValue(signToken));
        Assert.assertTrue(requestModel.validationSignValue(signToken));
    }

    @Test
    public void testSign() {
        AppRequestModel requestModel = AppRequestModel.builder()
                .id(9)
                .name("Ds")
                .params(new ArrayList<>())
                .signMills(System.currentTimeMillis())
                .build();
        requestModel.signValue("63fda53a271966c8b4ac314b97c080dc");
        String headerValue = requestModel.toHeaderValue();
        System.out.println("Header Value : " + headerValue);
    }

    @Test
    public void testSign0() {
        AppRequestModel requestModel = AppRequestModel.builder()
                .id(2)
                .name("ST")
                .params(new ArrayList<>())
                .signMills(System.currentTimeMillis())
                .build();
        requestModel.signValue("e9da667d8bf72fc340a55b6d85af48eb");
        String headerValue = requestModel.toHeaderValue();
        System.out.println("Header Value : " + headerValue);
    }

}
