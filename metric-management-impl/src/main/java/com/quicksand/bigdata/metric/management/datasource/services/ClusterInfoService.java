package com.quicksand.bigdata.metric.management.datasource.services;

import com.quicksand.bigdata.metric.management.datasource.models.ClusterInfoModel;
import com.quicksand.bigdata.metric.management.datasource.vos.ClusterInfoVO;
import com.quicksand.bigdata.vars.util.PageImpl;

import javax.transaction.Transactional;
import java.util.Collection;

public interface ClusterInfoService {

    PageImpl<ClusterInfoVO> queryClusterInfos(int pageNo, int pageSize);

    @Transactional
    ClusterInfoVO saveClusterInfo(ClusterInfoModel model);

    ClusterInfoVO queryClusterInfo(Integer clusterId);

    @Transactional
    void deleteClusterInfos(Collection<Integer> clusterIds);

}
