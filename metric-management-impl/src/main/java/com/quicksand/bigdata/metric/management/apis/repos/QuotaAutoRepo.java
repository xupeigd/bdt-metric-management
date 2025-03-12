package com.quicksand.bigdata.metric.management.apis.repos;

import com.quicksand.bigdata.metric.management.apis.dbvos.QuotaDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QuotaAutoRepo
 *
 * @author page
 * @date 2022/9/27
 */
@Repository
public interface QuotaAutoRepo
        extends JpaRepository<QuotaDBVO, Integer> {

    List<QuotaDBVO> findByAppId(int appId);

}
