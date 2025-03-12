package com.quicksand.bigdata.metric.management.job.core.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * xxl-job log, used to track trigger process
 *
 * @author xuxueli  2015-12-19 23:19:09
 */
@Data
@Entity
@Table(name = "tbl_metric_job_log")
public class JobLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Long id;

    // job info
    @Column(name = "job_group")
    int jobGroup;

    @Column(name = "job_id")
    int jobId;

    // execute info
    @Column(name = "executor_address")
    String executorAddress;

    @Column(name = "executor_handler")
    String executorHandler;

    @Column(name = "executor_param")
    String executorParam;

    @Column(name = "executor_sharding_param")
    String executorShardingParam;

    @Column(name = "executor_fail_retry_count")
    int executorFailRetryCount;

    // trigger info
    @Column(name = "trigger_time")
    Date triggerTime;

    @Column(name = "trigger_code")
    int triggerCode;

    @Column(name = "trigger_msg")
    String triggerMsg;

    // handle info
    @Column(name = "handle_time")
    Date handleTime;

    @Column(name = "handle_code")
    int handleCode;

    @Column(name = "handle_msg")
    String handleMsg;

    // alarm info
    @Column(name = "alarm_status")
    int alarmStatus;

}
