package com.quicksand.bigdata.metric.management.job.repos;

import com.quicksand.bigdata.metric.management.job.core.model.JobLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JobLogReportAutoRepo
 *
 * @author page
 * @date 2023/2/2
 */
@Repository
public interface JobLogReportAutoRepo
        extends JpaRepository<JobLogReport, Integer> {

    @Query(value = "SELECT * FROM tbl_metric_job_log_report AS t WHERE t.trigger_day between :triggerDayFrom and :triggerDayTo ORDER BY t.trigger_day ASC",
            nativeQuery = true)
    List<JobLogReport> queryLogReport(@Param("triggerDayFrom") Date triggerDayFrom,
                                      @Param("triggerDayTo") Date triggerDayTo);

    @Query(value = "SELECT SUM(running_count) running_count, SUM(suc_count) suc_count, SUM(fail_count) fail_count FROM tbl_metric_job_log_report AS t",
            nativeQuery = true)
    Map<String, Object> queryLogReportTotal();

}
