package com.quicksand.bigdata.metric.management.job.repos;

import com.quicksand.bigdata.metric.management.job.core.model.JobLogGlue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JobLogGlueAutoRepo
 *
 * @author page
 * @date 2023/2/2
 */
@Repository
public interface JobLogGlueAutoRepo
        extends JpaRepository<JobLogGlue, Integer> {

    List<JobLogGlue> findByJobIdOrderByIdDesc(int jobId);

    @Modifying
    @Query(value = "DELETE  FROM tbl_metric_job_logglue WHERE id NOT in (  SELECT id FROM ( SELECT id FROM tbl_metric_job_logglue " +
            "WHERE `job_id` = :jobId ORDER BY update_time desc LIMIT 0, :limit ) t1 )  AND `job_id` = :jobId",
            nativeQuery = true)
    void removeOld(@Param("jobId") int jobId, @Param("limit") int limit);

}