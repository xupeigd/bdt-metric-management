package com.quicksand.bigdata.metric.management.job.core.thread;

import com.quicksand.bigdata.metric.management.job.core.conf.MetricJobManagementConfig;
import com.quicksand.bigdata.metric.management.job.core.model.JobGroup;
import com.quicksand.bigdata.metric.management.job.core.model.JobRegistry;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.*;

import static org.springframework.util.StringUtils.hasText;

/**
 * job registry instance
 *
 * @author xuxueli 2016-10-02 19:10:24
 */
@Slf4j
public class JobRegistryHelper {

    private static JobRegistryHelper instance = new JobRegistryHelper();
    private ThreadPoolExecutor registryOrRemoveThreadPool = null;
    private Thread registryMonitorThread;
    private volatile boolean toStop = false;

    public static JobRegistryHelper getInstance() {
        return instance;
    }

    @Transactional
    public void start() {

        // for registry or remove
        registryOrRemoveThreadPool = new ThreadPoolExecutor(
                2,
                10,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "metric-jobs, admin JobRegistryMonitorHelper-registryOrRemoveThreadPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        r.run();
                        log.warn(">>>>>>>>>>> metric-jobs, registry or remove too fast, match threadpool rejected handler(run now).");
                    }
                });

        // for monitor
        registryMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // auto registry group
                        List<JobGroup> groupList = MetricJobManagementConfig.getAdminConfig().getJobGroupAutoRepo().findByAddressType(0);
                        if (groupList != null && !groupList.isEmpty()) {

                            // remove dead address (admin/executor)
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.SECOND, -RegistryConfig.DEAD_TIMEOUT);
                            List<JobRegistry> deadRegistries = MetricJobManagementConfig.getAdminConfig().getJobRegistryAutoRepo().findByUpdateTimeBefore(calendar.getTime());
                            if (!CollectionUtils.isEmpty(deadRegistries)) {
                                MetricJobManagementConfig.getAdminConfig().getJobRegistryAutoRepo().deleteAll(deadRegistries);
                            }
                            // List<Integer> ids = XxlJobAdminConfig.getAdminConfig().getJobRegistryAutoRepo().findDead(RegistryConfig.DEAD_TIMEOUT, new Date());
                            // if (ids != null && ids.size() > 0) {
                            //     XxlJobAdminConfig.getAdminConfig().getJobRegistryAutoRepo().removeDead(ids);
                            // }

                            // fresh online address (admin/executor)
                            HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
                            List<JobRegistry> list = MetricJobManagementConfig.getAdminConfig().getJobRegistryAutoRepo().findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
                            if (list != null) {
                                for (JobRegistry item : list) {
                                    if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                                        String appname = item.getRegistryKey();
                                        List<String> registryList = appAddressMap.get(appname);
                                        if (registryList == null) {
                                            registryList = new ArrayList<String>();
                                        }

                                        if (!registryList.contains(item.getRegistryValue())) {
                                            registryList.add(item.getRegistryValue());
                                        }
                                        appAddressMap.put(appname, registryList);
                                    }
                                }
                            }

                            // fresh group address
                            for (JobGroup group : groupList) {
                                List<String> registryList = appAddressMap.get(group.getAppname());
                                String addressListStr = null;
                                if (registryList != null && !registryList.isEmpty()) {
                                    Collections.sort(registryList);
                                    StringBuilder addressListSB = new StringBuilder();
                                    for (String item : registryList) {
                                        addressListSB.append(item).append(",");
                                    }
                                    addressListStr = addressListSB.toString();
                                    addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
                                }
                                group.setAddressList(addressListStr);
                                group.setUpdateTime(new Date());

                                MetricJobManagementConfig.getAdminConfig().getJobGroupAutoRepo().save(group);
                            }
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            log.error(">>>>>>>>>>> metric-jobs, job registry monitor thread error:{}", e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        if (!toStop) {
                            log.error(">>>>>>>>>>> metric-jobs, job registry monitor thread error:{}", e);
                        }
                    }
                }
                log.info(">>>>>>>>>>> metric-jobs, job registry monitor thread stop");
            }
        });
        registryMonitorThread.setDaemon(true);
        registryMonitorThread.setName("metric-jobs, admin JobRegistryMonitorHelper-registryMonitorThread");
        registryMonitorThread.start();
    }

    public void toStop() {
        toStop = true;

        // stop registryOrRemoveThreadPool
        registryOrRemoveThreadPool.shutdownNow();

        // stop monitir (interrupt and wait)
        registryMonitorThread.interrupt();
        try {
            registryMonitorThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }


    // ---------------------- helper ----------------------
    @Transactional
    public ReturnT<String> registry(RegistryParam registryParam) {

        // valid
        if (!hasText(registryParam.getRegistryGroup())
                || !hasText(registryParam.getRegistryKey())
                || !hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        // async execute
        registryOrRemoveThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                int ret = Try.of(() -> {
                            JobRegistry existRegistry = MetricJobManagementConfig.getAdminConfig().getJobRegistryAutoRepo().findTopByRegistryGroupAndRegistryKeyAndRegistryValueOrderByUpdateTimeDesc(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
                            if (null != existRegistry) {
                                existRegistry.setUpdateTime(new Date());
                                MetricJobManagementConfig.getAdminConfig().getJobRegistryAutoRepo().save(existRegistry);
                                return 1;
                            }
                            return 0;
                        })
                        .getOrElse(0);
                if (ret < 1) {
                    MetricJobManagementConfig.getAdminConfig()
                            .getJobRegistryAutoRepo()
                            .save(JobRegistry.builder()
                                    .registryKey(registryParam.getRegistryKey())
                                    .registryGroup(registryParam.getRegistryGroup())
                                    .registryValue(registryParam.getRegistryValue())
                                    .updateTime(new Date())
                                    .build());
                    // fresh
                    freshGroupRegistryInfo(registryParam);
                }
            }
        });

        return ReturnT.SUCCESS;
    }

    @Transactional
    public ReturnT<String> registryRemove(RegistryParam registryParam) {

        // valid
        if (!hasText(registryParam.getRegistryGroup())
                || !hasText(registryParam.getRegistryKey())
                || !hasText(registryParam.getRegistryValue())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        // async execute
        registryOrRemoveThreadPool.execute(() -> {
            int ret = Try.of(() -> {
                        MetricJobManagementConfig.getAdminConfig().getJobRegistryAutoRepo().registryDelete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
                        return 1;
                    })
                    .getOrElse(0);
            if (ret > 0) {
                // fresh
                freshGroupRegistryInfo(registryParam);
            }
        });

        return ReturnT.SUCCESS;
    }

    private void freshGroupRegistryInfo(RegistryParam registryParam) {
        // Under consideration, prevent affecting core tables
    }


}
