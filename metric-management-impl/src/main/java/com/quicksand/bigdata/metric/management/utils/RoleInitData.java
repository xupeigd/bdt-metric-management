package com.quicksand.bigdata.metric.management.utils;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.RoleType;
import com.quicksand.bigdata.metric.management.identify.dbvos.PermissionDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * RoleInitData
 *
 * @author page
 * @date 2022/8/19
 */
public interface RoleInitData {

    static List<RoleDBVO> initDatas() {
        List<RoleDBVO> roles = new ArrayList<>();
        Date initDate = new Date();
        roles.add(new RoleDBVO(0, "System", "SYS", initDate, initDate, RoleType.PUBLIC, DataStatus.ENABLE, 0, 0, new ArrayList<>(), null));
        roles.add(new RoleDBVO(1, "Admin", "Admin", initDate, initDate, RoleType.PUBLIC, DataStatus.ENABLE, 0, 0, PermissionInitData.initDatas(), null));
        roles.add(new RoleDBVO(2, "指标开发者", "metrics-developer", initDate, initDate, RoleType.PUBLIC, DataStatus.ENABLE, 0, 0, new ArrayList<>(), null));
        roles.add(new RoleDBVO(3, "应用开发者", "app-developer", initDate, initDate, RoleType.PUBLIC, DataStatus.ENABLE, 0, 0, new ArrayList<>(), null));
        return roles;
    }

    static List<RoleDBVO> initDatas(List<PermissionDBVO> permissions) {
        List<RoleDBVO> roles = new ArrayList<>();
        Date initDate = new Date();
        roles.add(new RoleDBVO(0, "System", "SYS", initDate, initDate, RoleType.PUBLIC, DataStatus.ENABLE, 0, 0, new ArrayList<>(), null));
        roles.add(new RoleDBVO(1, "Admin", "Admin", initDate, initDate, RoleType.PUBLIC, DataStatus.ENABLE, 0, 0, permissions, null));
        roles.add(new RoleDBVO(2, "指标开发者", "metrics-developer", initDate, initDate, RoleType.PUBLIC, DataStatus.ENABLE, 0, 0, new ArrayList<>(), null));
        roles.add(new RoleDBVO(3, "应用开发者", "app-developer", initDate, initDate, RoleType.PUBLIC, DataStatus.ENABLE, 0, 0, new ArrayList<>(), null));
        return roles;
    }

    static List<String> initSqls() {
        List<String> sqls = new ArrayList<>();
        //roles数据
        sqls.add("INSERT INTO `t_identify_roles` (`id`, `code`, `create_time`, `create_user_id`, `name`, `status`, `type`, `update_time`, `update_user_id`) VALUES (0, 'SYS',  '2022-07-30 22:05:00', 0, 'System', 1, 0,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_roles` (`id`, `code`, `create_time`, `create_user_id`, `name`, `status`, `type`, `update_time`, `update_user_id`) VALUES (1, 'Admin',  '2022-07-30 22:05:00', 0, 'Admin', 1, 0,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_roles` (`id`, `code`, `create_time`, `create_user_id`, `name`, `status`, `type`, `update_time`, `update_user_id`) VALUES (2, 'metrics-developer',  '2022-07-30 22:05:00', 0, '指标开发者', 1, 0,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_roles` (`id`, `code`, `create_time`, `create_user_id`, `name`, `status`, `type`, `update_time`, `update_user_id`) VALUES (3, 'app-developer',  '2022-07-30 22:05:00', 0, '应用开发者', 1, 0,  '2022-07-30 22:05:00', 0);");
        // relations of role and permission
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (1, 1, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (2, 2, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (3, 3, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (4, 4, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (5, 5, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (6, 300, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (7, 301, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (8, 302, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (9, 303, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (10, 304, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (11, 305, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (12, 306, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (13, 307, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (14, 308, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (15, 400, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (16, 401, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (17, 402, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (18, 403, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (19, 404, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (20, 405, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (21, 406, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (22, 450, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (23, 501, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (24, 320, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (25, 321, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (26, 322, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (27, 323, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (45, 309, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (46, 502, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (47, 503, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (48, 504, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (49, 505, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (50, 506, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (51, 520, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (52, 521, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_role_permission` (`id`, `permission_id`, `role_id`, `create_time`, `create_user_id`, `status`, `update_time`, `update_user_id`) VALUES (53, 522, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-11 17:45:53', 0, 1, '2022-11-11 17:45:53', 0, 6, 1, 1, 0, '2022-11-11 17:45:53', '2022-11-11 17:45:53');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-01 09:42:09', 0, 502, 1, 1, 0, '2022-11-01 06:30:38', '2022-11-01 06:30:38');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 503, 1, 1, 0, '2022-11-01 06:30:38', '2022-11-01 06:30:38');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 504, 1, 1, 0, '2022-11-01 06:30:38', '2022-11-01 06:30:38');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 505, 1, 1, 0, '2022-11-01 06:30:38', '2022-11-01 06:30:38');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 506, 1, 1, 0, '2022-11-01 06:30:38', '2022-11-01 06:30:38');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 507, 1, 1, 0, '2022-11-01 06:30:38', '2022-11-01 06:30:38');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 508, 1, 1, 0, '2022-11-01 06:30:38', '2022-11-01 06:30:38');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-01 17:42:08', 0, 509, 1, 1, 0, '2022-11-01 06:30:38', '2022-11-01 06:30:38');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-11 17:45:53', 0, 510, 1, 1, 0, '2022-11-11 17:45:53', '2022-11-11 17:45:53');");
        sqls.add("INSERT INTO t_identify_rel_role_permission ( create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES( '2022-11-01 17:42:08', 0, 1, '2022-11-11 09:45:54', 0, 511, 1, 1, 0, '2022-11-11 17:45:53', '2022-11-11 17:45:53');");
        sqls.add("INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 512, 1, 1, 0, '2022-11-17 15:38:01', '2022-11-17 15:38:01');");
        sqls.add("INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 513, 1, 1, 0, '2022-11-17 15:38:01', '2022-11-17 15:38:01');");
        sqls.add("INSERT INTO t_identify_rel_role_permission (create_time, create_user_id, status, update_time, update_user_id, permission_id, role_id, `type`, permission_grant_type, grant_start_time, grant_end_time) VALUES('2022-11-17 15:38:01', 0, 1, '2022-11-17 15:38:01', 0, 514, 1, 1, 0, '2022-11-17 15:38:01', '2022-11-17 15:38:01');");
        return sqls;
    }

}
