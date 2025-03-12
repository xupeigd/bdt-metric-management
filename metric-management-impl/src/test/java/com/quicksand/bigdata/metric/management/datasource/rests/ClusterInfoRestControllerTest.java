package com.quicksand.bigdata.metric.management.datasource.rests;

import com.quicksand.bigdata.vars.http.model.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * ClusterInfoRestControllerTest
 *
 * @author page
 * @date 2022/8/2
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClusterInfoRestControllerTest {

    @Resource
    TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @Test
    void queryClusterInfos() {
        Response response = restTemplate.getForObject(String.format("http://127.0.0.1:%d/datasource/clusters", port), Response.class);
        Assert.notNull(response);
    }

    @Test
    void queryTables() {
    }

    @Test
    void queryColumns() {
    }
}