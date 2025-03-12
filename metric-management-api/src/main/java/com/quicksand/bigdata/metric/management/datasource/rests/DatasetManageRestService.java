package com.quicksand.bigdata.metric.management.datasource.rests;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetModifyModel;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetOverviewModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * DatasetManageRestService
 *
 * @author page
 * @date 2022/7/28
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.DatasetManageRestService:}",
        contextId = "DatasetManageRestService")
public interface DatasetManageRestService {

    /**
     * 创建dataset
     * （成功则回显）
     *
     * @param model 创建参数
     * @return instance of DatasetOverviewModel
     */
    @PostMapping("/datasource/datasets")
    Response<DatasetOverviewModel> createDataset(@RequestBody @Validated({Insert.class}) DatasetModifyModel model);

    /**
     * 编辑dataset
     * （成功则回显）
     *
     * @param datasetId 数据集合Id
     * @param model     修改参数
     * @return instance of DatasetOverviewModel
     */
    @PutMapping("/datasource/datasets/{datasetId}")
    Response<DatasetOverviewModel> modifyDataset(@PathVariable("datasetId") @Min(value = 0L, message = "不存在的数据集")
                                                 @Max(value = Integer.MAX_VALUE, message = "不存在的数据集") int datasetId,
                                                 @RequestBody @Validated({Update.class}) DatasetModifyModel model);

    /**
     * 删除数据集合
     *
     * @return void
     */
    @DeleteMapping("/datasource/datasets/{datasetId}")
    Response<Void> deleteDataset(@PathVariable("datasetId") @Min(value = 0L, message = "不存在的数据集")
                                 @Max(value = Integer.MAX_VALUE, message = "不存在的数据集") int datasetId);


}
