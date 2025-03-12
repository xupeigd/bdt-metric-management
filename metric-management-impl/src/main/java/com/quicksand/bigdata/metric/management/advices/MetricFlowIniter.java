package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.metric.management.datasource.vos.ClusterInfoVO;
import com.quicksand.bigdata.metric.management.yaml.services.impls.MetricFileService;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * MetricFlowIniter
 *
 * @author page
 * @date 2022/8/19
 */
@Slf4j
@Component
public class MetricFlowIniter
        implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${bdt.management.mf.conf.path:/root/.metricflow}")
    String mfConfigPath;
    @Value("${bdt.management.mf.conf.path:bdt}")
    String mfModulePath;
    @Value("${spring.datasource.host:m3sql}")
    String dwhHost;
    @Value("${spring.datasource.port:9918}")
    int dwhPort;
    @Value("${spring.datasource.database:db_metric_management}")
    String dwhDatabase;
    @Value("${spring.datasource.username:page.Hsu}")
    String dwhUser;
    @Value("${spring.datasource.username:123@456}")
    String dwhPassword;

    @Resource
    MetricFileService metricFileService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        initMetricflowConfigs();
    }

    private void initMetricflowConfigs() {
        File file = new File(mfConfigPath + "/" + mfModulePath);
        Try.run(() -> {
                    //判断配置目录是否存在
                    if (!file.exists()) {
                        log.warn("mf config dir not exist , try create it . path:{}", file.getAbsolutePath());
                        if (!file.mkdirs()) {
                            log.warn("mf config dir not exist and create fail ! path:{}", file.getAbsolutePath());
                        }
                    }
                })
                .onFailure(ex -> log.error("mf config dir not exist and create fail !", ex));
        Try.run(() -> {
                    if (file.exists()) {
                        //创建默认的配置目录
                        if (!metricFileService.replaceConfigFile(ClusterInfoVO.builder()
                                .address(String.format("%s:%d", dwhHost, dwhPort))
                                .defaultDatabase(dwhDatabase)
                                .userName(dwhUser)
                                .password(dwhPassword)
                                .comment("Tmp")
                                .type("mysql")
                                .build())) {
                            log.warn("mf config create fail ! ");
                        }
                    }
                })
                .onFailure(ex -> log.warn("mf config not exist and create fail !", ex));

    }

}
