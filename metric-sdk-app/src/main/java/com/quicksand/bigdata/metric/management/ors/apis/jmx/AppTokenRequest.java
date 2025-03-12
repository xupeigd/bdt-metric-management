package com.quicksand.bigdata.metric.management.ors.apis.jmx;

import com.quicksand.bigdata.metric.management.ors.apis.AppRequestModel;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.ArrayList;

/**
 * AppTokenRequest
 *
 * @author page
 * @date 2022/12/9
 */
public class AppTokenRequest
        extends AbstractJavaSamplerClient {

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult sampleResult = new SampleResult();
        sampleResult.setSampleLabel("生成token");
        sampleResult.sampleStart();
        String atid = null == javaSamplerContext.getJMeterVariables().get("ATID") ? "2" : javaSamplerContext.getJMeterVariables().get("ATID");
        String atToken = null == javaSamplerContext.getJMeterVariables().get("ATToken") ? "e9da667d8bf72fc340a55b6d85af48eb" : javaSamplerContext.getJMeterVariables().get("ATToken");
        long atoffset = Long.parseLong(null == javaSamplerContext.getJMeterVariables().get("ATOS") ? "0" : javaSamplerContext.getJMeterVariables().get("ATOS"));
        AppRequestModel requestModel = AppRequestModel.builder()
                .id(Integer.parseInt(atid))
                .name("MTC")
                .params(new ArrayList<>())
                .signMills(System.currentTimeMillis() + atoffset)
                .build();
        requestModel.signValue(atToken);
        String headerValue = requestModel.toHeaderValue();
        javaSamplerContext.getJMeterVariables().put("AUA", headerValue);
        sampleResult.setSuccessful(true);
        sampleResult.setResponseData(headerValue, "UTF-8");
        sampleResult.sampleEnd();
        return sampleResult;
    }

}
