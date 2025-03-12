package com.quicksand.bigdata.metric.management.consts;

/**
 * TableNames
 *
 * @author page
 * @date 2020/8/18 15:15
 */
public class TableNames {

    /**
     * 用户的主数据表
     */
    public static final String TABLE_USERS = "t_identify_users";

    /**
     * 角色的主数据表
     */
    public static final String TABLE_ROLES = "t_identify_roles";

    /**
     * 权限的主数据表
     */
    public static final String TABLE_PERMISSIONS = "t_identify_permissions";

    /**
     * 用户与角色关系表
     */
    public static final String TABLE_REL_USER_ROLE = "t_identify_rel_user_role";

    /**
     * 用户与权限关系表
     */
    @Deprecated
    public static final String TABLE_REL_USER_PERMISSION = "t_identify_rel_user_permission";

    /**
     * 角色与权限的关系表
     */
    public static final String TABLE_REL_ROLE_PERMISSION = "t_identify_rel_role_permission";

    /**
     * 操作日志表
     */
    public static final String TABLE_OPERATION_LOG = "t_identify_operation_logs";

    /**
     * Id段
     */
    public static final String TABLE_IDS = "t_metrics_ids";

    /**
     * 用户的主数据表
     */
    public static final String TABLE_CLUSTER_INFOS = "t_datasource_cluster_infos";

    /**
     * Dataset信息主数据表
     */
    public static final String TABLE_DATASOURCE_DATASETS = "t_datasource_datasets";

    /**
     * Dataset identifier
     */
    public static final String TABLE_DATASOURCE_DATASET_IDENTIFIERS = "t_datasource_dataset_identifiers";

    /**
     * Dataset字段信息表
     */
    public static final String TABLE_DATASOURCE_DATASET_COLUMNS = "t_datasource_dataset_columns";

    /**
     * 指标实体数据表
     */
    public static final String TABLE_METRIC_METRICS = "t_metric_metrics";

    /**
     * 指标维度数据表
     */
    public static final String TABLE_METRIC_DIMENSIONS = "t_metric_dimensions";


    /**
     * 指标度量数据表
     */
    public static final String TABLE_METRIC_MEASURES = "t_metric_measures";


    /**
     * 指标目录实体数据表
     */
    public static final String TABLE_METRIC_CATALOGS = "t_metric_catalogs";

    /**
     * yaml片段实体表
     */
    public static final String TABLE_YAML_SEGMENTS = "t_yaml_segments";

    /**
     * 草稿表
     */
    public static final String TABLE_DRAFT_DRAFTS = "t_draft_drafts";

    /**
     * Dataset 数据集及负责人关系表
     */
    public static final String TABLE_DATASOURCE_REL_DATASET_OWNER = "t_datasource_dataset_owners";

    /**
     * Metric 指标与业务负责人关系表
     */
    public static final String TABLE_METRIC_REL_BUSINESS_OWNER = "t_metric_metrics_business_owners";

    /**
     * Dataset 指标与技术负责人关系表
     */
    public static final String TABLE_METRIC_REL_TECH_OWNER = "t_metric_metrics_tech_owners";

    /**
     * 应用数据表
     */
    public static final String TABLE_APIS_APPS = "t_apis_apps";

    /**
     * APP与指标关联表
     */
    public static final String TABLE_APIS_REL_APP_METRIC = "t_apis_rel_app_metric";

    /**
     * 额度数据表
     */
    public static final String TABLE_APIS_QUOTAS = "t_apis_quotas";

    /**
     * 授权token
     */
    public static final String TABLE_APIS_AUTH_TOKENS = "t_apis_auth_token";

    /**
     * 调用申请记录数据表
     */
    public static final String TABLE_APIS_INVOKE_APPLY_RECORDS = "t_apis_invoke_apply_records";

    /**
     * 调用申请记录与指标关系表
     */
    public static final String TABLE_APIS_REL_INVOKE_APPLY_RECORDS_METRIC = "t_apis_rel_invoke_apply_records_metric";

    /**
     * 额度消耗数据表
     */
    public static final String TABLE_APIS_QUOTA_COSTS = "t_apis_quota_costs";

    /**
     * 指标调用统计数据表
     */
    public static final String TABLE_APIS_METRIC_INVOKE_STATISTICS = "t_apis_metric_invoke_statisics";

    /**
     * 指标用户标签表
     */
    public static final String TABLE_METRIC_LABELS = "tbl_metric_user_labels";

    /**
     * 指标用户标签表与指标关系表
     */
    public static final String TABLE_METRIC_REL_LABEL_Metric = "tbl_metric_rel_label_metric";

}
