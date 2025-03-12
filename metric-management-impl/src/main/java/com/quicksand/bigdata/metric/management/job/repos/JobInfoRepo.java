package com.quicksand.bigdata.metric.management.job.repos;

import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import org.springframework.data.domain.Page;

/**
 * JobInfoRepo
 *
 * @author page
 * @date 2023/2/1
 */
public interface JobInfoRepo {

    Page<JobInfo> queryJobInfos(int offset, int pagesize, int jobGroup, int triggerStatus,
                                String jobDesc, String executorHandler, String author);

}