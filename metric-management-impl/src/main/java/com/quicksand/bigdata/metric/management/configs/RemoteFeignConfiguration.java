package com.quicksand.bigdata.metric.management.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * FeignClientConfiguration
 *
 * @author page
 * @date 2022/8/12
 */
@ConditionalOnProperty(value = "remote.query.enable", havingValue = "true")
@EnableFeignClients(basePackages = {"com.quicksand.bigdata.query.rests"})
@Configuration
public class RemoteFeignConfiguration {
}
