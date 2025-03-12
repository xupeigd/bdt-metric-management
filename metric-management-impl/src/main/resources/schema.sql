-- ----------------------------
-- Table structure for t_datasource_cluster_infos
-- ----------------------------
DROP TABLE IF EXISTS `t_datasource_cluster_infos`;
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
DROP TABLE IF EXISTS `t_datasource_dataset_columns`;
CREATE TABLE `t_datasource_dataset_columns`
(
    `dataset_id`     bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `column_type`    tinyint      NOT NULL DEFAULT '1' COMMENT '字段类型 0 normal 1 pk 2 fk',
    `comment`        varchar(255) NOT NULL DEFAULT '' COMMENT '注释/描述',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `name`           varchar(256) NOT NULL DEFAULT '' COMMENT '字段名称',
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
DROP TABLE IF EXISTS `t_datasource_dataset_identifiers`;
CREATE TABLE `t_datasource_dataset_identifiers`
(
    `dataset_id`     bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `expr`           varchar(128) NOT NULL DEFAULT '' COMMENT '表达式',
    `identifiers`    varchar(255) NOT NULL DEFAULT '' COMMENT 'Composite keys Identifier ',
    `name`           varchar(256) NOT NULL DEFAULT '' COMMENT '名称',
    `status`         tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `type`           tinyint      NOT NULL DEFAULT '1' COMMENT '类型 0 primary 1 foreign 2 unique',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '数据集识别列信息表';

-- ----------------------------
-- Table structure for t_datasource_datasets
-- ----------------------------
DROP TABLE IF EXISTS `t_datasource_datasets`;
CREATE TABLE `t_datasource_datasets`
(
    `id`             bigint       NOT NULL DEFAULT '0' COMMENT '逻辑主键',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '创建用户Id',
    `description`    varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
    `along_column`   varchar(32)  NOT NULL DEFAULT '' COMMENT '变化周期corn',
    `mutability`     tinyint      NOT NULL DEFAULT '1' COMMENT '数据可变性 0亘古不变 1全量变化 2仅追加',
    `update_corn`    varchar(32)  NOT NULL DEFAULT '' COMMENT '变化周期corn',
    `name`           varchar(256) NOT NULL DEFAULT '' COMMENT '名称',
    `status`         tinyint      NOT NULL DEFAULT '1' COMMENT '数据状态 0删除 1 可用',
    `table_name`     varchar(256) NOT NULL DEFAULT '' COMMENT '表名称',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_user_id` bigint       NOT NULL DEFAULT '0' COMMENT '更新用户Id',
    `cluster_id`     bigint       NOT NULL DEFAULT '0' COMMENT '所属数据几圈',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '数据集信息实体表';

-- ----------------------------
-- Table structure for t_draft_drafts
-- ----------------------------
DROP TABLE IF EXISTS `t_draft_drafts`;
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
DROP TABLE IF EXISTS `t_identify_operation_logs`;
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
DROP TABLE IF EXISTS `t_identify_permissions`;
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
DROP TABLE IF EXISTS `t_identify_rel_role_permission`;
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
DROP TABLE IF EXISTS `t_identify_rel_user_permission`;
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
DROP TABLE IF EXISTS `t_identify_rel_user_role`;
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
DROP TABLE IF EXISTS `t_identify_roles`;
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
DROP TABLE IF EXISTS `t_identify_users`;
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
DROP TABLE IF EXISTS `t_metric_catalogs`;
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
DROP TABLE IF EXISTS `t_metric_dimensions`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '指标纬度数据表';

-- ----------------------------
-- Table structure for t_metric_measures
-- ----------------------------
DROP TABLE IF EXISTS `t_metric_measures`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '指标度量数据表';


-- ----------------------------
-- Table structure for t_metric_metrics
-- ----------------------------
DROP TABLE IF EXISTS `t_metric_metrics`;
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
DROP TABLE IF EXISTS `t_metrics_ids`;
CREATE TABLE `t_metrics_ids`
(
    `sequence_name` varchar(255) NOT NULL DEFAULT '' COMMENT '序列名称',
    `next_val`      bigint       NOT NULL DEFAULT 1000 COMMENT '下一值',
    PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT 'id序列记录表';

-- ----------------------------
-- Table structure for t_yaml_segments
-- ----------------------------
DROP TABLE IF EXISTS `t_yaml_segments`;
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
DROP TABLE IF EXISTS `t_datasource_dataset_owners`;
CREATE TABLE `t_datasource_dataset_owners`
(
    `dataset_id` bigint NOT NULL DEFAULT '0' COMMENT '数据集id',
    `user_id`    bigint NOT NULL DEFAULT '0' COMMENT '用户id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT 'owner数据表';

-- ----------------------------
-- Table structure for t_metric_metrics_business_owners
-- ----------------------------
DROP TABLE IF EXISTS `t_metric_metrics_business_owners`;
CREATE TABLE `t_metric_metrics_business_owners`
(
    `metric_id` bigint NOT NULL DEFAULT '0' COMMENT '指标主键',
    `user_id`   bigint NOT NULL DEFAULT '0' COMMENT '用户主键'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '指标业务负责人关系表';

-- ----------------------------
-- Table structure for t_metric_metrics_tech_owners
-- ----------------------------
DROP TABLE IF EXISTS `t_metric_metrics_tech_owners`;
CREATE TABLE `t_metric_metrics_tech_owners`
(
    `metric_id` bigint NOT NULL DEFAULT '0' COMMENT '指标主键',
    `user_id`   bigint NOT NULL DEFAULT '0' COMMENT '用户主键'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '指标技术负责人关系表';

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
DROP TABLE IF EXISTS `del_t_identify_rel_user_permission`;
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
DROP TABLE IF EXISTS `t_apis_apps`;
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
DROP TABLE IF EXISTS `t_apis_rel_app_metric`;
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
DROP TABLE IF EXISTS `t_apis_quotas`;
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
DROP TABLE IF EXISTS `t_apis_auth_token`;
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
DROP TABLE IF EXISTS `t_apis_invoke_apply_records`;
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
DROP TABLE IF EXISTS `t_apis_rel_invoke_apply_records_metric`;
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
DROP TABLE IF EXISTS `t_apis_quota_costs`;
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
    ADD COLUMN `default_cronn_express` varchar(24) NOT NULL DEFAULT '0 0 0 1 */1 *' COMMENT '默认配额刷新cron';

-- ----------------------------
-- Table structure for t_apis_metric_invoke_statisics
-- ----------------------------
DROP TABLE IF EXISTS `t_apis_metric_invoke_statisics`;
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


-- ----------------------------
-- since branch_xxl
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_metric_job_info
-- ----------------------------
DROP TABLE IF EXISTS `tbl_metric_job_info`;
CREATE TABLE `tbl_metric_job_info`
(
    `id`                        int(11) NOT NULL AUTO_INCREMENT,
    `job_group`                 int(11) NOT NULL COMMENT '执行器主键ID',
    `job_desc`                  varchar(255) NOT NULL,
    `add_time`                  datetime              DEFAULT NULL,
    `update_time`               datetime              DEFAULT NULL,
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
    `glue_type`                 varchar(50)  NOT NULL COMMENT 'GLUE类型',
    `glue_source`               mediumtext COMMENT 'GLUE源代码',
    `glue_remark`               varchar(128)          DEFAULT NULL COMMENT 'GLUE备注',
    `glue_updatetime`           datetime              DEFAULT NULL COMMENT 'GLUE更新时间',
    `child_jobid`               varchar(255)          DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    `trigger_status`            tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
    `trigger_last_time`         bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
    `trigger_next_time`         bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for tbl_metric_job_log
-- ----------------------------
DROP TABLE IF EXISTS `tbl_metric_job_log`;
CREATE TABLE `tbl_metric_job_log`
(
    `id`                        bigint(20) NOT NULL AUTO_INCREMENT,
    `job_group`                 int(11) NOT NULL COMMENT '执行器主键ID',
    `job_id`                    int(11) NOT NULL COMMENT '任务，主键ID',
    `executor_address`          varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    `executor_handler`          varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
    `executor_sharding_param`   varchar(20)  DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    `trigger_time`              datetime     DEFAULT NULL COMMENT '调度-时间',
    `trigger_code`              int(11) NOT NULL COMMENT '调度-结果',
    `trigger_msg`               text COMMENT '调度-日志',
    `handle_time`               datetime     DEFAULT NULL COMMENT '执行-时间',
    `handle_code`               int(11) NOT NULL COMMENT '执行-状态',
    `handle_msg`                text COMMENT '执行-日志',
    `alarm_status`              tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
    PRIMARY KEY (`id`),
    KEY                         `I_trigger_time` (`trigger_time`),
    KEY                         `I_handle_code` (`handle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for tbl_metric_job_log_report
-- ----------------------------
DROP TABLE IF EXISTS `tbl_metric_job_log_report`;
CREATE TABLE `tbl_metric_job_log_report`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `trigger_day`   datetime DEFAULT NULL COMMENT '调度-时间',
    `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
    `suc_count`     int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
    `fail_count`    int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
    `update_time`   datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for tbl_metric_job_logglue
-- ----------------------------
DROP TABLE IF EXISTS `tbl_metric_job_logglue`;
CREATE TABLE `tbl_metric_job_logglue`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `job_id`      int(11) NOT NULL COMMENT '任务，主键ID',
    `glue_type`   varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
    `glue_source` mediumtext COMMENT 'GLUE源代码',
    `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
    `add_time`    datetime    DEFAULT NULL,
    `update_time` datetime    DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for tbl_metric_job_registry
-- ----------------------------
DROP TABLE IF EXISTS `tbl_metric_job_registry`;
CREATE TABLE `tbl_metric_job_registry`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT,
    `registry_group` varchar(50)  NOT NULL,
    `registry_key`   varchar(255) NOT NULL,
    `registry_value` varchar(255) NOT NULL,
    `update_time`    datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY              `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for tbl_metric_job_group
-- ----------------------------
DROP TABLE IF EXISTS `tbl_metric_job_group`;
CREATE TABLE `tbl_metric_job_group`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `app_name`     varchar(64) NOT NULL COMMENT '执行器AppName',
    `title`        varchar(12) NOT NULL COMMENT '执行器名称',
    `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
    `address_list` text COMMENT '执行器地址列表，多地址逗号分隔',
    `update_time`  datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for tbl_metric_job_proxy_users
-- ----------------------------
DROP TABLE IF EXISTS `tbl_metric_job_proxy_users`;
CREATE TABLE `tbl_metric_job_proxy_users`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `user_id`    int(11) NOT NULL DEFAULT 0 COMMENT '关联用户Id',
    `role`       tinyint(4) NOT NULL COMMENT '角色：0-普通用户、1-管理员',
    `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
    PRIMARY KEY (`id`),
    UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for tbl_metric_job_lock
-- ----------------------------
DROP TABLE IF EXISTS `tbl_metric_job_lock`;
CREATE TABLE `tbl_metric_job_lock`
(
    `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
    PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



