package com.quicksand.bigdata.metric.management.metric.services.impl;

import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * MetricServiceImplTest
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/18 09:46
 * @description
 */

@SpringBootTest
class MetricServiceImplTest {

    @Resource
    MetricService metricService;

//    @Test
//    void verifyYamlMergeSegment() throws IOException {
//        String testContent = FileUtil.readAsString(ResourceUtils.getFile("classpath:UserMetric.yaml"));
//        metricService.verifyYamlMergeSegment(null, testContent, false).getVerifySuccess();
//        System.out.println(testContent);
//    }
}