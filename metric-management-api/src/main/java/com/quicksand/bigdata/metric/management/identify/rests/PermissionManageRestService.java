package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.PermissionOverviewModel;
import com.quicksand.bigdata.metric.management.identify.models.UserPermissionModifyModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * PermissionManageRestService
 *
 * @author page
 * @date 2020/8/18 14:29
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.PermissionManageRestService:}",
        contextId = "PermissionManageRestService")
public interface PermissionManageRestService {

    /**
     * 修改用户的授权情况
     *
     * @param userId 用户Id
     * @param model  权限Ids
     * @return List of UserPermissionModel
     */
    @PutMapping("/identify/permissions/users/{userId}")
    Response<List<PermissionOverviewModel>> modifyPermissionsByUser(@PathVariable("userId") int userId,
                                                                    @RequestBody UserPermissionModifyModel model);

}
