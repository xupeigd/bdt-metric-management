package com.quicksand.bigdata.metric.management.apis.rest;

import com.quicksand.bigdata.metric.management.apis.models.AppDetailModel;
import com.quicksand.bigdata.metric.management.apis.models.AppModifyModel;
import com.quicksand.bigdata.metric.management.apis.models.AppToken;
import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

/**
 * AppRestService
 *
 * @author page
 * @date 2022/9/27
 */
@FeignClient(
        name = "${vars.name.ms.apis:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.AppManageRestService:}",
        contextId = "AppManageRestService")
public interface AppManageRestService {

    /**
     * 创建应用
     *
     * @param model 创建参数model
     * @return list of AppModel
     */
    @PostMapping("/apis/apps")
    Response<AppDetailModel> createApp(@Validated(Insert.class) @RequestBody AppModifyModel model);


    /**
     * 修改应用信息
     *
     * @param id    应用主键
     * @param model 修改参数model
     * @return instance of AppDetailModel
     */
    @PutMapping("/apis/apps/{id}")
    Response<AppDetailModel> modifyApp(@Parameter(name = "id", description = "应用主键") @Min(value = 1, message = "不支持的应用主键！")
                                       @PathVariable("id") int id, @Validated(Update.class) @RequestBody AppModifyModel model);

    /**
     * 更换app的访问token
     *
     * @param id 应用主键
     * @return token:string
     */
    @PutMapping("/apis/apps/{id}/token")
    Response<AppToken> exchangeAppToken(@Parameter(name = "id", description = "应用主键") @Min(value = 1, message = "不支持的应用主键！")
                                        @PathVariable("id") int id);


    /**
     * 删除应用
     *
     * @param id 应用主键
     * @return void
     */
    @DeleteMapping("/apis/apps/{id}")
    Response<Void> removeApp(@Parameter(name = "id", description = "应用主键") @Min(value = 1, message = "不支持的应用主键！")
                             @PathVariable("id") int id);


}
