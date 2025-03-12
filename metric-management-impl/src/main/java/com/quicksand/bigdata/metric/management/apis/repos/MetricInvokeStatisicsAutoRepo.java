package com.quicksand.bigdata.metric.management.apis.repos;

import com.quicksand.bigdata.metric.management.apis.dbvos.MetricInvokeStatisicsDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * MetricInvokeStatisicsAutoRepo
 *
 * @author page
 * @date 2022/12/19
 */
@Repository
public interface MetricInvokeStatisicsAutoRepo
        extends JpaRepository<MetricInvokeStatisicsDBVO, Integer> {

    @Query(value = "select 0 as id, app_id as appId, metric_id as metricId, date_flag as dateFlag, max(quota) as quota, sum(day_cost) as dayCost, max(max_qps) as maxQps " +
            "from t_apis_metric_invoke_statisics where status = 1 and metric_id = :metricId and log_date between :startDate and :endDate group by app_id,metric_id order by app_id",
            nativeQuery = true)
    List<Map<String, Object>> fetchDateRangeStatistics(int metricId, Date startDate, Date endDate);


    MetricInvokeStatisicsDBVO findByAppIdAndMetricIdAndDateFlag(int appId, int metricId, Date dateFlag);

    MetricInvokeStatisicsDBVO findByAppIdAndMetricIdAndLogDate(int appId, int metricId, Date logDate);
}
