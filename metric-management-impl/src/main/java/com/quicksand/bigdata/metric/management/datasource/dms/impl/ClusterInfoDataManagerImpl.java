package com.quicksand.bigdata.metric.management.datasource.dms.impl;

import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.ClusterInfoDataManager;
import com.quicksand.bigdata.metric.management.datasource.repos.ClusterInfoAutoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * @author page
 * @date 2022/7/26
 */
@Slf4j
@Component
public class ClusterInfoDataManagerImpl
        implements ClusterInfoDataManager {

    @Resource
    ClusterInfoAutoRepo clusterInfoAutoRepo;

    @Override
    public List<ClusterInfoDBVO> findAllClusterInfos() {
        return clusterInfoAutoRepo.findAll(Sort.by("id"));
    }

    @Override
    public List<ClusterInfoDBVO> findClusterInfosById(Collection<Integer> clusterIds) {
        return clusterInfoAutoRepo.findAllById(clusterIds);
    }

    @Override
    public ClusterInfoDBVO findClusterInfo(int id) {
        return clusterInfoAutoRepo.findById(id);
    }

    @Override
    public List<ClusterInfoDBVO> findClustersNameLike(String clusterNameKeyword) {
        return clusterInfoAutoRepo.findByNameLike("%" + clusterNameKeyword + "%");
    }

    @Override
    public Page<ClusterInfoDBVO> queryClusterInfos(Pageable pageable) {
        return clusterInfoAutoRepo.findAll(pageable);
    }

    @Transactional
    @Override
    public void deleteClusterInfos(Collection<Integer> clusterIds) {
        clusterInfoAutoRepo.deleteAllByIdInBatch(clusterIds);
    }

    @Transactional
    @Override
    public ClusterInfoDBVO saveClusterInfo(ClusterInfoDBVO dbvo) {
        return clusterInfoAutoRepo.save(dbvo);
    }

    @Transactional
    @Override
    public void saveClusterInfos(List<ClusterInfoDBVO> dbvos) {
        clusterInfoAutoRepo.saveAll(dbvos);
    }

}
