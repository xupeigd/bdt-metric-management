package com.quicksand.bigdata.metric.management.job.core.thread;

import com.quicksand.bigdata.metric.management.job.core.conf.MetricJobManagementConfig;
import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import com.quicksand.bigdata.metric.management.job.core.model.JobLog;
import com.quicksand.bigdata.metric.management.job.core.trigger.TriggerTypeEnum;
import com.quicksand.bigdata.metric.management.job.core.util.I18nUtil;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * job monitor instance
 *
 * @author xuxueli 2015-9-1 18:05:56
 */
@Slf4j
public class JobFailMonitorHelper {

    private static JobFailMonitorHelper instance = new JobFailMonitorHelper();
    private Thread monitorThread;

    // ---------------------- monitor ----------------------
    private volatile boolean toStop = false;

    public static JobFailMonitorHelper getInstance() {
        return instance;
    }

    @Transactional
    public void start() {
        monitorThread = new Thread(() -> {
            // monitor
            while (!toStop) {
                try {
                    List<Long> failLogIds = MetricJobManagementConfig.getAdminConfig().getJobLogAutoRepo().findFailJobLogs(1000).stream()
                            .map(JobLog::getId)
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(failLogIds)) {
                        for (long failLogId : failLogIds) {
                            // lock log
                            int lockRet = Try.of(() -> {
                                        MetricJobManagementConfig.getAdminConfig().getJobLogAutoRepo().updateAlarmStatus(failLogId, 0, -1);
                                        return 1;
                                    })
                                    .getOrElse(0);
                            if (lockRet < 1) {
                                continue;
                            }
                            JobLog log = MetricJobManagementConfig.getAdminConfig().getJobLogAutoRepo().findById(failLogId).orElse(null);
                            JobInfo info = MetricJobManagementConfig.getAdminConfig().getJobInfoDao().findById(null == log ? 0 : log.getJobId()).orElse(null);
                            // 1、fail retry monitor
                            if (log.getExecutorFailRetryCount() > 0) {
                                JobTriggerPoolHelper.trigger(log.getJobId(), TriggerTypeEnum.RETRY, (log.getExecutorFailRetryCount() - 1), log.getExecutorShardingParam(), log.getExecutorParam(), null);
                                String retryMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>" + I18nUtil.getString("jobconf_trigger_type_retry") + "<<<<<<<<<<< </span><br>";
                                log.setTriggerMsg(log.getTriggerMsg() + retryMsg);
                                MetricJobManagementConfig.getAdminConfig().getJobLogAutoRepo().save(log);
                            }
                            // 2、fail alarm monitor
                            int newAlarmStatus = 0;        // 告警状态：0-默认、-1=锁定状态、1-无需告警、2-告警成功、3-告警失败
                            if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {
                                boolean alarmResult = MetricJobManagementConfig.getAdminConfig().getJobAlarmer().alarm(info, log);
                                newAlarmStatus = alarmResult ? 2 : 3;
                            } else {
                                newAlarmStatus = 1;
                            }
                            MetricJobManagementConfig.getAdminConfig().getJobLogAutoRepo().updateAlarmStatus(failLogId, -1, newAlarmStatus);
                        }
                    }
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(">>>>>>>>>>> metric-jobs, job fail monitor thread error:{}", e);
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info(">>>>>>>>>>> metric-jobs, job fail monitor thread stop");
        });
        monitorThread.setDaemon(true);
        monitorThread.setName("metric-jobs, admin JobFailMonitorHelper");
        monitorThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        monitorThread.interrupt();
        try {
            monitorThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}
