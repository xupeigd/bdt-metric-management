package com.quicksand.bigdata.metric.management.job.core.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * xxl-job log for glue, used to track job code process
 *
 * @author xuxueli 2016-5-19 17:57:46
 */
@Data
@Entity
@Table(name = "tbl_metric_job_logglue")
public class JobLogGlue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    // 任务主键ID
    @Column(name = "job_id")
    int jobId;

    // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    @Column(name = "glue_type")
    String glueType;

    @Column(name = "glue_source")
    String glueSource;

    @Column(name = "glue_remark")
    String glueRemark;

    @Column(name = "add_time")
    Date addTime;

    @Column(name = "update_time")
    Date updateTime;

}
