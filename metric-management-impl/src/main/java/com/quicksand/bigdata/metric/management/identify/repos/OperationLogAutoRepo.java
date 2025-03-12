package com.quicksand.bigdata.metric.management.identify.repos;

import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * OperationLogAutoRepo
 *
 * @author page
 * @date 2020/8/18 17:00
 */
@Repository
public interface OperationLogAutoRepo
        extends JpaRepository<OperationLogDBVO, Long> {

    Page<OperationLogDBVO> findByUserId(Integer operationUserId, Pageable pageable);

    @Query(value = "select max(`id`) as id, max(`operation_time`) as `operation_time`, max(`address`) as `address`, max(`ip`) as `ip`," +
            " max(`detail`) as `detail`, max(`type`) as `type`, max(`user_id`) as `user_id` from t_identify_operation_logs where `user_id` in (:userIds) and `type` = :type group by `user_id`",
            nativeQuery = true)
    List<OperationLogDBVO> queryLastLogsByUserIdsAndType(Collection<Integer> userIds, int type);

}
