package com.quicksand.bigdata.metric.management.apis.repos;

import com.quicksand.bigdata.metric.management.apis.dbvos.QuotaCostDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * QuotaCostAutoRepo
 *
 * @author page
 * @date 2022/12/14
 */
@Repository
public interface QuotaCostAutoRepo
        extends JpaRepository<QuotaCostDBVO, Integer> {

    @Query(value = "select * from `t_apis_quota_costs` where status = 1 and lock_down_flag ='' order by update_time ",
            nativeQuery = true)
    List<QuotaCostDBVO> findCurQuotaCosts();

    @Query(value = "select * from `t_apis_quota_costs` where status = 1 and lock_down_flag ='' and app_id in :appIds " +
            "and metric_id in :metricIds order by update_time ",
            nativeQuery = true)
    List<QuotaCostDBVO> findCurQuotaCosts(Collection<Integer> appIds, Collection<Integer> metricIds);

    @Query(value = "select * from `t_apis_quota_costs` where status = 1 and lock_down_flag ='' and app_id in :appIds " +
            "order by update_time ",
            nativeQuery = true)
    List<QuotaCostDBVO> findCurQuotaCostsByAppIds(Collection<Integer> appIds);

    @Query(value = "select * from `t_apis_quota_costs` where status = 1 and lock_down_flag ='' and metric_id in :metricIds " +
            "order by update_time ",
            nativeQuery = true)
    List<QuotaCostDBVO> findCurQuotaCostsByMetriceIds(Collection<Integer> metricIds);

}