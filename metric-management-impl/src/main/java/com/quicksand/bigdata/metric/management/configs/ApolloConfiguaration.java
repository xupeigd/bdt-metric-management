package com.quicksand.bigdata.metric.management.configs;

//import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * ApolloConfiguaration
 *
 * @author page
 * @date 2022/9/14
 */
@Configuration
//@EnableApolloConfig
@ConditionalOnProperty(name = "management.vars.apollo.enable", havingValue = "true")
public class ApolloConfiguaration {
}
