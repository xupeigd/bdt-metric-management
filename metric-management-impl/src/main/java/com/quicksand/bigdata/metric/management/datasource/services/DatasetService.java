package com.quicksand.bigdata.metric.management.datasource.services;

import com.quicksand.bigdata.metric.management.datasource.models.DatasetModifyModel;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;

/**
 * DatasetService
 *
 * @author page
 * @date 2022/7/28
 */
public interface DatasetService {

    /**
     * 更新/新建dataset实体
     *
     * @param model 更新/新建参数
     * @return instance of DatasetVO
     */
    DatasetVO upsertDataset(DatasetModifyModel model) throws Throwable;

}
