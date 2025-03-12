package com.quicksand.bigdata.metric.management.job.repos;


import com.quicksand.bigdata.metric.management.job.core.model.JobLog;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * JobLogRepo
 *
 * @author page
 * @date 2023/2/2
 */
public interface JobLogRepo {

    List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize);

    Page<JobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus);

}
