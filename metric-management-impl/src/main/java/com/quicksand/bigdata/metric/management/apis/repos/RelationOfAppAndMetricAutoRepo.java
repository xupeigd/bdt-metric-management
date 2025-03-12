package com.quicksand.bigdata.metric.management.apis.repos;

import com.quicksand.bigdata.metric.management.apis.dbvos.RelationOfAppAndMetricDBVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * RelationOfAppAndMetricAutoRepo
 *
 * @author page
 * @date 2022/9/27
 */
@Repository
public interface RelationOfAppAndMetricAutoRepo
        extends JpaRepository<RelationOfAppAndMetricDBVO, Integer> {

    List<RelationOfAppAndMetricDBVO> findByAppId(int appId);

    @Query(value = "select tr.* from t_apis_rel_app_metric tr left join t_metric_metrics tm " +
            " on tr.metric_id = tm.id where tr.status = 1 and tm.status = 1  and tm.pub_sub = 1" +
            " and (tr.grant_type = 0 || (tr.grant_type = 1 and tr.grant_start_time <= CURRENT_TIMESTAMP() and tr.grant_end_time >= CURRENT_TIMESTAMP())) " +
            " and tr.app_id = :appId and tm.cn_name like :metricKeyword \n-- #pageReq\n",
            countQuery = "select count() from t_apis_rel_app_metric tr left join t_metric_metrics tm " +
                    " on tr.metric_id = tm.id where tr.status = 1 and tm.status = 1  and tm.pub_sub = 1" +
                    " and (tr.grant_type = 0 || (tr.grant_type = 1 and tr.grant_start_time <= CURRENT_TIMESTAMP() and tr.grant_end_time >= CURRENT_TIMESTAMP())) " +
                    " and tr.app_id = :appId and tm.cn_name like :metricKeyword ",
            nativeQuery = true)
    Page<RelationOfAppAndMetricDBVO> findEffectiveRelasByAppIdAndMetricNameLike(int appId, String metricKeyword, Pageable page);

    @Query(value = "select tr.* from t_apis_rel_app_metric tr left join t_metric_metrics tm on tr.metric_id = tm.id " +
            " where tm.status = 1 and (tm.pub_sub <> 1 " +
            " OR tr.status = 0 " +
            " OR (tr.grant_type = 1 and (tr.grant_start_time > CURRENT_TIMESTAMP() OR tr.grant_end_time < CURRENT_TIMESTAMP()))) " +
            " and tr.app_id = :appId and tm.cn_name like :metricKeyword \n-- #pageReq\n",
            countQuery = "select count() from t_apis_rel_app_metric tr left join t_metric_metrics tm on tr.metric_id = tm.id " +
                    " where tm.status = 1 and (tm.pub_sub <> 1 " +
                    " OR tr.status = 0 " +
                    " OR (tr.grant_type = 1 and (tr.grant_start_time > CURRENT_TIMESTAMP() OR tr.grant_end_time < CURRENT_TIMESTAMP()))) " +
                    " and tr.app_id = :appId and tm.cn_name like :metricKeyword ",
            nativeQuery = true)
    Page<RelationOfAppAndMetricDBVO> findIneffectiveRelasByAppIdAndMetricNameLike(int appId, String metricKeyword, Pageable page);

    @Query(value = "select tr.* from t_apis_rel_app_metric tr left join t_metric_metrics tm " +
            " on tr.metric_id = tm.id where tr.status = 1 and tm.status = 1  and tm.pub_sub = 1" +
            " and (tr.grant_type = 0 || (tr.grant_type = 1 and tr.grant_start_time <= CURRENT_TIMESTAMP() and tr.grant_end_time >= CURRENT_TIMESTAMP())) " +
            " and tr.app_id = :appId ",
            nativeQuery = true)
    List<RelationOfAppAndMetricDBVO> findAllEffectiveRelasByAppId(int appId);

    @Query(value = "select tr.* from t_apis_rel_app_metric tr left join t_metric_metrics tm " +
            " on tr.metric_id = tm.id where tr.status = 1 and tm.status = 1  and tm.pub_sub = 1" +
            " and (tr.grant_type = 0 || (tr.grant_type = 1 and tr.grant_start_time <= CURRENT_TIMESTAMP() and tr.grant_end_time >= CURRENT_TIMESTAMP())) ",
            nativeQuery = true)
    List<RelationOfAppAndMetricDBVO> findAllEffectiveRelas();

    List<RelationOfAppAndMetricDBVO> findAllByAppIdAndMetricId(int appId, int metricId);

    /**
     * 按照应用id查找关系
     *
     * @param appIds 应用id
     * @return list of RelationOfAppAndMetricDBVO
     */
    @Query(value = "select ts.app_id, COUNT(1) as total from ( select tr.* from t_apis_rel_app_metric tr left join t_metric_metrics tm " +
            " on tr.metric_id = tm.id where tr.status = 1 and tm.status = 1  and tm.pub_sub = 1" +
            " and (tr.grant_type = 0 || (tr.grant_type = 1 and tr.grant_start_time <= CURRENT_TIMESTAMP() and tr.grant_end_time >= CURRENT_TIMESTAMP())) " +
            " and tr.app_id in :appIds ) ts group by ts.app_id", nativeQuery = true)
    List<Map<String, Long>> findByAppIds(List<Integer> appIds);

    @Query(value = "select tr.* from t_apis_rel_app_metric tr left join t_metric_metrics tm on tr.metric_id = tm.id " +
            " where tm.status = 1 and (tm.pub_sub <> 1 " +
            " OR tr.status = 0 " +
            " OR (tr.grant_type = 1 and (tr.grant_start_time > CURRENT_TIMESTAMP() OR tr.grant_end_time < CURRENT_TIMESTAMP()))) " +
            " and tr.app_id = :appId and tr.metric_id in :metricIds ",
            nativeQuery = true)
    List<RelationOfAppAndMetricDBVO> findIneffectiveRelasByAppIdAndMetricIds(Integer appId, List<Integer> metricIds);

}
