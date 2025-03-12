-- 仅dev使用
-- 插入集群信息
INSERT INTO `t_datasource_cluster_infos` (`id`, `address`, `comment`, `create_time`, `default_database`,
                                          `default_schema`, `name`, `password`, `status`, `type`, `update_time`,
                                          `user_name`)
VALUES (1, 'm3sql:9918', 'mock', '2022-08-02 07:34:04', 'db_metric_management', '', 'mockCluster', 'metric_management',
        1, 'mysql', '2022-08-02 07:34:04', 'metric_management');
INSERT INTO `t_datasource_cluster_infos` (`id`, `address`, `comment`, `create_time`, `default_database`,
                                          `default_schema`, `name`, `password`, `status`, `type`, `update_time`,
                                          `user_name`)
VALUES (2, '192.168.13.84:9918', 'MPB', '2022-08-17 09:21:41', 'postgresdb', '', 'postgresdb', 'postgresdb', 1, 'mysql',
        '2022-08-17 09:21:41', 'postgresdb');
INSERT INTO `t_datasource_cluster_infos` (`id`, `address`, `comment`, `create_time`, `default_database`,
                                          `default_schema`, `name`, `password`, `status`, `type`, `update_time`,
                                          `user_name`)
VALUES (3, '172.16.0.35:9030', 'dohko-sr', '2022-08-25 03:21:49', 'db_yqs_b_common', '', 'dohko-sr', 'root123', 1,
        'mysql', '2022-08-25 03:21:49', 'root');
INSERT INTO `t_datasource_cluster_infos` (`id`, `address`, `comment`, `create_time`, `default_database`,
                                          `default_schema`, `name`, `password`, `status`, `type`, `update_time`,
                                          `user_name`)
