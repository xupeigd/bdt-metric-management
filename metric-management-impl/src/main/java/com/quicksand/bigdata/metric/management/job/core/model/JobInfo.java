package com.quicksand.bigdata.metric.management.job.core.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
@Data
@Entity
@Table(name = "tbl_metric_job_info")
public class JobInfo {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    // 执行器主键ID
    @Column(name = "job_group")
    int jobGroup;

    @Column(name = "job_desc")
    String jobDesc;

    @Column(name = "add_time")
    Date addTime;

    @Column(name = "update_time")
    Date updateTime;

    // 负责人
    @Column
    String author;

    // 报警邮件
    @Column(name = "alarm_email")
    String alarmEmail;

    // 调度类型
    @Column(name = "schedule_type")
    String scheduleType;

    // 调度配置，值含义取决于调度类型
    @Column(name = "schedule_conf")
    String scheduleConf;

    // 调度过期策略
    @Column(name = "misfire_strategy")
    String misfireStrategy;

    // 执行器路由策略
    @Column(name = "executor_route_strategy")
    String executorRouteStrategy;

    // 执行器，任务Handler名称
    @Column(name = "executor_handler")
    String executorHandler;

    // 执行器，任务参数
    @Column(name = "executor_param")
    String executorParam;

    // 阻塞处理策略
    @Column(name = "executor_block_strategy")
    String executorBlockStrategy;

    // 任务执行超时时间，单位秒
    @Column(name = "executor_timeout")
    int executorTimeout;

    // 失败重试次数
    @Column(name = "executor_fail_retry_count")
    int executorFailRetryCount;

    // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    @Column(name = "glue_type")
    String glueType;

    // GLUE源代码
    @Column(name = "glue_source")
    String glueSource;

    // GLUE备注
    @Column(name = "glue_remark")
    String glueRemark;

    // GLUE更新时间
    @Column(name = "glue_updatetime")
    Date glueUpdatetime;

    // 子任务ID，多个逗号分隔
    @Column(name = "child_jobid")
    String childJobId;

    // 调度状态：0-停止，1-运行
    @Column(name = "trigger_status")
    int triggerStatus;

    // 上次调度时间
    @Column(name = "trigger_last_time")
    long triggerLastTime;

    // 下次调度时间
    @Column(name = "trigger_next_time")
    long triggerNextTime;

}
