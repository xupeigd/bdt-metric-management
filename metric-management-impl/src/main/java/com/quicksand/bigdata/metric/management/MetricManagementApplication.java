package com.quicksand.bigdata.metric.management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@OpenAPIDefinition(info = @Info(
        title = "Metric-Management",
        version = "1.1.0",
        description = "Documentation APIs v 1.1.0-SNAPSHOT"
))
@SpringBootApplication(
        scanBasePackages = {
                "com.quicksand.bigdata.metric.management",
                "com.quicksand.bigdata.metric.management.advices",
                "com.quicksand.bigdata.vars"
        })
@EnableTransactionManagement
public class MetricManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricManagementApplication.class, args);
    }

}
