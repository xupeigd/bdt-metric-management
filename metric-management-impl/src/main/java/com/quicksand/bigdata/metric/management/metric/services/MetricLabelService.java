package com.quicksand.bigdata.metric.management.metric.services;

import com.quicksand.bigdata.metric.management.metric.dbvos.RelationOfLabelAndMetricDBVO;
import com.quicksand.bigdata.metric.management.metric.models.MetricLabelBindModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricLabelModifyModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import com.quicksand.bigdata.vars.util.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * MetricLabelService
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/24 10:59
 * @description
 */
public interface MetricLabelService {
    /**
     * @param metricId 指标id
     * @return
     * @author zhao_xin
     * @description 获取用户单个指标下的已有标签
     **/
    List<MetricLabelModifyModel> findUserMetricLabelsBind(Integer metricId);


    /**
     * @param metricId 指标id
     * @return
     * @author zhao_xin
     * @description 获取用户对某个指标下的可添加标签
     **/
    List<MetricLabelModifyModel> findUserMetricLabelsForAdd(Integer metricId);


    /**
     * @return
     * @author zhao_xin
     * @description 获取用户对某个指标下的可添加标签
     **/
    List<MetricLabelModifyModel> findUserAllLabels();


    /**
     * @return
     * @author zhao_xin
     * @description 获取标签下的所有指标
     **/
    PageImpl<MetricOverviewModel> findAllMetricsByLabelId(Integer labelId, Pageable pageable);

    /**
     * @param labelId 指标id
     * @author zhao_xin
     * @description 逻辑删除标签
     **/
    Void markDeleteByLabelId(Integer labelId);

    /**
     * 更新/新建标签实体
     *
     * @param model 更新/新建参数
     * @return instance of MetricVO
     */
    MetricLabelModifyModel upsertMetricLabel(MetricLabelModifyModel model);


    /**
     * @param labelId 指标id
     * @author zhao_xin
     * @description 删除指标与标签的绑定关系
     **/
    Void markRelationDeleteByLabelIdAndMetricId(Integer metricId, Integer labelId);

    /**
     * @param model 指标
     * @author zhao_xin
     * @description 新增指标与标签的绑定关系
     **/
    List<RelationOfLabelAndMetricDBVO> bindMetricAndLabel(MetricLabelBindModel model);
}
