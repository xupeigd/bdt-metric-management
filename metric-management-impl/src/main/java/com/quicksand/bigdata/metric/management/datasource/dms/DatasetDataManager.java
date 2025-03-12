package com.quicksand.bigdata.metric.management.datasource.dms;

import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * DatasetDataManager
 *
 * @author page
 * @date 2022/7/28
 */
public interface DatasetDataManager {

    /**
     * 获取所有有效的数据集实体
     *
     * @return list of DatasetDBVO
     */
    Page<DatasetDBVO> findAllDatasets(@Nullable String namekeyword, List<Integer> clusterIds, List<Integer> ownerIds, Pageable pageReq);


    DatasetDBVO findDatasetById(int datasetId);

    void updateDataset(DatasetDBVO dataset);

    DatasetDBVO saveDataset(DatasetDBVO newInstance);

    DatasetDBVO findDatasetByName(String name);


    DatasetDBVO findFirstByName(String name, Integer clusterId);

    List<DatasetDBVO> findDatasetByNames(List<String> dsNames);

}
