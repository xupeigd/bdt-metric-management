SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_datasource_cluster_infos
-- ----------------------------

CREATE TABLE `t_datasource_cluster_infos`
(
    `id`               bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `address`          varchar(64)  NOT NULL DEFAULT '' COMMENT '连接地址 host:port格式',
    `comment`          varchar(255) NOT NULL DEFAULT '' COMMENT '数据源名称',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `default_database` varchar(64)  NOT NULL DEFAULT '' COMMENT '默认数据库',
    `default_schema`   varchar(64)  NOT NULL DEFAULT '' COMMENT '默认schema',
    `name`             varchar(64)  NOT NULL DEFAULT '' COMMENT '数据源名称',
    `password`         varchar(64)  NOT NULL DEFAULT '' COMMENT '密码',
    `status`           tinyint      NOT NULL DEFAULT '0' COMMENT '数据状态 0 删除 1 可用',
    `type`             varchar(16)  NOT NULL DEFAULT '' COMMENT '类型,eg: doris',
    `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `user_name`        varchar(64)  NOT NULL DEFAULT '' COMMENT '用户名',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '数据集群信息表';

-- ----------------------------
-- Table structure for t_datasource_dataset_columns
-- ----------------------------

CREATE TABLE `t_datasource_dataset_columns`
(
    `dataset_id`     bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `column_type`    tinyint      NOT NULL DEFAULT '1' COMMENT '字段类型 0 normal 1 pk 2 fk',
    `comment`        varchar(255) NOT NULL DEFAULT '' COMMENT '注释/描述',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `name`           varchar(64)  NOT NULL DEFAULT '' COMMENT '字段名称',
    `serial`         int          NOT NULL DEFAULT '0' COMMENT '序列号，表内顺序',
    `status`         tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `table_name`     varchar(64)  NOT NULL DEFAULT '' COMMENT '表名称',
    `type`           varchar(16)  NOT NULL DEFAULT '' COMMENT '类型：db/dwh 定义',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT '数据集列信息表';

-- ----------------------------
-- Table structure for t_datasource_dataset_identifiers
-- ----------------------------

CREATE TABLE `t_datasource_dataset_identifiers`
(
    `dataset_id`     bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `expr`           varchar(128) NOT NULL DEFAULT '' COMMENT '表达式',
    `identifiers`    varchar(255) NOT NULL DEFAULT '' COMMENT 'Composite keys Identifier ',
    `name`           varchar(64)  NOT NULL DEFAULT '' COMMENT '名称',
    `status`         tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `type`           tinyint      NOT NULL DEFAULT '1' COMMENT '类型 0 primary 1 foreign 2 unique',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '数据集识别列信息表';

-- ----------------------------
-- Table structure for t_datasource_datasets
-- ----------------------------

CREATE TABLE `t_datasource_datasets`
(
    `id`             bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `description`    varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
    `along_column`   varchar(32)  NOT NULL DEFAULT '' COMMENT '变化周期corn',
    `mutability`     tinyint      NOT NULL DEFAULT '1' COMMENT '数据可变性 0亘古不变 1全量变化 2仅追加',
    `update_corn`    varchar(32)  NOT NULL DEFAULT '' COMMENT '变化周期corn',
    `name`           varchar(64)  NOT NULL DEFAULT '' COMMENT '名称',
    `status`         tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `table_name`     varchar(128) NOT NULL DEFAULT '' COMMENT '表名称',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `cluster_id`     bigint       NOT NULL DEFAULT '0' COMMENT '所属数据几圈',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '数据集信息实体表';

-- ----------------------------
-- Table structure for t_draft_drafts
-- ----------------------------

CREATE TABLE `t_draft_drafts`
(
    `id`          bigint      NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `content`     text COMMENT '草稿json',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `flag`        varchar(32) NOT NULL DEFAULT '' COMMENT '识别flag',
    `status`      tinyint     NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `type`        tinyint     NOT NULL DEFAULT '1' COMMENT '草稿类型 0 数据集 1 指标',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `user_id`     bigint      NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_flag_user_id` (`flag`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '草稿实体表';

-- ----------------------------
-- Table structure for t_identify_operation_logs
-- ----------------------------

CREATE TABLE `t_identify_operation_logs`
(
    `id`             bigint      NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `address`        varchar(64) NOT NULL DEFAULT '' COMMENT '所属地址',
    `detail`         text COMMENT '日志详情',
    `ip`             varchar(32) NOT NULL DEFAULT '' COMMENT '操作IP',
    `operation_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `type`           smallint    NOT NULL DEFAULT 0 COMMENT '日志分类 0默认 1登陆 2登出',
    `user_id`        bigint      NOT NULL DEFAULT '0' COMMENT '操作用户',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '操作日志实体表';

-- ----------------------------
-- Table structure for t_identify_permissions
-- ----------------------------

CREATE TABLE `t_identify_permissions`
(
    `id`          bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `code`        varchar(64)  NOT NULL DEFAULT '' COMMENT '权限code',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `module`      varchar(32)  NOT NULL DEFAULT '0' COMMENT '所属模块',
    `name`        varchar(64)  NOT NULL DEFAULT '' COMMENT '权限名称 ',
    `parent_id`   bigint       NOT NULL DEFAULT '0' COMMENT '父权限Id',
    `path`        varchar(255) NOT NULL DEFAULT '' COMMENT '前端页面path',
    `status`      tinyint      NOT NULL DEFAULT '0' COMMENT '数据状态 0 删除 1 可用',
    `type`        tinyint      NOT NULL DEFAULT '0' COMMENT '权限类型 0 非法数据 1 MENU 2 OPERATION ',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`),
    UNIQUE KEY `uniq_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '权限码实体表';

-- ----------------------------
-- Table structure for t_identify_rel_role_permission
-- ----------------------------

CREATE TABLE `t_identify_rel_role_permission`
(
    `id`             bigint   NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`         tinyint  NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `permission_id`  bigint   NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `role_id`        bigint   NOT NULL DEFAULT '0' COMMENT '数据集Id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '角色权限关系表';

-- ----------------------------
-- Table structure for t_identify_rel_user_permission
-- ----------------------------

CREATE TABLE `t_identify_rel_user_permission`
(
    `id`             bigint   NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`         tinyint  NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `type`           tinyint  NOT NULL DEFAULT '0' COMMENT '权限的状态 0撤销 1授予',
    `update_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `permission_id`  bigint   NOT NULL DEFAULT '0' COMMENT '权限Id',
    `user_id`        bigint   NOT NULL DEFAULT '0' COMMENT '用户Id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '用户直接权限关系表';

-- ----------------------------
-- Table structure for t_identify_rel_user_role
-- ----------------------------

CREATE TABLE `t_identify_rel_user_role`
(
    `id`             bigint   NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`         tinyint  NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1可用',
    `update_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `role_id`        bigint   NOT NULL DEFAULT '0' COMMENT '角色Id',
    `user_id`        bigint   NOT NULL DEFAULT '0' COMMENT '用户Id',
    PRIMARY KEY (`id`),
    KEY              `idx_user_id_role_id` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT '用户角色关系表';

-- ----------------------------
-- Table structure for t_identify_roles
-- ----------------------------

CREATE TABLE `t_identify_roles`
(
    `id`             bigint      NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `code`           varchar(64) NOT NULL DEFAULT '' COMMENT '角色code',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `name`           varchar(64) NOT NULL DEFAULT '' COMMENT '角色名称',
    `status`         tinyint     NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `type`           tinyint     NOT NULL DEFAULT '1' COMMENT '角色类型 0 公共 1 私有(个人/组)',
    `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`),
    UNIQUE KEY `uniq_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT '角色实体表';

-- ----------------------------
-- Table structure for t_identify_users
-- ----------------------------

CREATE TABLE `t_identify_users`
(
    `id`             bigint      NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `email`          varchar(64) NOT NULL DEFAULT '' COMMENT 'Email',
    `mobile`         varchar(16) NOT NULL DEFAULT '' COMMENT 'MobilePhone',
    `name`           varchar(32) NOT NULL DEFAULT '' COMMENT '用户名',
    `password`       varchar(64) NOT NULL DEFAULT '' COMMENT '密码密文串',
    `status`         tinyint     NOT NULL DEFAULT '0' COMMENT '状态 0删除 1非活跃（锁定/冻结）2活跃',
    `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '用户实体表';

-- ----------------------------
-- Table structure for t_metric_catalogs
-- ----------------------------

CREATE TABLE `t_metric_catalogs`
(
    `id`             bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `code`           varchar(32) NOT NULL DEFAULT '' COMMENT '编码 3位/级别 eg：001001001',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `name`           varchar(64) NOT NULL DEFAULT '' COMMENT '名称',
    `status`         tinyint     NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `type`           tinyint     NOT NULL DEFAULT '1' COMMENT '类型 0 主题域 1 业务域',
    `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `parent_id`      bigint      NOT NULL DEFAULT '0' COMMENT '父节点主键',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_code` (`code`),
    UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '分类实体表';


-- ----------------------------
-- Table structure for t_metric_dimensions
-- ----------------------------

CREATE TABLE `t_metric_dimensions`
(
    `metric_id`        bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id`   bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `data_type`        varchar(32)  NOT NULL DEFAULT '' COMMENT '字段的真实类型，与真实的数据库/数仓 一致',
    `description`      varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
    `expr`             varchar(256) NOT NULL DEFAULT '' COMMENT '计算表达式',
    `is_primary`       tinyint      NOT NULL DEFAULT '0' COMMENT '是否为主时间维度',
    `name`             varchar(48)  NOT NULL DEFAULT '' COMMENT '维度名称',
    `status`           tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `time_granularity` varchar(12)  NOT NULL DEFAULT 'day' COMMENT '更新时间',
    `type`             varchar(24)  NOT NULL DEFAULT 'categorical' COMMENT '维度类型',
    `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id`   bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `table_name`       varchar(256) NOT NULL DEFAULT '' COMMENT '维度字段所在表名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '指标纬度数据表';

-- ----------------------------
-- Table structure for t_metric_measures
-- ----------------------------

CREATE TABLE `t_metric_measures`
(
    `metric_id`        bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `agg`              varchar(32)  NOT NULL DEFAULT 'SUM' COMMENT '聚合方法',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id`   bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `data_type`        varchar(32)  NOT NULL DEFAULT '' COMMENT '字段的真实类型，与真实的数据库/数仓 一致',
    `description`      varchar(256) NOT NULL DEFAULT '' COMMENT '描述',
    `expr`             varchar(256) NOT NULL DEFAULT '' COMMENT '计算表达式',
    `name`             varchar(48)  NOT NULL DEFAULT '' COMMENT '维度名称',
    `processing_logic` varchar(512) NOT NULL DEFAULT '' COMMENT '加工逻辑',
    `status`           tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id`   bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `table_name`       varchar(256) NOT NULL DEFAULT '' COMMENT '度量字段所在表名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '指标度量数据表';


-- ----------------------------
-- Table structure for t_metric_metrics
-- ----------------------------

CREATE TABLE `t_metric_metrics`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `cn_alias`            varchar(256) NOT NULL DEFAULT '' COMMENT '中文别名',
    `cn_name`             varchar(256) NOT NULL DEFAULT '' COMMENT '中文名称',
    `create_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `data_security_level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '安全等级 0 S0 1 S1 2 S2',
    `data_type`           varchar(16)  NOT NULL DEFAULT '' COMMENT '数据类型',
    `en_alias`            varchar(256) NOT NULL DEFAULT '' COMMENT '英文别名',
    `en_name`             varchar(256) NOT NULL DEFAULT '' COMMENT '英文名称',
    `metric_level`        tinyint(4) NOT NULL DEFAULT '0' COMMENT '指标等级 0 T0 1 T1',
    `process_logic`       text COMMENT '加工逻辑',
    `pub_sub`             tinyint(4) NOT NULL DEFAULT '0' COMMENT '发布状态 0 下线 1 上线',
    `serial_number`       varchar(32)  NOT NULL DEFAULT '' COMMENT '指标编号',
    `statistic_periods`   varchar(64)  NOT NULL DEFAULT '' COMMENT '统计周期',
    `status`              tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `business_id`         bigint(20) NOT NULL DEFAULT 0 COMMENT '业务域id',
    `business_owner`      bigint(20) NOT NULL DEFAULT 0 COMMENT '业务负责人',
    `dataset_id`          bigint(20) NOT NULL DEFAULT 0 COMMENT '数据集id',
    `tech_owner`          bigint(20) NOT NULL DEFAULT 0 COMMENT '技术负责人',
    `topic_id`            bigint(20) NOT NULL DEFAULT 0 COMMENT '主题域id',
    `measure`             varchar(64)  NOT NULL DEFAULT '' COMMENT '对应度量名称',
    `description`         text COMMENT '指标说明',
    `cluster_type`        tinyint(4) NOT NULL DEFAULT '0' COMMENT '指标等级 0 离线， 1 实时',
    `metric_expr`         varchar(256) NOT NULL DEFAULT '' COMMENT '指标表达式',
    `metric_type`         varchar(64)  NOT NULL DEFAULT '' COMMENT '指标metricFLow类型',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_serial_number` (`serial_number`),
    KEY                   `idx_business_id` (`business_id`),
    KEY                   `idx_dataset_id` (`dataset_id`),
    KEY                   `idx_topic_id` (`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '指标实体表';

-- ----------------------------
-- Table structure for t_metrics_ids
-- ----------------------------

CREATE TABLE `t_metrics_ids`
(
    `sequence_name` varchar(255) NOT NULL DEFAULT '' COMMENT '序列名称',
    `next_val`      bigint       NOT NULL DEFAULT 1000 COMMENT '下一值',
    PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT 'id序列记录表';

-- ----------------------------
-- Table structure for t_yaml_segments
-- ----------------------------

CREATE TABLE `t_yaml_segments`
(
    `id`             bigint      NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `content`        text COMMENT '片段内容',
    `content_md5`    varchar(32) NOT NULL DEFAULT '' COMMENT '片段Md5',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`         tinyint     NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `type`           tinyint     NOT NULL DEFAULT '1' COMMENT '片段类型 0 dataset 1 metric',
    `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `version`        int         NOT NULL DEFAULT '1' COMMENT '版本',
    `dataset_id`     bigint      NOT NULL DEFAULT 0 COMMENT '关联数据集id',
    `metric_id`      bigint      NOT NULL DEFAULT 0 COMMENT '关联指标Id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT 'yaml片段实体表';

-- ----------------------------
-- Table structure for t_datasource_dataset_owners
-- ----------------------------

CREATE TABLE `t_datasource_dataset_owners`
(
    `dataset_id` bigint NOT NULL DEFAULT '0' COMMENT '数据集id',
    `user_id`    bigint NOT NULL DEFAULT '0' COMMENT '用户id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT 'owner数据表';

-- ----------------------------
-- Table structure for t_metric_metrics_business_owners
-- ----------------------------

CREATE TABLE `t_metric_metrics_business_owners`
(
    `metric_id` bigint NOT NULL DEFAULT '0' COMMENT '指标主键',
    `user_id`   bigint NOT NULL DEFAULT '0' COMMENT '用户主键'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '指标业务负责人关系表';

-- ----------------------------
-- Table structure for t_metric_metrics_tech_owners
-- ----------------------------

CREATE TABLE `t_metric_metrics_tech_owners`
(
    `metric_id` bigint NOT NULL DEFAULT '0' COMMENT '指标主键',
    `user_id`   bigint NOT NULL DEFAULT '0' COMMENT '用户主键'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '指标技术负责人关系表';

-- ----------------------------
-- after mvp
-- ----------------------------

-- ----------------------------
-- alter Table for t_metric_metrics
-- ----------------------------

ALTER TABLE `t_metric_metrics` MODIFY COLUMN `en_alias` varchar (256) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '英文别名';
ALTER TABLE `t_metric_metrics` MODIFY COLUMN `en_name` varchar (256) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '英文名称';
ALTER TABLE `t_metric_metrics` MODIFY COLUMN `cn_alias` varchar (512) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '中文别名';
ALTER TABLE `t_metric_metrics` MODIFY COLUMN `cn_name` varchar (512) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '中文名称';
ALTER TABLE `t_metric_metrics` MODIFY COLUMN `serial_number` varchar (32) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '指标编号';
ALTER TABLE `t_metric_metrics` DROP COLUMN `tech_owner`;
ALTER TABLE `t_metric_metrics` DROP COLUMN `business_owner`;

-- ----------------------------
-- alter Table for t_datasource_datasets
-- ----------------------------

ALTER TABLE `t_datasource_datasets` MODIFY COLUMN `name` varchar (256) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '名称';
ALTER TABLE `t_datasource_datasets` MODIFY COLUMN `table_name` varchar (256) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '表名称';

-- ----------------------------
-- alter Table for t_identify_rel_user_role
-- ----------------------------

ALTER TABLE `t_identify_rel_user_role`
    modify column `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键';
ALTER TABLE `t_identify_rel_user_role`
    add column `grant_type` tinyint(2) NOT NULL DEFAULT 0 COMMENT 'grant类型 0固定 1临时';
ALTER TABLE `t_identify_rel_user_role`
    add column `grant_start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效时间-start';
ALTER TABLE `t_identify_rel_user_role`
    add column `grant_end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效时间-end';

-- ----------------------------
-- alter Table for t_identify_rel_role_permission
-- ----------------------------

ALTER TABLE `t_identify_rel_role_permission`
    modify column `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键';
ALTER TABLE `t_identify_rel_role_permission`
    add column `type` tinyint(2) NOT NULL DEFAULT 1 COMMENT '类型 0回收 1授予';
ALTER TABLE `t_identify_rel_role_permission`
    add column `permission_grant_type` tinyint(2) NOT NULL DEFAULT 0 COMMENT 'grant类型 0固定 1临时';
ALTER TABLE `t_identify_rel_role_permission`
    add column `grant_start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效时间-start';
ALTER TABLE `t_identify_rel_role_permission`
    add column `grant_end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效时间-end';

-- ----------------------------
-- alter Table for t_identify_rel_user_permission
-- ----------------------------

ALTER TABLE `t_identify_rel_user_permission` rename `del_t_identify_rel_user_permission`;

-- ----------------------------
-- alter Table for t_metric_dimensions
-- ----------------------------

ALTER TABLE `t_metric_dimensions` MODIFY COLUMN `name` varchar (256) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '维度名称';

-- ----------------------------
-- alter Table for t_metric_measures
-- ----------------------------

ALTER TABLE `t_metric_measures` MODIFY COLUMN `name` varchar (256) CHARACTER SET utf8mb4 DEFAULT '' NOT NULL COMMENT '度量名称';

-- ----------------------------
-- Table structure for t_apis_apps
-- ----------------------------

CREATE TABLE `t_apis_apps`
(
    `id`             bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `name`           VARCHAR(255) NOT NULL DEFAULT '' COMMENT '应用名称',
    `type`           tinyint(2) NOT NULL DEFAULT 1 COMMENT 'App类型 0内部应用 1外部应用',
    `description`    TEXT COMMENT '应用描述',
    `owner_id`       bigint(11) NOT NULL DEFAULT 0 COMMENT '用户Id',
    `token_id`       bigint(11) NOT NULL DEFAULT 0 COMMENT 'tokenId',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`         tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '应用数据表';

-- ----------------------------
-- Table structure for t_apis_rel_app_metric
-- ----------------------------

CREATE TABLE `t_apis_rel_app_metric`
(
    `id`               bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `apply_record_id`  bigint(11) NOT NULL DEFAULT 0 COMMENT '申请记录Id',
    `app_id`           bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id',
    `metric_id`        bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id',
    `quota_id`         bigint(11) NOT NULL DEFAULT 0 COMMENT '配额Id',
    `grant_type`       tinyint(2) NOT NULL DEFAULT 0 COMMENT 'grant类型 0固定 1临时',
    `grant_start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效时间-start',
    `grant_end_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效时间-end',
    `create_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id`   bigint   NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`           tinyint  NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id`   bigint   NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`),
    KEY                `idx_app_metric_quota`(`app_id`,`metric_id`,`quota_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT 'APP与指标关联表';

-- ----------------------------
-- Table structure for t_apis_quotas
-- ----------------------------

CREATE TABLE `t_apis_quotas`
(
    `id`               bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `app_id`           bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id',
    `metric_id`        bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id',
    `mode`             tinyint(2) NOT NULL DEFAULT 0 COMMENT '刷新模式 0一次性 1周期性',
    `quota`            bigint(20) NOT NULL DEFAULT 0 COMMENT '额度值',
    `apply_record_id`  bigint(11) NOT NULL DEFAULT 0 COMMENT '申请记录Id',
    `cron_express`     varchar(255) NOT NULL DEFAULT '' COMMENT '刷新cron表达式',
    `grant_type`       tinyint(2) NOT NULL DEFAULT 0 COMMENT 'grant类型 0固定 1临时',
    `grant_start_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效时间-start',
    `grant_end_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效时间-end',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id`   bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`           tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id`   bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '额度数据表';

-- ----------------------------
-- Table structure for t_apis_auth_token
-- ----------------------------

CREATE TABLE `t_apis_auth_token`
(
    `id`             bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `app_id`         bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id',
    `token`          varchar(255) NOT NULL DEFAULT '' COMMENT '访问token',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`         tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT 'token数据表';

-- ----------------------------
-- Table structure for t_apis_invoke_apply_records
-- ----------------------------

CREATE TABLE `t_apis_invoke_apply_records`
(
    `id`               bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `app_id`           bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id',
    `description`      text COMMENT '申请原因',
    `approved_state`   tinyint(2) NOT NULL DEFAULT 0 COMMENT '审批状态 0未审核 1批准 2拒绝',
    `approved_comment` text COMMENT '审批意见',
    `approved_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '审核用户Id',
    `approved_time`    datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    `approved_type`    tinyint  NOT NULL DEFAULT '0' COMMENT '申请类型 0指标申请调用 1其他',
    `create_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id`   bigint   NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`           tinyint  NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`      datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id`   bigint   NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '调用申请数据表';

-- ----------------------------
-- Table structure for t_apis_rel_invoke_apply_records_metric
-- ----------------------------

CREATE TABLE `t_apis_rel_invoke_apply_records_metric`
(
    `id`             bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `apply_id`       bigint(11) NOT NULL DEFAULT 0 COMMENT '申请记录id',
    `app_id`         bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id',
    `metric_id`      bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id',
    `create_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `status`         tinyint  NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `update_time`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint   NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '调用申请与指标关联表';

-- ----------------------------
-- v 1.1.1
-- ----------------------------

-- ----------------------------
-- Table structure for t_apis_quota_costs
-- ----------------------------

CREATE TABLE `t_apis_quota_costs`
(
    `id`             bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `app_id`         bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id',
    `metric_id`      bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id',
    `lock_down_flag` varchar(64) NOT NULL DEFAULT '' COMMENT 'lockdown标识 （正在使用的为空串，失效的为yyyy-MM-dd HH:mm:ss）',
    `quota`          bigint(20) NOT NULL DEFAULT 0 COMMENT '额度值',
    `cost`           bigint(20) NOT NULL DEFAULT 0 COMMENT '额度消耗值',
    `status`         tinyint     NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint      NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    PRIMARY KEY (`id`),
    KEY              `idx_app_metric_lockdown`(`app_id`,`metric_id`,`lock_down_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '指标配额消耗数据表';

-- ----------------------------
-- Table structure for t_metric_metrics
-- ----------------------------
ALTER TABLE `t_metric_metrics`
    ADD COLUMN `default_quota` bigint(20) NOT NULL DEFAULT 1000 COMMENT '指标的默认配额';
ALTER TABLE `t_metric_metrics`
    ADD COLUMN `default_quota_period` tinyint(2) NOT NULL DEFAULT 1 COMMENT '默认配额刷新周期 1月 2周 3日 4小时 默认 月';
ALTER TABLE `t_metric_metrics`
    ADD COLUMN `default_cronn_express` varchar(24) NOT NULL DEFAULT '0 0 0 1 */1 * ' COMMENT '默认配额刷新cron';

-- ----------------------------
-- Table struct for t_apis_metric_invoke_statisics
-- ----------------------------

CREATE TABLE `t_apis_metric_invoke_statisics`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键',
    `app_id`      bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id',
    `metric_id`   bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id',
    `date_flag`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '标识时间',
    `log_date`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间点日期',
    `quota`       bigint(20) NOT NULL DEFAULT 0 COMMENT '额度值',
    `cur_cost`    bigint(20) NOT NULL DEFAULT 0 COMMENT '当前配额消耗值（累积）',
    `day_cost`    bigint(20) NOT NULL DEFAULT 0 COMMENT '日计消耗值',
    `max_qps`     bigint(11) NOT NULL DEFAULT 0 COMMENT '最高QPS(天计) ',
    `status`      tinyint  NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_metric_app_date_flag`(`metric_id`,`app_id`,`date_flag`,`log_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '指标调用统计数据表';

SET
FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for t_metric_catalogs 为指标目录添加业务编码字段
-- ----------------------------
ALTER TABLE `t_metric_catalogs`
    add column `business_code` varchar(32) NOT NULL DEFAULT '' COMMENT '业务编码，用户生成指标唯一编码';


-- ----------------------------
-- V1.1.2
-- ----------------------------

-- ----------------------------
-- db_metric_management.t_metric_metrics 添加指标聚合类型、是否可累积、是否被认证3个属性
-- ----------------------------
ALTER TABLE `t_metric_metrics`
    ADD metric_aggregation_type tinyint DEFAULT 0 NOT NULL COMMENT '指标聚合类型(0:原子指标，1:衍生指标，2:复合指标)';
ALTER TABLE `t_metric_metrics`
    ADD metric_accumulative tinyint DEFAULT 0 NOT NULL COMMENT '指标是否可累积(0:不可，1:可累加)';
ALTER TABLE `t_metric_metrics`
    ADD metric_authentication tinyint DEFAULT 0 NOT NULL COMMENT '指标是否被认证(0:非，1:是)';

-- ----------------------------
-- db_metric_management.t_apis_apps 添加应用类型属性
-- ----------------------------
ALTER TABLE `t_apis_apps`
    ADD app_type tinyint DEFAULT 0 NOT NULL COMMENT '应用类型(0:数据产品，1:业务产品)';

-- ----------------------------
-- db_metric_management.t_metric_user_lables definition
-- ----------------------------

CREATE TABLE `tbl_metric_user_labels`
(
    `id`          bigint                                                NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '标签名称',
    `user_id`     bigint                                                NOT NULL DEFAULT '0' COMMENT '用户id',
    `status`      tinyint                                                        DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `create_time` datetime                                              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_t_metric_user_labels_user_id` (`user_id`,`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='指标-用户-标签表';

-- ----------------------------
-- db_metric_management.t_metric_rel_label_metric definition
-- ----------------------------
CREATE TABLE `tbl_metric_rel_label_metric`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `label_id`    bigint   NOT NULL DEFAULT '0' COMMENT '标签id',
    `metric_id`   bigint   NOT NULL DEFAULT '0' COMMENT '指标id',
    `status`      tinyint  NOT NULL DEFAULT '1' COMMENT '生效状态(0:已删除，1:未删除)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY           `idx_t_metric_rel_label_metric_metric_id` (`status`,`metric_id`) USING BTREE,
    KEY           `idx_t_metric_rel_label_metric_label` (`status`,`label_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='指标标签与指标映射表';

-- ----------------------------
-- db_metric_management.t_apis_invoke_apply_records 添加三个字段
-- ----------------------------
ALTER TABLE t_apis_invoke_apply_records
    ADD qpd int DEFAULT 0 NOT NULL COMMENT '每日请求次数';
ALTER TABLE t_apis_invoke_apply_records
    ADD qps int DEFAULT 0 NOT NULL COMMENT '峰值期间的每秒查询次数';
ALTER TABLE t_apis_invoke_apply_records
    ADD tp99 int DEFAULT 0 NOT NULL COMMENT '查询请求的完成时间，前端可以选不同单位填写，后端统一存储单位ms';
