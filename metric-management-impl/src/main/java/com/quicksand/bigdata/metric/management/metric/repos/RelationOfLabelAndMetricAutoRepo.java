package com.quicksand.bigdata.metric.management.metric.repos;

import com.quicksand.bigdata.metric.management.metric.dbvos.RelationOfLabelAndMetricDBVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * RelationOfLabelAndMetricAutoRepo
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/24 10:35
 * @description
 */
public interface RelationOfLabelAndMetricAutoRepo extends JpaRepository<RelationOfLabelAndMetricDBVO, Integer> {
    List<RelationOfLabelAndMetricDBVO> findAllByLabelIdAndMetricId(Integer labelId, Integer metricId);

    List<RelationOfLabelAndMetricDBVO> findAllByLabelId(Integer labelId);

}
