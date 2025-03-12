package com.quicksand.bigdata.metric.management.identify.repos;

import com.quicksand.bigdata.metric.management.identify.dbvos.RelationOfUserAndRoleDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RelationOfUserAndRoleAutoRepo
 *
 * @author page
 * @date 2020/8/18 16:16
 */
@Repository
public interface RelationOfUserAndRoleAutoRepo
        extends JpaRepository<RelationOfUserAndRoleDBVO, Integer> {

    List<RelationOfUserAndRoleDBVO> findAllByUserId(int id);

}
