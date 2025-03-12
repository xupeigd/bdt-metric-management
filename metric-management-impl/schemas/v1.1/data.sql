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
VALUES (508, 'OP_APP_INVOKE_LIST', '2022-11-01 17:36:07', 'market', '获取接口的调用信息', 2, '', 1, 2, '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (509, 'OP_APP_INVOKE_APPLY', '2022-11-01 17:36:07', 'market', '新建调用申请', 2, '', 1, 2, '2022-11-01 17:36:07');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (510, 'ME_APPROVAL_LIST', '2022-11-11 07:39:56', 'approve-management', '待我审批列表', 6, '/approval/pending-list', 1,
        1, '2022-11-11 07:44:04');
INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time)
VALUES (511, 'ME_MY_APPROVAL', '2022-11-11 15:39:56', 'approve-management', '我提交的审批', 6, '/approval/submit-list', 1, 1,
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

-- 更新旧权限码的路径
UPDATE t_identify_permissions
SET `path`='/market/list'
WHERE id = 2;
UPDATE t_identify_permissions
SET `path`='/app/list'
WHERE id = 5;
-- 为角色1插入新增权限
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-11 17:45:53', 0, 1, '2022-11-11 17:45:53', 0, 6, 1, 1, 0, '2022-11-11 17:45:53',
#         '2022-11-11 17:45:53');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 09:42:09', 0, 502, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 503, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 504, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 505, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 506, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 507, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 508, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 509, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-11 17:45:53', 0, 510, 1, 1, 0, '2022-11-11 17:45:53',
#         '2022-11-11 17:45:53');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-01 17:42:08', 0, 1, '2022-11-11 09:45:54', 0, 511, 1, 1, 0, '2022-11-11 17:45:53',
#         '2022-11-11 17:45:53');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 512, 1, 1, 0, '2022-11-17 15:38:01',
#         '2022-11-17 15:38:01');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 513, 1, 1, 0, '2022-11-17 15:38:01',
#         '2022-11-17 15:38:01');
#
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES ('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 514, 1, 1, 0, '2022-11-17 15:38:01',
#         '2022-11-17 15:38:01');

-- add new permission for role
#
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES (46, '2022-11-01 17:42:08', 0, 1, '2022-11-01 09:42:09', 0, 502, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES (47, '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 503, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES (48, '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 504, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES (49, '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 505, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES (50, '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 506, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES (51, '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 507, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES (52, '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 508, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');
#
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
#                                             permission_id, role_id, `type`, permission_grant_type, grant_start_time,
#                                             grant_end_time)
# VALUES (53, '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 509, 1, 1, 0, '2022-11-01 06:30:38',
#         '2022-11-01 06:30:38');

-- 插入初始化的内部应用

INSERT INTO `t_apis_apps` (`name`, `type`, `description`, `owner_id`, `token_id`, `status`, `create_time`,
                           `create_user_id`, `update_time`, `update_user_id`)
VALUES ('指标系统Metric-Platform', 0, 'API-Gateway', 0, 1, 1, '2022-07-30 22:05:00', 0, '2022-07-30 22:05:00', 0);

INSERT INTO `t_apis_apps` (`name`, `type`, `description`, `owner_id`, `token_id`, `status`, `create_time`,
                           `create_user_id`, `update_time`, `update_user_id`)
VALUES ('指标系统Stitcher', 0, 'Stitcher', 0, 2, 1, '2022-07-30 22:05:00', 0, '2022-07-30 22:05:00', 0);

INSERT INTO `t_apis_apps` (`name`, `type`, `description`, `owner_id`, `token_id`, `status`, `create_time`,
                           `create_user_id`, `update_time`, `update_user_id`)
VALUES ('标签系统', 0, '标签系统', 0, 3, 1, '2022-07-30 22:05:00', 0, '2022-07-30 22:05:00', 0);

INSERT INTO `t_apis_auth_token` (`id`, `app_id`, `token`, `create_time`, `create_user_id`,
                                 `status`, `update_time`, `update_user_id`)
VALUES (1, 1, 'dac3835cc17e9fee4594e4960b445a3b', '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0);

INSERT INTO `t_apis_auth_token` (`id`, `app_id`, `token`, `create_time`, `create_user_id`,
                                 `status`, `update_time`, `update_user_id`)
VALUES (2, 2, 'e9da667d8bf72fc340a55b6d85af48eb', '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0);

INSERT INTO `t_apis_auth_token` (`id`, `app_id`, `token`, `create_time`, `create_user_id`,
                                 `status`, `update_time`, `update_user_id`)
VALUES (3, 3, 'daa89bff1bed9a25584be796f525e80a', '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0);


-- role 3 应用开发者，绑定权限码
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 2, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 指标市场
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 3, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 指标管理
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 5, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 应用中心
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 6, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 审批中心
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 300, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 查看指标列表
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 301, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 搜索指标列表
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 306, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 查看单指标的详情
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 307, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 查看单指标的模型
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 308, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 指标试算
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 309, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 指标市场(查询)
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 320, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 查看指标目录列表
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 501, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 查看用户列表
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 502, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 应用创建
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 503, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 应用修改
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 504, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 应用Token更换
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 505, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 应用删除
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 506, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 应用列表获取
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 507, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 调用申请审核
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 508, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 获取接口的调用信息
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 509, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 新建调用申请
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 510, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 待我审批列表
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 511, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 我提交的审批
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 512, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 审批列表
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 513, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 审批详情
INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES ('2022-12-08 10:38:01', 0, 1, '2022-12-08 10:38:01', 0, 514, 3, 1, 0, '2022-12-08 10:38:01',
        '2022-12-08 10:38:01');
-- 我提交的审批列表

-- ----------------------------
-- v 1.1.1 为新增的业务编码字段更新值
-- ----------------------------
update `t_metric_catalogs`
SET business_code='H1002'
WHERE name = '会员营销';-- no.2
update `t_metric_catalogs`
SET business_code='G1003'
WHERE name = '供应链';-- no.3
update `t_metric_catalogs`
SET business_code='Y1001'
WHERE name = '营业';-- no.4
update `t_metric_catalogs`
SET business_code='N1004'
WHERE name = '内部线';-- no.5
update `t_metric_catalogs`
SET business_code='P1006'
WHERE name = '产品';-- no.6
update `t_metric_catalogs`
SET business_code='201'
WHERE name = '会员';-- no.7
update `t_metric_catalogs`
SET business_code='202'
WHERE name = '营销';-- no.8
update `t_metric_catalogs`
SET business_code='301'
WHERE name = '商城线-团膳';-- no.12
update `t_metric_catalogs`
SET business_code='303'
WHERE name = '零售线-智慧零售';-- no.13
update `t_metric_catalogs`
SET business_code='302'
WHERE name = '商城线-怪售商城';-- no.14
update `t_metric_catalogs`
SET business_code='304'
WHERE name = 'SCM';-- no.15
update `t_metric_catalogs`
SET business_code='305'
WHERE name = '5S-WMS';-- no.16
update `t_metric_catalogs`
SET business_code='306'
WHERE name = '5S-MES';-- no.19
update `t_metric_catalogs`
SET business_code='307'
WHERE name = '5S-QMS';-- no.20
update `t_metric_catalogs`
SET business_code='308'
WHERE name = '5S-TMS';-- no.21
update `t_metric_catalogs`
SET business_code='101'
WHERE name = '账单';-- no.22
update `t_metric_catalogs`
SET business_code='103'
WHERE name = '支付';-- no.23
update `t_metric_catalogs`
SET business_code='104'
WHERE name = '结算';-- no.24
update `t_metric_catalogs`
SET business_code='102'
WHERE name = '订单';-- no.25
update `t_metric_catalogs`
SET business_code='401'
WHERE name = '销售合同';-- no.26
update `t_metric_catalogs`
SET business_code='601'
WHERE name = '客户成功';
-- no.27

-- 更新指标开发者角色的权限码（grant 调用申请审批相关权限，回收应用中心菜单权限）
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES (71, '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0, 6, 2, 1, 0, '2022-07-30 22:05:00',
        '2022-07-30 22:05:00');
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES (72, '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0, 507, 2, 1, 0, '2022-07-30 22:05:00',
        '2022-07-30 22:05:00');
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES (73, '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0, 510, 2, 1, 0, '2022-07-30 22:05:00',
        '2022-07-30 22:05:00');
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES (74, '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0, 512, 2, 1, 0, '2022-07-30 22:05:00',
        '2022-07-30 22:05:00');
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES (75, '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0, 513, 2, 1, 0, '2022-07-30 22:05:00',
        '2022-07-30 22:05:00');
INSERT INTO t_identify_rel_role_permission (id, create_time, create_user_id, status, update_time, update_user_id,
                                            permission_id, role_id, `type`, permission_grant_type, grant_start_time,
                                            grant_end_time)
VALUES (76, '2022-07-30 22:05:00', 0, 1, '2022-07-30 22:05:00', 0, 506, 2, 1, 0, '2022-07-30 22:05:00',
        '2022-07-30 22:05:00');
delete
from t_identify_rel_role_permission
where id = 50;


