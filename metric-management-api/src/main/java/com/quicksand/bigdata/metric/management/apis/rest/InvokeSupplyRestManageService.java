package com.quicksand.bigdata.metric.management.apis.rest;

import com.quicksand.bigdata.metric.management.apis.models.InvokeApplyApproveModel;
import com.quicksand.bigdata.metric.management.apis.models.InvokeApplyDetailModel;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * InvokeSupplyRestManageService
 *
 * @author page
 * @date 2022/10/11
 */
@FeignClient(
        name = "${vars.name.ms.apis:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.InvokeSupplyRestManageService:}",
        contextId = "InvokeSupplyRestManageService")
public interface InvokeSupplyRestManageService {

    /**
     * 调用申请审核
     *
     * @return instance of InvokeApplyDetailModel
     */
    @PutMapping("/apis/invoke/applys/{id}")
    Response<InvokeApplyDetailModel> approve(@Parameter(name = "id", description = "申请记录id")
                                             @Min(value = 1L, message = "非法的申请Id")
                                             @PathVariable("id") int id,
                                             @Parameter(description = "审核参数")
                                             @NotNull
                                             @RequestBody InvokeApplyApproveModel model);

}
