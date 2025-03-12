package com.quicksand.bigdata.metric.management.job.core.thread;

import com.quicksand.bigdata.metric.management.job.core.conf.MetricJobManagementConfig;
import com.quicksand.bigdata.metric.management.job.core.model.JobLogReport;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * job log report helper
 *
 * @author xuxueli 2019-11-22
 */
@Slf4j
public class JobLogReportHelper {

    private static JobLogReportHelper instance = new JobLogReportHelper();
    private Thread logrThread;
    private volatile boolean toStop = false;

    public static JobLogReportHelper getInstance() {
        return instance;
    }

    public void start() {
        logrThread = new Thread(new Runnable() {

            @Override
            public void run() {

                // last clean log time
                long lastCleanLogTime = 0;


                while (!toStop) {

                    // 1、log-report refresh: refresh log report in 3 days
                    try {

                        for (int i = 0; i < 3; i++) {

                            // today
                            Calendar itemDay = Calendar.getInstance();
                            itemDay.add(Calendar.DAY_OF_MONTH, -i);
                            itemDay.set(Calendar.HOUR_OF_DAY, 0);
                            itemDay.set(Calendar.MINUTE, 0);
                            itemDay.set(Calendar.SECOND, 0);
                            itemDay.set(Calendar.MILLISECOND, 0);

                            Date todayFrom = itemDay.getTime();

                            itemDay.set(Calendar.HOUR_OF_DAY, 23);
                            itemDay.set(Calendar.MINUTE, 59);
                            itemDay.set(Calendar.SECOND, 59);
                            itemDay.set(Calendar.MILLISECOND, 999);

                            Date todayTo = itemDay.getTime();

                            // refresh log-report every minute
                            JobLogReport jobLogReport = new JobLogReport();
                            jobLogReport.setTriggerDay(todayFrom);
                            jobLogReport.setRunningCount(0);
                            jobLogReport.setSucCount(0);
                            jobLogReport.setFailCount(0);

                            List<JobLogReport> jobLogReports = MetricJobManagementConfig.getAdminConfig().getJobLogReportAutoRepo().queryLogReport(todayFrom, todayTo);
                            if (!CollectionUtils.isEmpty(jobLogReports)) {
                                jobLogReport = jobLogReports.get(0);
                            }

                            Map<String, Object> triggerCountMap = MetricJobManagementConfig.getAdminConfig().getJobLogAutoRepo().findLogReport(todayFrom, todayTo);
                            if (triggerCountMap != null && triggerCountMap.size() > 0) {
                                //noinspection UnnecessaryBoxing
                                int triggerDayCount = triggerCountMap.containsKey("triggerDayCount") && null != triggerCountMap.get("triggerDayCount") ? Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCount"))) : 0;
                                //noinspection UnnecessaryBoxing
                                int triggerDayCountRunning = triggerCountMap.containsKey("triggerDayCountRunning") && null != triggerCountMap.get("triggerDayCountRunning") ? Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCountRunning"))) : 0;
                                //noinspection UnnecessaryBoxing
                                int triggerDayCountSuc = triggerCountMap.containsKey("triggerDayCountSuc") && null != triggerCountMap.get("triggerDayCountSuc") ? Integer.valueOf(String.valueOf(triggerCountMap.get("triggerDayCountSuc"))) : 0;
                                int triggerDayCountFail = triggerDayCount - triggerDayCountRunning - triggerDayCountSuc;

                                jobLogReport.setRunningCount(triggerDayCountRunning);
                                jobLogReport.setSucCount(triggerDayCountSuc);
                                jobLogReport.setFailCount(triggerDayCountFail);
                            }

                            // do refresh
                            JobLogReport finalJobLogReport = jobLogReport;
                            int ret = Try.of(() -> {
                                        MetricJobManagementConfig.getAdminConfig().getJobLogReportAutoRepo().save(finalJobLogReport);
                                        return 1;
                                    })
                                    .getOrElse(0);
                            if (ret < 1) {
                                MetricJobManagementConfig.getAdminConfig().getJobLogReportAutoRepo().save(jobLogReport);
                            }
                        }

                    } catch (Exception e) {
                        if (!toStop) {
                            log.error(">>>>>>>>>>> metric-jobs, job log report thread error:{}", e);
                        }
                    }

                    // 2、log-clean: switch open & once each day
                    if (MetricJobManagementConfig.getAdminConfig().getLogretentiondays() > 0
                            && System.currentTimeMillis() - lastCleanLogTime > 24 * 60 * 60 * 1000) {

                        // expire-time
                        Calendar expiredDay = Calendar.getInstance();
                        expiredDay.add(Calendar.DAY_OF_MONTH, -1 * MetricJobManagementConfig.getAdminConfig().getLogretentiondays());
                        expiredDay.set(Calendar.HOUR_OF_DAY, 0);
                        expiredDay.set(Calendar.MINUTE, 0);
                        expiredDay.set(Calendar.SECOND, 0);
                        expiredDay.set(Calendar.MILLISECOND, 0);
                        Date clearBeforeTime = expiredDay.getTime();

                        // clean expired log
                        List<Long> logIds = null;
                        do {
                            logIds = MetricJobManagementConfig.getAdminConfig().getJobLogRepo().findClearLogIds(0, 0, clearBeforeTime, 0, 1000);
                            if (logIds != null && logIds.size() > 0) {
                                MetricJobManagementConfig.getAdminConfig().getJobLogAutoRepo().deleteByIds(logIds);
                            }
                        } while (logIds != null && logIds.size() > 0);

                        // update clean time
                        lastCleanLogTime = System.currentTimeMillis();
                    }

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (Exception e) {
                        if (!toStop) {
                            log.error(e.getMessage(), e);
                        }
                    }

                }

                log.info(">>>>>>>>>>> metric-jobs, job log report thread stop");

            }
        });
        logrThread.setDaemon(true);
        logrThread.setName("metric-jobs, admin JobLogReportHelper");
        logrThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        logrThread.interrupt();
        try {
            logrThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

}
