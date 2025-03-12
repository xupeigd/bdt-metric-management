package com.quicksand.bigdata.metric.management.datasource.dms;

import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * ClusterInfoDataManager
 *
 * @author page
 * @date 2022/7/26
 */
public interface ClusterInfoDataManager {

    List<ClusterInfoDBVO> findAllClusterInfos();

    List<ClusterInfoDBVO> findClusterInfosById(Collection<Integer> clusterIds);

    ClusterInfoDBVO findClusterInfo(int id);

    List<ClusterInfoDBVO> findClustersNameLike(String clusterNameKeyword);

    Page<ClusterInfoDBVO> queryClusterInfos(Pageable pageable);

    @Transactional
    void deleteClusterInfos(Collection<Integer> clusterIds);

    @Transactional
    ClusterInfoDBVO saveClusterInfo(ClusterInfoDBVO dbvo);

    @Transactional
    void saveClusterInfos(List<ClusterInfoDBVO> dbvos);

}