VALUES (4, 'dohko.mysql.001.master.quicksand.com:3306', 'test_db_metric_management', '2022-08-25 03:21:49',
        'db_metric_management', '', 'db_metric_management', .quicksand.ev ', 1, ' mysql ', ' 2022 - 08 - 25 03 : 21 :
        49 ', ' root ');
INSERT INTO `t_datasource_cluster_infos` (`id`, `address`, `comment`, `create_time`, `default_database`,
                                          `default_schema`, `name`, `password`, `status`, `type`, `update_time`,
                                          `user_name`)
VALUES (5, ' 172.16.0.35 : 9030 ', ' zbxt ', ' 2022 - 09 - 06 06 : 57 : 57 ', ' zbxt ', '', ' zbxt - sr ', ' root123
        ', 1, ' starrocks ',
        ' 2022 - 09 - 06 06 : 57 : 57 ', ' root ');
-- 插入catalog信息
INSERT INTO `t_metric_catalogs` (`id`, `code`, `create_time`, `create_user_id`, `name`, `status`,
                                 `type`, `update_time`, `update_user_id`, `parent_id`)
VALUES (100, ' 001 ', ' 2022 - 10 - 12 17 : 22 : 49 ', 1, ' 指标 ', 1, 0, ' 2022 - 10 - 12 17 : 22 : 49 ', 1, 1);
INSERT INTO `t_metric_catalogs` (`id`, `code`, `create_time`, `create_user_id`, `name`, `status`,
                                 `type`, `update_time`, `update_user_id`, `parent_id`)
VALUES (101, ' 001001 ', ' 2022 - 10 - 12 17 : 23 : 02 ', 1, ' 指标管理 ', 1, 1, ' 2022 - 10 - 12 17 : 23 : 02 ', 1, 4);
-- 插入dataset信息
INSERT INTO `t_datasource_datasets` (`id`, `create_time`, `create_user_id`, `description`,
                                     `along_column`, `mutability`, `update_corn`, `name`,
                                     `status`, `table_name`, `update_time`, `update_user_id`,
                                     `cluster_id`)
VALUES (1, ' 2022 - 10 - 09 17 : 33 : 08 ', 1, '', '', 0, '', ' 指标用户数据 ', 1, ' t_identify_users ', ' 2022 - 10 -
        09 17 :
        33 : 08 ', 1, 1);
-- 插入dataset 字段信息
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 1, ' 逻辑主键 ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' id ', 1, 1, ' t_identify_users ', ' bigint(20) ', '
        2022 -
        10 - 12 18 : 11 : 13 ', 1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 0, ' 创建时间 ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' create_time ', 2, 1, ' t_identify_users ', ' datetime ',
        ' 2022 - 10 - 12 18 : 11 : 13 ', 1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 0, ' 创建用户Id ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' create_user_id ', 3, 1, ' t_identify_users ', '
        bigint(20) ',
        ' 2022 - 10 - 12 18 : 11 : 13 ', 1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 0, ' Email ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' email ', 4, 1, ' t_identify_users ', ' varchar (64) ',
        ' 2022 - 10 - 12 18 : 11 : 13 ', 1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 0, ' MobilePhone ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' mobile ', 5, 1, ' t_identify_users ', ' varchar (
        16) ',
        ' 2022 - 10 - 12 18 : 11 : 13 ', 1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 3, ' 用户名 ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' name ', 6, 1, ' t_identify_users ', ' varchar (32) ', '
        2022 -
        10 - 12 18 : 11 : 13 ',
        1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 0, ' 密码密文串 ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' password ', 7, 1, ' t_identify_users ', ' varchar (
        64) ',
        ' 2022 - 10 - 12 18 : 11 : 13 ', 1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 0, ' 状态 0删除 1非活跃（锁定 / 冻结）2活跃 ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' status ', 8, 1, '
        t_identify_users ', '
        tinyint(4) ',
        ' 2022 - 10 - 12 18 : 11 : 13 ', 1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 0, ' 更新时间 ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' update_time ', 9, 1, ' t_identify_users ', ' datetime ',
        ' 2022 - 10 - 12 18 : 11 : 13 ', 1);
INSERT INTO `t_datasource_dataset_columns` (`dataset_id`, `column_type`, `comment`, `create_time`, `create_user_id`,
                                            `name`, `serial`, `status`, `table_name`, `type`, `update_time`,
                                            `update_user_id`)
VALUES (1, 0, ' 更新用户Id ', ' 2022 - 10 - 12 18 : 11 : 13 ', 1, ' update_user_id ', 10, 1, ' t_identify_users ', '
        bigint(20) ',
        ' 2022 - 10 - 12 18 : 11 : 13 ', 1);
-- 插入指标示例
INSERT INTO `t_metric_metrics` (`id`, `cn_alias`, `cn_name`, `create_time`, `create_user_id`,
                                `data_security_level`, `data_type`, `en_alias`, `en_name`,
                                `metric_level`, `process_logic`, `pub_sub`, `serial_number`,
                                `statistic_periods`, `status`, `update_time`, `update_user_id`,
                                `business_id`, `dataset_id`,
                                `topic_id`, `measure`, `description`, `cluster_type`,
                                `metric_expr`, `metric_type`)
VALUES (1, '', ' 用户量 ', ' 2022 - 10 - 13 10 : 07 : 43 ', 1, 0, '', '', ' user_count ', 0, ' select count (1) from
        t_identify_users ',
        0, ' UC001 ', ' 年 ', 1, ' 2022 - 10 - 13 10 : 07 : 43 ', 1, 101, 1, 100, '[id] ', ' 用户量 ', 0, '',
        ' measure_proxy ');
INSERT INTO `t_metric_metrics_business_owners` (`metric_id`, `user_id`)
VALUES (1, 1);
INSERT INTO `t_metric_metrics_tech_owners` (`metric_id`, `user_id`)
VALUES (1, 1);
INSERT INTO `t_metric_dimensions` (`metric_id`, `create_time`, `create_user_id`, `data_type`,
                                   `description`, `expr`, `is_primary`, `name`, `status`,
                                   `time_granularity`, `type`, `update_time`, `update_user_id`,
                                   `table_name`)
VALUES (1, ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' varchar (32) ', ' 用户名 ', ' name ', 0, ' name ', 1, '', ' categorical ',
        ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' t_identify_users ');
INSERT INTO `t_metric_dimensions` (`metric_id`, `create_time`, `create_user_id`, `data_type`,
                                   `description`, `expr`, `is_primary`, `name`, `status`,
                                   `time_granularity`, `type`, `update_time`, `update_user_id`,
                                   `table_name`)
VALUES (1, ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' varchar (64) ', ' Email ', ' email ', 0, ' email ', 1, '', '
        categorical ',
        ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' t_identify_users ');
INSERT INTO `t_metric_dimensions` (`metric_id`, `create_time`, `create_user_id`, `data_type`,
                                   `description`, `expr`, `is_primary`, `name`, `status`,
                                   `time_granularity`, `type`, `update_time`, `update_user_id`,
                                   `table_name`)
VALUES (1, ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' varchar (16) ', ' MobilePhone ', ' mobile ', 0, ' mobile ', 1, '', '
        categorical ',
        ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' t_identify_users ');
INSERT INTO `t_metric_dimensions` (`metric_id`, `create_time`, `create_user_id`, `data_type`,
                                   `description`, `expr`, `is_primary`, `name`, `status`,
                                   `time_granularity`, `type`, `update_time`, `update_user_id`,
                                   `table_name`)
VALUES (1, ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' tinyint(4) ', ' 状态 / 0x02 / 0删除 / 0x02 / 1非活跃（锁定 / 冻结）2活跃
        ', ' status
        ', 0, ' status ', 1, '',
        ' categorical ', ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' t_identify_users ');
INSERT INTO `t_metric_measures` (`metric_id`, `agg`, `create_time`, `create_user_id`,
                                 `data_type`, `description`, `expr`, `name`, `processing_logic`,
                                 `status`, `update_time`, `update_user_id`, `table_name`)
VALUES (1, ' 6 ', ' 2022 - 10 - 13 10 : 07 : 43 ', 1, ' bigint(20) ', ' 逻辑主键 ', ' id ', ' id ', '', 1, ' 2022 - 10 -
        13
        10
        : 07 : 43 ', 1,
        ' t_identify_users ');
INSERT INTO `t_yaml_segments` (`id`, `content`, `content_md5`, `create_time`, `create_user_id`,
                               `status`, `type`, `update_time`, `update_user_id`, `version`,
                               `dataset_id`, `metric_id`)
VALUES (1,
        '---\ndata_source:\n  name: 指标系统用户\n  mutability:\n    type: immutable\n  identifiers: []\n  sql_table: db_metric_management.t_identify_users\n---\ndimensions:\n- name: name\n  type: categorical\n  expr: name\n  data_type: varchar(32)\n  description: 用户名\n- name: email\n  type: categorical\n  expr: email\n  data_type: varchar(64)\n  description: Email\n- name: mobile\n  type: categorical\n  expr: mobile\n  data_type: varchar(16)\n  description: MobilePhone\n- name: status\n  type: categorical\n  expr: status\n  data_type: tinyint(4)\n  description: 状态 0删除 1非活跃（锁定/冻结）2活跃\n---\nmeasures:\n- name: id\n  description: 逻辑主键\n  expr: id\n  agg: count_distinct\n  data_type: bigint(20)\n---\nmetric:\n  name: user_count\n  type: measure_proxy\n  type_params:\n    measure: id\n  cn_name: 用户量\n  metric_code: UC001\n  theme: HES\n  business: PGT\n  tech_owners:\n  - admin\n  business_owners:\n  - admin\n  metric_level: T1\n  data_security_level: L1\n  description: 用户量\n  processing_logic: select count(1) from t_identify_users\n  statisticPeriods:\n  - 年\n  cluster_type: 离线\n',
        '162d314efa9961732fda52cfa06a5a8b', '2022-10-13 10:07:43', 1, 1, 1, '2022-10-13 10:07:43', 1, 1, 1, 1);
INSERT INTO `t_yaml_segments` (`id`, `content`, `content_md5`, `create_time`, `create_user_id`,
                               `status`, `type`, `update_time`, `update_user_id`, `version`,
                               `dataset_id`, `metric_id`)
VALUES (2,
        'SELECT \n  name \n  , email \n  , mobile \n  , status \n  , COUNT(DISTINCT id) AS user_count \nFROM t_identify_users on79_src_0 \nGROUP BY \n  name \n  , email \n  , mobile \n  , status \n',
        '6681443eea16637ce65a6fbb887ca377', '2022-10-13 10:07:43', 1, 1, 2, '2022-10-13 10:07:43', 1, 1, 1, 1);
-- 插入应用数据

INSERT INTO `t_apis_apps` (`id`, `name`, `type`, `description`, `owner_id`, `token_id`, `create_time`,
                           `create_user_id`, `status`, `update_time`, `update_user_id`)
VALUES (1, '指标系统4Android', 0, '指标系统Android端应用', 1, 1, '2022-10-13 11:29:19', 1, 1, '2022-10-13 11:31:02', 1);

-- 插入新的权限码
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (6, 'ME_APPROVAL', '2022-11-11 07:05:40', 'approve-management', '审批中心', 0, '/approval', 1, 1,
        '2022-11-11 07:05:44');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (502, 'OP_APP_CREATE', '2022-11-01 09:36:07', 'application-management', '应用创建', 5, '', 1, 2,
        '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (503, 'OP_APP_MODIFY', '2022-11-01 17:36:07', 'application-management', '应用修改', 5, '', 1, 2,
        '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (504, 'OP_APP_MODIFY_TOKEN', '2022-11-01 17:36:07', 'application-management', '应用Token更换', 5, '', 1, 2,
        '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (505, 'OP_APP_DELETE', '2022-11-01 17:36:07', 'application-management', '应用删除', 5, '', 1, 2,
        '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (506, 'OP_APP_LIST', '2022-11-01 17:36:07', 'application-management', '应用列表获取', 5, '', 1, 2,
        '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (507, 'OP_APP_INVOKE_APPROVE', '2022-11-01 17:36:07', 'approve-management', '调用申请审核', 6, '', 1, 2,
        '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (508, 'OP_APP_INVOKE_LIST', '2022-11-01 17:36:07', 'market', '获取接口的调用信息', 2, '', 1, 2,
        '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (509, 'OP_APP_INVOKE_APPLY', '2022-11-01 17:36:07', 'market', '新建调用申请', 2, '', 1, 2,
        '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (510, 'ME_APPROVAL_LIST', '2022-11-11 07:39:56', 'approve-management', '待我审批列表', 6,
        '/approval/pending-list', 1,
        1, '2022-11-11 07:44:04');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (511, 'ME_MY_APPROVAL', '2022-11-11 15:39:56', 'approve-management', '我提交的审批', 6, '/approval/submit-list',
        1, 1,
        '2022-11-11 07:44:04');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (512, 'OP_APPROVAL_LIST', '2022-11-17 15:34:34', 'approve-management', '审批列表', 510, '', 1, 2,
        '2022-11-17 15:34:33');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (513, 'OP_APPROVAL_DETAIL', '2022-11-17 15:34:33', 'approve-management', '审批详情', 510, '', 1, 2,
        '2022-11-17 15:34:33');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (514, 'OP_APPROVAL_MINE', '2022-11-17 15:34:33', 'approve-management', '我提交的审批列表', 511, '', 1, 2,
        '2022-11-17 15:34:33');

--更新旧权限码的路径
UPDATE t_identify_permissions
SET `path`='/market/list'
WHERE id = 2;
UPDATE t_identify_permissions
SET `path`='/app/list'
WHERE id = 5;
--为角色1插入新增权限
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-11 17:45:53', 0, 1, '2022-11-11 17:45:53', 0, 6, 1, 1, 0, '2022-11-11 17:45:53',
        '2022-11-11 17:45:53');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 09:42:09', 0, 502, 1, 1, 0, '2022-11-01 06:30:38',
        '2022-11-01 06:30:38');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 503, 1, 1, 0, '2022-11-01 06:30:38',
        '2022-11-01 06:30:38');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 504, 1, 1, 0, '2022-11-01 06:30:38',
        '2022-11-01 06:30:38');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 505, 1, 1, 0, '2022-11-01 06:30:38',
        '2022-11-01 06:30:38');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 506, 1, 1, 0, '2022-11-01 06:30:38',
        '2022-11-01 06:30:38');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 507, 1, 1, 0, '2022-11-01 06:30:38',
        '2022-11-01 06:30:38');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 508, 1, 1, 0, '2022-11-01 06:30:38',
        '2022-11-01 06:30:38');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 509, 1, 1, 0, '2022-11-01 06:30:38',
        '2022-11-01 06:30:38');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-11 17:45:53', 0, 510, 1, 1, 0, '2022-11-11 17:45:53',
        '2022-11-11 17:45:53');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-11 09:45:54', 0, 511, 1, 1, 0, '2022-11-11 17:45:53',
        '2022-11-11 17:45:53');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 512, 1, 1, 0, '2022-11-17 15:38:01',
        '2022-11-17 15:38:01');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 513, 1, 1, 0, '2022-11-17 15:38:01',
        '2022-11-17 15:38:01');
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 514, 1, 1, 0, '2022-11-17 15:38:01',
        '2022-11-17 15:38:01');


