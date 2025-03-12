package com.quicksand.bigdata.metric.management.yaml.services;

import com.quicksand.bigdata.metric.management.apis.models.ConditionModel;
import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;

import java.util.List;

/**
 * ExplainService
 *
 * @author page
 * @date 2022/10/18
 */
public interface ExplainService {

    /**
     * 解析器的标识
     */
    String flag();

    /**
     * 校验
     */
    boolean validation(MetricVO metric, DatasetVO dataset, List<String> dimensionss, ConditionModel condition, List<SortModel> sorts);

    /**
     * 编译为sql
     *
     * @param metrics    指标对象
     * @param dimensions 纬度（编译所需）: 穿越纬度使用__标识
     * @param condition  条件LR式
     * @param sorts      排序
     * @return sql
     */
    String expain2Sql(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts);


}
