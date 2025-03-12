package com.quicksand.bigdata.metric.management.apis.repos;

import com.quicksand.bigdata.metric.management.apis.dbvos.RelationOfApplyAndMetricDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author page
 * @date 2022/10/11
 */
@Repository
public interface RelationOfApplyAndMetricAutoRepo
        extends JpaRepository<RelationOfApplyAndMetricDBVO, Integer> {
}
