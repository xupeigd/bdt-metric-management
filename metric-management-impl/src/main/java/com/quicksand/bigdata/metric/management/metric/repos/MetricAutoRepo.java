package com.quicksand.bigdata.metric.management.metric.repos;

import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * @author page
 * @date 2022-07-27
 */
public interface MetricAutoRepo
        extends JpaRepository<MetricDBVO, Integer> {

    List<MetricDBVO> findByDatasetIdAndTopicDomainIdAndBusinessDomainIdOrderByIdDesc(int datasetId, int topicId, int businessId);

    Page<MetricDBVO> findByCnNameLike(String cnName, Pageable pageable);

    List<MetricDBVO> findByBusinessDomainId(int domainId);

    List<MetricDBVO> findByTopicDomainId(int domainId);

    List<MetricDBVO> findBySerialNumber(String serialNumber);

    @Query(value = "select * from t_metric_metrics where status=1 and pub_sub=1 " +
            "and ( cn_name like :keyword or en_name like :keyword or cn_alias like :keyword or en_alias like :keyword ) \n-- #pageReq\n",
            countQuery = "select count() from t_metric_metrics where status=1 and pub_sub=1 " +
                    "and ( cn_name like :keyword or en_name like :keyword or cn_alias like :keyword or en_alias like :keyword ) ",
            nativeQuery = true)
    Page<MetricDBVO> findPubMetricsByKeyword(String keyword, Pageable pageable);

    @Query(value = "select tm.* from t_metric_metrics tm " +
            " left join t_metric_metrics_tech_owners tmmto on tm.id = tmmto.metric_id " +
            " left join t_metric_metrics_business_owners tmmbo on tmmto.metric_id = tmmbo.metric_id " +
            " where tm.status = 1 and tm.pub_sub = 1 " +
            " and (tm.cn_name like :keyword or tm.en_name like :keyword or tm.cn_alias like :keyword or tm.en_alias like :keyword) " +
            " and tmmbo.user_id in :businessOwnerIds \n-- #pageReq\n",
            countQuery = "select count() from t_metric_metrics tm " +
                    " left join t_metric_metrics_tech_owners tmmto on tm.id = tmmto.metric_id " +
                    " left join t_metric_metrics_business_owners tmmbo on tmmto.metric_id = tmmbo.metric_id " +
                    " where tm.status = 1 and tm.pub_sub = 1 " +
                    " and (tm.cn_name like :keyword or tm.en_name like :keyword or tm.cn_alias like :keyword or tm.en_alias like :keyword) " +
                    " and tmmbo.user_id in :businessOwnerIds",
            nativeQuery = true)
    Page<MetricDBVO> findPubMetricsByKeywordAndbusinessOwnerIds(String keyword, Collection<Integer> businessOwnerIds, Pageable pageable);

    @Query(value = "select tm.* from t_metric_metrics tm " +
            " left join t_metric_metrics_tech_owners tmmto on tm.id = tmmto.metric_id " +
            " left join t_metric_metrics_business_owners tmmbo on tmmto.metric_id = tmmbo.metric_id " +
            " where tm.status = 1 and tm.pub_sub = 1 " +
            " and (tm.cn_name like :keyword or tm.en_name like :keyword or tm.cn_alias like :keyword or tm.en_alias like :keyword) " +
            " and tmmto.user_id in :techOwnerIds \n-- #pageReq\n",
            countQuery = "select count() from t_metric_metrics tm " +
                    " left join t_metric_metrics_tech_owners tmmto on tm.id = tmmto.metric_id " +
                    " left join t_metric_metrics_business_owners tmmbo on tmmto.metric_id = tmmbo.metric_id " +
                    " where tm.status = 1 and tm.pub_sub = 1 " +
                    " and (tm.cn_name like :keyword or tm.en_name like :keyword or tm.cn_alias like :keyword or tm.en_alias like :keyword) " +
                    " and tmmto.user_id in :techOwnerIds",
            nativeQuery = true)
    Page<MetricDBVO> findPubMetricsByKeywordAndtechOwnerIds(String keyword, Collection<Integer> techOwnerIds, Pageable pageable);

    @Query(value = "select tm.* from t_metric_metrics tm " +
            " left join t_metric_metrics_tech_owners tmmto on tm.id = tmmto.metric_id " +
            " left join t_metric_metrics_business_owners tmmbo on tmmto.metric_id = tmmbo.metric_id " +
            " where tm.status = 1 and tm.pub_sub = 1 " +
            " and (tm.cn_name like :keyword or tm.en_name like :keyword or tm.cn_alias like :keyword or tm.en_alias like :keyword) " +
            " and tmmbo.user_id in :businessOwnerIds " +
            " and tmmto.user_id in :techOwnerIds \n-- #pageReq\n",
            countQuery = "select count() from t_metric_metrics tm " +
                    " left join t_metric_metrics_tech_owners tmmto on tm.id = tmmto.metric_id " +
                    " left join t_metric_metrics_business_owners tmmbo on tmmto.metric_id = tmmbo.metric_id " +
                    " where tm.status = 1 and tm.pub_sub = 1 " +
                    " and (tm.cn_name like :keyword or tm.en_name like :keyword or tm.cn_alias like :keyword or tm.en_alias like :keyword) " +
                    " and tmmbo.user_id in :businessOwnerIds " +
                    " and tmmto.user_id in :techOwnerIds ",
            nativeQuery = true)
    Page<MetricDBVO> findPubMetricsByKeywordAndOwnerIds(String keyword, Collection<Integer> businessOwnerIds, Collection<Integer> techOwnerIds, Pageable pageable);

    @Query(value = "SELECT id,cn_name  from t_metric_metrics  where status =1 AND  pub_sub =1 and id not in (SELECT metric_id from t_apis_rel_app_metric taram where app_id = :appId and status = 1 ) ", nativeQuery = true)
    List<Object[]> findAppSurplusOnlineMetrics(int appId);

    @Query(value = "SELECT taram .metric_id ,taq.quota, taa.id  ,taa.name  from t_apis_rel_app_metric taram\n" +
            "inner join t_apis_apps taa on taram .app_id =taa.id \n" +
            "LEFT JOIN t_apis_quotas taq on taram.quota_id =taq.id \n" +
            "where taram.status =1 and taa.status =1 and taq.status =1\n" +
            "and taram.metric_id = :metricId",
            countQuery = "SELECT COUNT(1)  from t_apis_rel_app_metric taram\n" +
                    "inner join t_apis_apps taa on taram .app_id =taa.id \n" +
                    "LEFT JOIN t_apis_quotas taq on taram.quota_id =taq.id \n" +
                    "where taram.status =1 and taa.status =1 and taq.status =1\n" +
                    "and taram.metric_id = :metricId",
            nativeQuery = true)
    Page<Object[]> findAppInvokeInfoByMetricId(Integer metricId, Pageable pageable);

    MetricDBVO findTopByTopicDomainIdAndBusinessDomainIdOrderBySerialNumberDesc(Integer topicId, Integer businessId);

    List<MetricDBVO> findAllByPubsubOrderByUpdateTime(PubsubStatus pubsubStatus);

    @Query(value = "SELECT max(serial_number)  from t_metric_metrics  where status = 1 and topic_id = :topicId and business_id = :businessId ", nativeQuery = true)
    String getMaxSerialNumberByTopicAndBusiness(Integer topicId, Integer businessId);

    List<MetricDBVO> findAllByEnNameOrCnName(String enName, String cnName);

    MetricDBVO findByEnName(String enName);

    MetricDBVO findMetricByEnNameOrSerialNumber(String enName, String serialNumber);

    @Query(value = "SELECT tmm.* FROM  `t_metric_metrics` tmm inner join `tbl_metric_rel_label_metric` tmrlm on " +
            "tmm .id =tmrlm.metric_id  where tmm .status =1 and tmrlm .status =1 and tmrlm.label_id = :label_id order by tmrlm.update_time desc",
            countQuery = "SELECT count(1) FROM  `t_metric_metrics` tmm inner join `tbl_metric_rel_label_metric` tmrlm on " +
                    "tmm .id =tmrlm.metric_id  where tmm .status =1 and tmrlm .status =1 and tmrlm.label_id = :label_id",
            nativeQuery = true)
    Page<MetricDBVO> findAllByLabelId(Integer label_id, Pageable pageable);

}
