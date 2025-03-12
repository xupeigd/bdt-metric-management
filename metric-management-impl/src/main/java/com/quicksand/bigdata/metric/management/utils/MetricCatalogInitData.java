package com.quicksand.bigdata.metric.management.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * MetricCatalogInitData
 * (指标目录初始化数据)
 *
 * @author page
 * @date 2022/9/5
 */
public interface MetricCatalogInitData {

    static List<String> initSqls() {
        List<String> sqls = new ArrayList<>();
        sqls.add("INSERT INTO `t_metric_catalogs` (`id`, `code`, `create_time`, `create_user_id`, `name`, `status`, `type`, `update_time`, `update_user_id`, `parent_id`) VALUES (1, '000', '2022-08-02 07:25:18', 0, '目录', 1, 0, '2022-07-30 22:05:00', 0, -1);");
        return sqls;
    }

}
