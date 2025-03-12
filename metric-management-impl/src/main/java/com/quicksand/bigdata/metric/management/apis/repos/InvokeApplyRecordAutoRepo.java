package com.quicksand.bigdata.metric.management.apis.repos;

import com.quicksand.bigdata.metric.management.apis.dbvos.InvokeApplyRecordDBVO;
import com.quicksand.bigdata.metric.management.consts.ApproveState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * InvokeApplyRecordAutoRepo
 *
 * @author page
 * @date 2022/10/11
 */
@Repository
public interface InvokeApplyRecordAutoRepo
        extends JpaRepository<InvokeApplyRecordDBVO, Integer> {

    Page<InvokeApplyRecordDBVO> findDistinctByAppIdInAndCreateUserIdInAndApprovedStateInAndMetricsCnNameLike(Collection<Integer> appIds, Collection<Integer> userIds,
                                                                                                             List<ApproveState> filterStates, String metricKeyword, Pageable pageable);

    Page<InvokeApplyRecordDBVO> findDistinctByCreateUserIdInAndApprovedStateInAndMetricsCnNameLike(Collection<Integer> userIds, List<ApproveState> filterStates, String metricKeyword, Pageable pageable);

    Page<InvokeApplyRecordDBVO> findDistinctByAppIdInAndApprovedStateInAndMetricsCnNameLike(Collection<Integer> appIds, List<ApproveState> filterStates, String metricKeyword, Pageable pageable);

    Page<InvokeApplyRecordDBVO> findDistinctByApprovedStateInAndMetricsCnNameLike(Collection<ApproveState> filterStates, String metricsCnNameKeyword, Pageable pageable);

}
