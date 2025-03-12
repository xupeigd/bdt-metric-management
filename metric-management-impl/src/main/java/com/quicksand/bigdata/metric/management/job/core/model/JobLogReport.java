package com.quicksand.bigdata.metric.management.job.core.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "tbl_metric_job_log_report")
public class JobLogReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    @Column(name = "trigger_day")
    Date triggerDay;

    @Column(name = "running_count")
    int runningCount;

    @Column(name = "suc_count")
    int sucCount;

    @Column(name = "fail_count")
    int failCount;

}
