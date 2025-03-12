package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.PermissionOverviewModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * PermissionRestService
 *
 * @author page
 * @date 2020/8/18 14:04
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.PermissionRestService:}",
        contextId = "PermissionRestService")
public interface PermissionRestService {

    /**
     * 查询全系统支持的权限
     *
     * @return List of PermissionOverviewModel
     */
    @GetMapping("/identify/permissions")
    Response<List<PermissionOverviewModel>> queryAllPermissions();

    /**
     * 根据用户Id查询用户拥有的权限
     *
     * @param userId 用户Id
     * @return List of UserPermissionModel
     */
    @GetMapping("/identify/permissions/users/{userId}")
    Response<List<PermissionOverviewModel>> queryPermissionsByUserId(@PathVariable("userId") int userId);

    /**
     * 根据用户Id查询用户拥有的权限
     *
     * @param roleId 用户Id
     * @return List of UserPermissionModel
     */
    @GetMapping("/identify/permissions/roles/{roleId}")
    Response<List<PermissionOverviewModel>> queryPermissionsByRoleId(@PathVariable("roleId") int roleId);

}
