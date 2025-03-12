package com.quicksand.bigdata.metric.management.utils;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.identify.dbvos.PermissionDBVO;
import com.quicksand.bigdata.vars.security.consts.PermissionType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PermissionInitData
 *
 * @author page
 * @date 2022/8/19
 */
public interface PermissionInitData {

    static List<PermissionDBVO> initDatas() {
        Date initDate = new Date();
        List<PermissionDBVO> datas = new ArrayList<>();
        datas.add(new PermissionDBVO(1, "ME_DASHBOARD", "指标看板", 0, "", PermissionType.MENU, 0, "dashboard", "#", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(2, "ME_MARKET", "指标市场", 0, "", PermissionType.MENU, 0, "market", "/market/list", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(3, "ME_METRICS", "指标管理", 0, "", PermissionType.MENU, 0, "metric-management", "/metric/list", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(4, "ME_DATASET", "数据集管理", 0, "", PermissionType.MENU, 0, "dataset-management", "/dataset/list", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(5, "ME_APPLICATION", "应用中心", 0, "", PermissionType.MENU, 0, "application-management", "/app/list", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(6, "ME_APPROVAL", "审批中心", 0, "", PermissionType.MENU, 0, "approve-management", "/approval", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(300, "OP_METRICS_LIST", "查看指标列表", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(301, "OP_METRICS_SEARCH", "搜索指标列表", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(302, "OP_METRICS_PUS", "指标上下线", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(303, "OP_METRICS_CREATE", "创建指标", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(304, "OP_METRICS_MODIFY", "修改单指标", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(305, "OP_METRICS_DELETE", "删除单指标", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(306, "OP_METRICS_DETAIL", "查看单指标的详情", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(307, "OP_METRICS_MODEL", "查看单指标的模型", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(308, "OP_METRICS_CALCULATE", "指标试算(查看SQL)", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(309, "OP_METRICS_CALCULATE_ACTION", "指标试算(运行查询)", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(320, "OP_METRICS_CATALOGS_LIST", "查看指标目录列表", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(321, "OP_METRICS_CATALOGS_CREATE", "创建指标目录实体", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(322, "OP_METRICS_CATALOGS_MODIFY", "修改指标目录实体", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(323, "OP_METRICS_CATALOGS_DELETE", "删除指标目录实体", 0, "", PermissionType.OPERATION, 3, "metric-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(400, "OP_DATASET_LIST", "查看数据集列表", 0, "", PermissionType.OPERATION, 4, "dataset-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(401, "OP_DATASET_SEARCH", "搜索数据集", 0, "", PermissionType.OPERATION, 4, "dataset-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(402, "OP_DATASET_CREATE", "创建数据集", 0, "", PermissionType.OPERATION, 4, "dataset-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(403, "OP_DATASET_MODIFY", "修改单一数据集", 0, "", PermissionType.OPERATION, 4, "dataset-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(404, "OP_DATASET_DELETE", "删除单一数据集", 0, "", PermissionType.OPERATION, 4, "dataset-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(405, "OP_DATASET_DETAIL", "查看单一数据集详情", 0, "", PermissionType.OPERATION, 4, "dataset-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(406, "OP_DATASET_COLUMNS", "查看单一数据集字段信息", 0, "", PermissionType.OPERATION, 4, "dataset-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(450, "OP_DATASOURCE_CLUSTER_LIST", "查看数据集群列表", 0, "", PermissionType.OPERATION, 4, "dataset-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(501, "OP_USER_LIST", "查看用户列表", 0, "", PermissionType.OPERATION, 0, "identify", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(502, "OP_APP_CREATE", "应用创建", 0, "", PermissionType.OPERATION, 5, "application-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(503, "OP_APP_MODIFY", "应用修改", 0, "", PermissionType.OPERATION, 5, "application-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(504, "OP_APP_MODIFY_TOKEN", "应用Token更换", 0, "", PermissionType.OPERATION, 5, "application-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(505, "OP_APP_DELETE", "应用删除", 0, "", PermissionType.OPERATION, 5, "application-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(506, "OP_APP_LIST", "应用列表获取", 0, "", PermissionType.OPERATION, 5, "application-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(507, "OP_APP_INVOKE_APPROVE", "调用申请审核", 0, "", PermissionType.OPERATION, 6, "approve-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(508, "OP_APP_INVOKE_LIST", "获取接口的调用信息", 0, "", PermissionType.OPERATION, 2, "market", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(509, "OP_APP_INVOKE_APPLY", "新建调用申请", 0, "", PermissionType.OPERATION, 2, "market", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(510, "ME_APPROVAL_LIST", "待我审批列表", 0, "", PermissionType.OPERATION, 6, "approve-management", "/approval/pending-list", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(511, "ME_MY_APPROVAL", "我提交的审批", 0, "", PermissionType.OPERATION, 6, "approve-management", "/approval/submit-list", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(512, "OP_APPROVAL_LIST", "审批列表", 0, "", PermissionType.OPERATION, 510, "approve-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(513, "OP_APPROVAL_DETAIL", "审批详情", 0, "", PermissionType.OPERATION, 510, "approve-management", "", initDate, initDate, DataStatus.ENABLE));
        datas.add(new PermissionDBVO(514, "OP_APPROVAL_MINE", "我提交的审批列表", 0, "", PermissionType.OPERATION, 511, "approve-management", "", initDate, initDate, DataStatus.ENABLE));
        return datas;
    }

    static List<String> initSqls() {
        List<String> sqls = new ArrayList<>();
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (1 , 'ME_DASHBOARD' , '指标看板' , '2022-07-30 22:05:00', '#', 0, 'dashboard', 1, 1, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (2 , 'ME_MARKET' , '指标市场' , '2022-07-30 22:05:00', '/market/list', 0, 'market', 1, 1, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (3 , 'ME_METRICS' , '指标管理' , '2022-07-30 22:05:00', '/metric/list', 0, 'metric-management', 1, 1, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (4 , 'ME_DATASET' , '数据集管理' , '2022-07-30 22:05:00', '/dataset/list', 0, 'dataset-management', 1, 1, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (5 , 'ME_APPLICATION' , '应用中心', '2022-07-30 22:05:00', '/app/list', 0, 'application-management', 1, 1, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (6 , 'ME_APPROVAL' , '审批中心', '2022-07-30 22:05:00', '/approval', 0, 'approve-management', 1, 1, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (300, 'OP_METRICS_LIST' , '查看指标列表', '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (301, 'OP_METRICS_SEARCH' , '搜索指标列表', '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (302, 'OP_METRICS_PUS' , '指标上下线', '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (303, 'OP_METRICS_CREATE' , '创建指标', '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (304, 'OP_METRICS_MODIFY' , '修改单指标', '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (305, 'OP_METRICS_DELETE' , '删除单指标', '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (306, 'OP_METRICS_DETAIL' , '查看单指标的详情' , '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (307, 'OP_METRICS_MODEL' , '查看单指标的模型' , '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (308, 'OP_METRICS_CALCULATE' , '指标试算(查看SQL)' , '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (309, 'OP_METRICS_CALCULATE_ACTION' , '指标试算(运行查询)' , '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (320, 'OP_METRICS_CATALOGS_LIST' , '查看指标目录列表' , '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (321, 'OP_METRICS_CATALOGS_CREATE' , '创建指标目录实体' , '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (322, 'OP_METRICS_CATALOGS_MODIFY' , '修改指标目录实体' , '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (323, 'OP_METRICS_CATALOGS_DELETE' , '删除指标目录实体' , '2022-07-30 22:05:00', '', 3, 'metric-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (400, 'OP_DATASET_LIST' , '查看数据集列表' , '2022-07-30 22:05:00', '', 4, 'dataset-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (401, 'OP_DATASET_SEARCH' , '搜索数据集' , '2022-07-30 22:05:00', '', 4, 'dataset-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (402, 'OP_DATASET_CREATE' , '创建数据集' , '2022-07-30 22:05:00', '', 4, 'dataset-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (403, 'OP_DATASET_MODIFY' , '修改单一数据集' , '2022-07-30 22:05:00', '', 4, 'dataset-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (404, 'OP_DATASET_DELETE' , '删除单一数据集' , '2022-07-30 22:05:00', '', 4, 'dataset-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (405, 'OP_DATASET_DETAIL' , '查看单一数据集详情' , '2022-07-30 22:05:00', '', 4, 'dataset-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (406, 'OP_DATASET_COLUMNS' , '查看单一数据集字段信息' , '2022-07-30 22:05:00', '', 4, 'dataset-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (450, 'OP_DATASOURCE_CLUSTER_LIST' , '查看数据集群列表' , '2022-07-30 22:05:00', '', 4, 'dataset-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (501, 'OP_USER_LIST' , '查看用户列表' , '2022-07-30 22:05:00', '', 0, 'identify', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (502, 'OP_APP_LIST' , '查看应用信息列表' , '2022-07-30 22:05:00', '', 5, 'application-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (503, 'OP_APP_CREATE' , '创建应用' , '2022-07-30 22:05:00', '', 5, 'application-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (504, 'OP_APP_MODIFY' , '修改应用' , '2022-07-30 22:05:00', '', 5, 'application-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (505, 'OP_APP_MODIFY_TOKEN' , '修改应用的token' , '2022-07-30 22:05:00', '', 5, 'application-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (506, 'OP_APP_DELETE' , '删除应用' , '2022-07-30 22:05:00', '', 5, 'application-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (520, 'OP_APP_INVOKE_LIST' , '查看调用申请记录' , '2022-07-30 22:05:00', '', 5, 'application-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (521, 'OP_APP_INVOKE_APPLY' , '创建调用申请' , '2022-07-30 22:05:00', '', 5, 'application-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (522, 'OP_APP_INVOKE_APPROVE' , '审核调用申请' , '2022-07-30 22:05:00', '', 5, 'application-management', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO `t_identify_permissions` (`id`, `code`, `name`, `create_time`, `path`, `parent_id`, `module`, `status`, `type`, `update_time`) VALUES (501, 'OP_USER_LIST', '2022-07-30 22:05:00', 'identify', '查看用户列表', 0, '', 1, 2, '2022-07-30 22:05:00');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(6, 'ME_APPROVAL', '2022-11-11 07:05:40', 'approve-management', '审批中心', 0, '/approval', 1, 1, '2022-11-11 07:05:44');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(502, 'OP_APP_CREATE', '2022-11-01 09:36:07', 'application-management', '应用创建', 5, '', 1, 2, '2022-11-01 17:36:07');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(503, 'OP_APP_MODIFY', '2022-11-01 17:36:07', 'application-management', '应用修改', 5, '', 1, 2, '2022-11-01 17:36:07');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(504, 'OP_APP_MODIFY_TOKEN', '2022-11-01 17:36:07', 'application-management', '应用Token更换', 5, '', 1, 2, '2022-11-01 17:36:07');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(505, 'OP_APP_DELETE', '2022-11-01 17:36:07', 'application-management', '应用删除', 5, '', 1, 2, '2022-11-01 17:36:07');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(506, 'OP_APP_LIST', '2022-11-01 17:36:07', 'application-management', '应用列表获取', 5, '', 1, 2, '2022-11-01 17:36:07');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(507, 'OP_APP_INVOKE_APPROVE', '2022-11-01 17:36:07', 'approve-management', '调用申请审核', 6, '', 1, 2, '2022-11-01 17:36:07');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(508, 'OP_APP_INVOKE_LIST', '2022-11-01 17:36:07', 'market', '获取接口的调用信息', 2, '', 1, 2, '2022-11-01 17:36:07');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(509, 'OP_APP_INVOKE_APPLY', '2022-11-01 17:36:07', 'market', '新建调用申请', 2, '', 1, 2, '2022-11-01 17:36:07');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(510, 'ME_APPROVAL_LIST', '2022-11-11 07:39:56', 'approve-management', '待我审批列表', 6, '/approval/pending-list', 1, 1, '2022-11-11 07:44:04');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(511, 'ME_MY_APPROVAL', '2022-11-11 15:39:56', 'approve-management', '我提交的审批', 6, '/approval/submit-list', 1, 1, '2022-11-11 07:44:04');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(512, 'OP_APPROVAL_LIST', '2022-11-17 15:34:34', 'approve-management', '审批列表', 510, '', 1, 2, '2022-11-17 15:34:33');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(513, 'OP_APPROVAL_DETAIL', '2022-11-17 15:34:33', 'approve-management', '审批详情', 510, '', 1, 2, '2022-11-17 15:34:33');");
        sqls.add("INSERT INTO t_identify_permissions (id, code, create_time, module, name, parent_id, `path`, status, `type`, update_time) VALUES(514, 'OP_APPROVAL_MINE', '2022-11-17 15:34:33', 'approve-management', '我提交的审批列表', 511, '', 1, 2, '2022-11-17 15:34:33');");
        return sqls;
    }

}
