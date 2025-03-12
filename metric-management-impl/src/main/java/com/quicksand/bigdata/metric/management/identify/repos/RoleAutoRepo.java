package com.quicksand.bigdata.metric.management.identify.repos;

import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * RoleAutoRepo
 *
 * @author page
 * @date 2020/8/18 15:48
 */
@Repository
public interface RoleAutoRepo
        extends JpaRepository<RoleDBVO, Integer> {
}
