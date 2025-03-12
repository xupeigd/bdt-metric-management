package com.quicksand.bigdata.metric.management.apis.repos;

import com.quicksand.bigdata.metric.management.apis.dbvos.AuthTokenDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AuthTokenAutoRepo
 *
 * @author page
 * @date 2022/10/10
 */
@Repository
public interface AuthTokenAutoRepo
        extends JpaRepository<AuthTokenDBVO, Integer> {
}
