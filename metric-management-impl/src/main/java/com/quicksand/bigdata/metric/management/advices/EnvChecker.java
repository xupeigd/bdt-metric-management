package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.metric.management.metric.dbvos.MetricCatalogDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricCatalogDataManager;
import com.quicksand.bigdata.metric.management.metric.vos.MetricCatalogVO;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * @author page
 * @date 2022/9/15
 */
@Slf4j
@Component
public class EnvChecker
        implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${spring.profiles.active:}")
    String activeProfiles;
    @Value("${spring.redis.host}")
    String redisHost;
    @Value("${spring.redis.port}")
    int redisPort;
    @Value("${spring.redis.database}")
    int redisDb;
    @Value("${spring.datasource.url}")
    String mysqlUrl;
    @Value("${spring.datasource.username}")
    String mysqlUserName;
    @Value("${spring.datasource.password}")
    String mysqlPassword;
    @Value("${query.var.env.checks:false}")
    boolean enableChecker;
    @Value("${apollo.bootstrap.namespaces}")
    String apolloNamespaces;

    @Resource
    RedisTemplate<String, Long> longValueRedistemplate;
    @Resource
    DataSource dataSource;
    @Resource
    MetricCatalogDataManager metricCatalogDataManager;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("EVC config namespaces:{}", apolloNamespaces);
        if (enableChecker) {
            testCodis();
            testMysql();
            testCatlogs();
        }
    }

    private void testCodis() {
        log.info("EVC EnvChecker testCodis start! EVC");
        log.info("EVC host:{}", redisHost);
        log.info("EVC port:{}", redisPort);
        log.info("EVC db:{}", redisDb);
        Try.run(() -> {
                    String testKey = String.format("EC:%d", System.currentTimeMillis());
                    log.info("== testKey:{}", testKey);
                    longValueRedistemplate.opsForValue().set(testKey, System.currentTimeMillis(), 1, TimeUnit.MINUTES);
                    log.info("== value:{}", longValueRedistemplate.opsForValue().get(testKey));
                    log.info("== ttl:{}", longValueRedistemplate.getExpire(testKey));
                })
                .onFailure(ex -> log.error("xx testCodis error!", ex));
        log.info("EVC EnvChecker testCodis end! EVC");
    }

    private void testMysql() {
        log.info("EVC EnvChecker testMysql start! EVC");
        log.info("EVC url:{}", mysqlUrl);
        log.info("EVC userName:{}", mysqlUserName);
        log.info("EVC password:{}", mysqlPassword);
        Try.run(() -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                    jdbcTemplate.queryForList("show tables")
                            .forEach(v -> log.info("EVC ==> value:{}", JsonUtils.toJsonString(v)));
                })
                .onFailure(ex -> log.error("EVC xx testCodis error!", ex));
        log.info("EVC EnvChecker testMysql end! EVC");
    }

    private void testCatlogs() {
        log.info("EVC EnvChecker testCatlogs start! EVC");
        Try.run(() -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                    jdbcTemplate.queryForList("select * from t_metric_catalogs ")
                            .forEach(v -> log.info("EVC ==> value:{}", JsonUtils.toJsonString(v)));
                    MetricCatalogDBVO rootCatalog = metricCatalogDataManager.findById(0);
                    log.info("EVC ==> rootCatalog:{}", JsonUtils.toJsonString(rootCatalog));
                    MetricCatalogVO metricCatalogVO = JsonUtils.transfrom(rootCatalog, MetricCatalogVO.class);
                    log.info("EVC ==> rootCatalogVO:{}", JsonUtils.toJsonString(metricCatalogVO));
                })
                .onFailure(ex -> log.error("EVC xx testCatlogs error!", ex));
        log.info("EVC EnvChecker testCatlogs end! EVC");
    }

}
