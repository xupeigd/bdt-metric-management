package com.quicksand.bigdata.metric.management.datasource.dms.impl.dms;

import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.repos.DatasetAutoRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * DatasetDataManagerImpl
 *
 * @author page
 * @date 2022/7/28
 */
@Component
public class DatasetDataManagerImpl
        implements DatasetDataManager {

    @Resource
    DatasetAutoRepo datasetAutoRepo;

    @Override
    public Page<DatasetDBVO> findAllDatasets(@Nullable String namekeyword, List<Integer> clusterIds, List<Integer> ownerIds, Pageable pageReq) {
        PageRequest jpqlPageReq = PageRequest.of(pageReq.getPageNumber(), pageReq.getPageSize(), Sort.by("updateTime").descending());
        if (!StringUtils.hasText(namekeyword)
                && CollectionUtils.isEmpty(clusterIds)
                && CollectionUtils.isEmpty(ownerIds)) {
            return datasetAutoRepo.findAll(jpqlPageReq);
        } else if (StringUtils.hasText(namekeyword)
                && !CollectionUtils.isEmpty(clusterIds)
                && !CollectionUtils.isEmpty(ownerIds)) {
            return datasetAutoRepo.findByNameLikeAndClusterIdAndOwner("%" + namekeyword + "%", StringUtils.collectionToCommaDelimitedString(clusterIds),
                    StringUtils.collectionToCommaDelimitedString(ownerIds), pageReq);
        } else if (StringUtils.hasText(namekeyword)) {
            return CollectionUtils.isEmpty(clusterIds) && CollectionUtils.isEmpty(ownerIds)
                    ? datasetAutoRepo.findByNameLike("%" + namekeyword + "%", jpqlPageReq)
                    : (!CollectionUtils.isEmpty(clusterIds)
                    ? datasetAutoRepo.findByNameLikeAndClusterIdIn("%" + namekeyword + "%", clusterIds, jpqlPageReq)
                    : datasetAutoRepo.findByNameLikeAndOwner("%" + namekeyword + "%", StringUtils.collectionToCommaDelimitedString(ownerIds), pageReq));
        } else {
            return !CollectionUtils.isEmpty(ownerIds)
                    ? datasetAutoRepo.findByOwner(ownerIds, pageReq)
                    : datasetAutoRepo.findByClusterIdIn(clusterIds, jpqlPageReq);
        }
    }


    @Override
    public DatasetDBVO findDatasetById(int datasetId) {
        return datasetAutoRepo.findById(datasetId);
    }

    @Override
    public void updateDataset(DatasetDBVO dataset) {
        datasetAutoRepo.save(dataset);
    }


    @Override
    public DatasetDBVO saveDataset(DatasetDBVO dataset) {
        return datasetAutoRepo.save(dataset);
    }

    @Override
    public DatasetDBVO findDatasetByName(String name) {
        return datasetAutoRepo.findByName(name);
    }

    @Override
    public DatasetDBVO findFirstByName(String name, Integer clusterId) {
        List<DatasetDBVO> list = datasetAutoRepo.findByNameAndClusterId(name, clusterId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<DatasetDBVO> findDatasetByNames(List<String> dsNames) {
        return datasetAutoRepo.findByNameIn(dsNames);
    }
}
