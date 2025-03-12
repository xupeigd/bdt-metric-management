package com.quicksand.bigdata.metric.management.datasource.repos;

import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author page
 * @date 2022/7/26
 */
@Repository
public interface ClusterInfoAutoRepo
        extends JpaRepository<ClusterInfoDBVO, Integer> {


    /**
     * 按id查找
     *
     * @param id datasetId
     * @return instance of ClusterInfoDBVO
     */
    ClusterInfoDBVO findById(int id);

    List<ClusterInfoDBVO> findByNameLike(String keyword);

}
