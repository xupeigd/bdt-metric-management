-- ----------------------------
-- since branch_xxl
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_metric_job_info
-- ----------------------------

CREATE TABLE `tbl_metric_job_info`
(
    `id`                        int(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `job_group`                 int(11) NOT NULL DEFAULT 0 COMMENT '执行器主键ID',
    `job_desc`                  varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
    `add_time`                  datetime              DEFAULT NULL COMMENT '增加时间',
    `update_time`               datetime              DEFAULT NULL COMMENT '更新时间',
    `author`                    varchar(64)           DEFAULT NULL COMMENT '作者',
    `alarm_email`               varchar(255)          DEFAULT NULL COMMENT '报警邮件',
    `schedule_type`             varchar(50)  NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
    `schedule_conf`             varchar(128)          DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
    `misfire_strategy`          varchar(50)  NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
    `executor_route_strategy`   varchar(50)           DEFAULT NULL COMMENT '执行器路由策略',
    `executor_handler`          varchar(255)          DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512)          DEFAULT NULL COMMENT '执行器任务参数',
    `executor_block_strategy`   varchar(50)           DEFAULT NULL COMMENT '阻塞处理策略',
    `executor_timeout`          int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
    `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    `glue_type`                 varchar(50)  NOT NULL DEFAULT '' COMMENT 'GLUE类型',
    `glue_source`               mediumtext COMMENT 'GLUE源代码',
    `glue_remark`               varchar(128)          DEFAULT NULL COMMENT 'GLUE备注',
    `glue_updatetime`           datetime              DEFAULT NULL COMMENT 'GLUE更新时间',
    `child_jobid`               varchar(255)          DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    `trigger_status`            tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
    `trigger_last_time`         bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
    `trigger_next_time`         bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '作业信息表';

-- ----------------------------
-- Table structure for tbl_metric_job_log
-- ----------------------------

CREATE TABLE `tbl_metric_job_log`
(
    `id`                        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `job_group`                 int(11) NOT NULL DEFAULT 0 COMMENT '执行器主键ID',
    `job_id`                    int(11) NOT NULL DEFAULT 0 COMMENT '任务，主键ID',
    `executor_address`          varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    `executor_handler`          varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
    `executor_sharding_param`   varchar(20)  DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    `trigger_time`              datetime     DEFAULT NULL COMMENT '调度-时间',
    `trigger_code`              int(11) NOT NULL DEFAULT 0 COMMENT '调度-结果',
    `trigger_msg`               text COMMENT '调度-日志',
    `handle_time`               datetime     DEFAULT NULL COMMENT '执行-时间',
    `handle_code`               int(11) NOT NULL DEFAULT 0 COMMENT '执行-状态',
    `handle_msg`                text COMMENT '执行-日志',
    `alarm_status`              tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
    PRIMARY KEY (`id`),
    KEY                         `idx_trigger_time` (`trigger_time`),
    KEY                         `idx_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '作业调度日志表';

-- ----------------------------
-- Table structure for tbl_metric_job_log_report
-- ----------------------------

CREATE TABLE `tbl_metric_job_log_report`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `trigger_day`   datetime DEFAULT NULL COMMENT '调度-时间',
    `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
    `suc_count`     int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
    `fail_count`    int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
    `update_time`   datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '作业调度日志报告';

-- ----------------------------
-- Table structure for tbl_metric_job_logglue
-- ----------------------------

CREATE TABLE `tbl_metric_job_logglue`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `job_id`      int(11) NOT NULL DEFAULT 0 COMMENT '任务，主键ID',
    `glue_type`   varchar(50)           DEFAULT NULL COMMENT 'GLUE类型',
    `glue_source` mediumtext COMMENT 'GLUE源代码',
    `glue_remark` varchar(128) NOT NULL DEFAULT '' COMMENT 'GLUE备注',
    `add_time`    datetime              DEFAULT NULL COMMENT '新增时间',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'GLUE码表';

-- ----------------------------
-- Table structure for tbl_metric_job_registry
-- ----------------------------

CREATE TABLE `tbl_metric_job_registry`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `registry_group` varchar(50)  NOT NULL DEFAULT '' COMMENT '注册组',
    `registry_key`   varchar(255) NOT NULL DEFAULT '' COMMENT '组册key',
    `registry_value` varchar(255) NOT NULL DEFAULT '' COMMENT '注册值',
    `update_time`    datetime              DEFAULT NULL COMMENT '逻辑主键',
    PRIMARY KEY (`id`),
    KEY              `idx_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '作业注册表';

-- ----------------------------
-- Table structure for tbl_metric_job_group
-- ----------------------------

CREATE TABLE `tbl_metric_job_group`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `app_name`     varchar(64) NOT NULL DEFAULT '' COMMENT '执行器AppName',
    `title`        varchar(12) NOT NULL DEFAULT '' COMMENT '执行器名称',
    `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
    `address_list` text COMMENT '执行器地址列表，多地址逗号分隔',
    `update_time`  datetime             DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT '作业组数据表';

-- ----------------------------
-- Table structure for tbl_metric_job_proxy_users
-- ----------------------------

CREATE TABLE `tbl_metric_job_proxy_users`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `user_id`    int(11) NOT NULL DEFAULT 0 COMMENT '关联用户Id',
    `role`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户、1-管理员',
    `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '作业代理用户表';

-- ----------------------------
-- Table structure for tbl_metric_job_lock
-- ----------------------------

CREATE TABLE `tbl_metric_job_lock`
(
    `lock_name` varchar(50) NOT NULL DEFAULT '' COMMENT '锁名称',
    PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT '作业锁止表';
