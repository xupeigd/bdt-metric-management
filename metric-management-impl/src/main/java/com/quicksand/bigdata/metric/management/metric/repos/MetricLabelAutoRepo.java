package com.quicksand.bigdata.metric.management.metric.repos;

import com.quicksand.bigdata.metric.management.metric.dbvos.MetricLabelDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * MetricLabelAutoRepo
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/23 17:39
 * @description
 */
public interface MetricLabelAutoRepo extends JpaRepository<MetricLabelDBVO, Integer> {

    /**
     * @param userId 用户id
     * @return
     * @author zhao_xin
     * @description 获取指标下该用户的所有标签
     **/
    List<MetricLabelDBVO> findAllByUserIdOrderByName(Integer userId);


    /**
     * @param userId   用户id
     * @param metricId 指标id
     * @return
     * @author zhao_xin
     * @description 根据用户信息和指标id，获取指标下已绑定该用户的标签
     **/
    @Query(value = "SELECT tmul.* FROM  tbl_metric_user_labels tmul inner join tbl_metric_rel_label_metric tmrlm on tmul.id =tmrlm.label_id  " +
            "where  tmul.status =1 and tmrlm.status =1 and tmul.user_id = :userId and tmrlm.metric_id = :metricId",
            nativeQuery = true)
    List<MetricLabelDBVO> findAllByUserIdAndMetric(Integer userId, Integer metricId);

    /**
     * @param userId 用户id
     * @param names  标签名称列表
     * @return
     * @author zhao_xin
     * @description 搜索用户下是否有相同名称的标签
     **/
    List<MetricLabelDBVO> findAllByUserIdAndNameIn(Integer userId, List<String> names);


    /**
     * @param userId 用户id
     * @param id     标签id
     * @return
     * @author zhao_xin
     * @description 确认标签是否归属用户
     **/
    MetricLabelDBVO findByUserIdAndId(Integer userId, Integer id);


}
