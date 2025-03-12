package com.quicksand.bigdata.metric.management.identify.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * UserMixDetailModel
 *
 * @author page
 * @date 2020/8/18 14:47
 */
@Data
public class UserMixDetailModel {

    /**
     * 用户数据
     */
    UserOverviewModel userInfo;
    /**
     * 用户具备的角色数据
     */
    List<RoleOverviewModel> roles = new ArrayList<>();
    /**
     * 页面的权限数据
     */
    List<PermissionOverviewModel> pagePermissions = new ArrayList<>();
    /**
     * 操作权限聚集数据
     */
    List<PermissionGatherModel> operationPermissions = new ArrayList<>();

}
