package com.quicksand.bigdata.metric.management.yaml.services;

import com.quicksand.bigdata.metric.management.apis.models.ConditionModel;
import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;

import java.util.List;

/**
 * MerticFlowService
 * (MF服务)
 *
 * @author page
 * @date 2022/10/18
 */
public interface MerticFlowService {

    /**
     * 校验
     */
    boolean validation(MetricVO metric, DatasetVO dataset, List<String> dimensionss, ConditionModel condition, List<SortModel> sorts);

    /**
     * 调度配置
     */
    void dispatchConfigs(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) throws Exception;

    /**
     * 运行编译
     */
    String explain(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts);

    /**
     * 清理现场
     */
    void clean(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts);

}
