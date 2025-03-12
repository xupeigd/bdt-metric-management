package com.quicksand.bigdata.metric.management.job.repos;

import com.quicksand.bigdata.metric.management.job.core.model.JobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
@Repository
public interface JobLogAutoRepo
        extends JpaRepository<JobLog, Long> {

    @Modifying
    @Query(value = "delete from tbl_metric_job_log where id in :ids",
            nativeQuery = true)
    void deleteByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM `tbl_metric_job_log` WHERE !( (trigger_code in (0 , 200) and handle_code = 0) OR (handle_code = 200) )" +
            " AND `alarm_status` = 0 ORDER BY id ASC LIMIT :pageSize",
            nativeQuery = true)
    List<JobLog> findFailJobLogs(@Param("pageSize") int pageSize);

    // @Transactional
    @Modifying
    @Query(value = "UPDATE tbl_metric_job_log SET `alarm_status` = :newAlarmStatus WHERE `id` = :id AND `alarm_status` = :oldAlarmStatus",
            nativeQuery = true)
    void updateAlarmStatus(@Param("id") long id, @Param("oldAlarmStatus") int oldAlarmStatus, @Param("newAlarmStatus") int newAlarmStatus);

    @Query(value = "SELECT COUNT(handle_code) triggerDayCount, SUM(CASE WHEN (trigger_code in (0, 200) and handle_code = 0) then 1 else 0 end) as triggerDayCountRunning," +
            " SUM(CASE WHEN handle_code = 200 then 1 else 0 end) as triggerDayCountSuc FROM tbl_metric_job_log WHERE trigger_time BETWEEN :from and :to ",
            nativeQuery = true)
    Map<String, Object> findLogReport(@Param("from") Date from,
                                      @Param("to") Date to);

    @Query(value = "SELECT t.id FROM tbl_metric_job_log t LEFT JOIN tbl_metric_job_registry t2 ON t.executor_address = t2.registry_value " +
            " WHERE t.trigger_code = 200 AND t.handle_code = 0 AND t.trigger_time <=  :losedTime AND t2.id IS NULL;",
            nativeQuery = true)
    List<Long> findLostJobIds(@Param("losedTime") Date losedTime);

}
