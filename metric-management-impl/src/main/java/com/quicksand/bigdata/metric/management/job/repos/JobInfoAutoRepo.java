package com.quicksand.bigdata.metric.management.job.repos;

import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
@Repository
public interface JobInfoAutoRepo
        extends JpaRepository<JobInfo, Integer> {

    List<JobInfo> findByJobGroup(int jobGroup);

    @Query(value = "SELECT * FROM tbl_metric_job_info AS t WHERE t.trigger_status = 1 and t.trigger_next_time  <=  :maxNextTime ",
            countQuery = "SELECT count() FROM tbl_metric_job_info AS t WHERE t.trigger_status = 1 and t.trigger_next_time  <=  :maxNextTime ",
            nativeQuery = true)
    Page<JobInfo> findByTriggerNextTimeBefore(long maxNextTime, Pageable pageable);

    JobInfo findByExecutorHandler(String executorHandler);

}
