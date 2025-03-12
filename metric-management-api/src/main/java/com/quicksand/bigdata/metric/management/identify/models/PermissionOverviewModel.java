package com.quicksand.bigdata.metric.management.identify.models;

import com.quicksand.bigdata.vars.security.consts.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionOverviewModel
 *
 * @author page
 * @date 2020/8/18 14:05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionOverviewModel {

    /*
     * json eg：
     * {
     * "id": 101,
     * "name": "开通签约集团",
     * "code": "up0",
     * "type": "OPERATION",
     * "top": false,
     * "childs": [ ]
     * }
     */

    /**
     * 权限id
     */
    Integer id;
    /**
     * 权限名称
     */
    String name;
    /**
     * 权限code
     */
    String code;
    /**
     * 权限类型
     *
     * @see PermissionType
     */
    String type;
    /**
     * 是否顶级权限
     */
    Boolean top;
    /**
     * 子权限
     */
    List<PermissionOverviewModel> children = new ArrayList<>();
    /**
     * 路径 (Menu时有)
     */
    String path = "";

    /**
     * 所属模块
     */
    String module;

    Integer parentId;

    Integer serial;

}
