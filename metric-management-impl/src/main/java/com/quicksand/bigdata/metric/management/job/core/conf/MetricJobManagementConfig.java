package com.quicksand.bigdata.metric.management.job.core.conf;

import com.quicksand.bigdata.metric.management.job.core.alarm.JobAlarmer;
import com.quicksand.bigdata.metric.management.job.core.scheduler.MetricJobScheduler;
import com.quicksand.bigdata.metric.management.job.repos.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Arrays;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Transactional
@Component
public class MetricJobManagementConfig
        implements InitializingBean, DisposableBean {

    private static MetricJobManagementConfig adminConfig = null;

    @Resource
    JobInfoAutoRepo jobInfoAutoRepo;
    // dao, service
    @Resource
    JobGroupAutoRepo jobGroupAutoRepo;
    @Resource
    JobLogAutoRepo jobLogAutoRepo;
    @Resource
    JobLogRepo jobLogRepo;
    @Resource
    JobLogReportAutoRepo jobLogReportAutoRepo;
    @Resource
    JobRegistryAutoRepo jobRegistryAutoRepo;
    // ---------------------- XxlJobScheduler ----------------------
    @Value("${xxl.job.triggerpool.fast.max}")
    int triggerPoolFastMax;
    @Value("${xxl.job.triggerpool.slow.max}")
    int triggerPoolSlowMax;
    @Value("${xxl.job.logretentiondays}")
    int logretentiondays;
    @Resource
    JavaMailSender mailSender;
    @Resource
    DataSource dataSource;
    @Resource
    JobAlarmer jobAlarmer;
    private MetricJobScheduler metricJobScheduler;
    // ---------------------- XxlJobScheduler ----------------------
    // conf
    @Value("${xxl.job.i18n}")
    private String i18n;
    @Value("${xxl.job.accessToken}")
    private String accessToken;
    @Value("${spring.mail.from}")
    private String emailFrom;

    public static MetricJobManagementConfig getAdminConfig() {
        return adminConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;

        metricJobScheduler = new MetricJobScheduler();
        metricJobScheduler.init();
    }

    @Override
    public void destroy() throws Exception {
        metricJobScheduler.destroy();
    }

    public String getI18n() {
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

    public int getLogretentiondays() {
        if (logretentiondays < 7) {
            return -1;  // Limit greater than or equal to 7, otherwise close
        }
        return logretentiondays;
    }

    public JobLogAutoRepo getJobLogAutoRepo() {
        return jobLogAutoRepo;
    }

    public JobLogRepo getJobLogRepo() {
        return jobLogRepo;
    }

    public JobInfoAutoRepo getJobInfoDao() {
        return jobInfoAutoRepo;
    }

    public JobRegistryAutoRepo getJobRegistryAutoRepo() {
        return jobRegistryAutoRepo;
    }

    public JobGroupAutoRepo getJobGroupAutoRepo() {
        return jobGroupAutoRepo;
    }

    public JobLogReportAutoRepo getJobLogReportAutoRepo() {
        return jobLogReportAutoRepo;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JobAlarmer getJobAlarmer() {
        return jobAlarmer;
    }

}
