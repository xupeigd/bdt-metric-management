package com.quicksand.bigdata.metric.management.identify.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * RoleDBVO
 *
 * @author page
 * @date 2020/8/18 15:34
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoleVO {

    /**
     * 角色id
     */
    Integer id;

    /**
     * 角色名称
     */
    String name;

    /**
     * 角色code
     */
    String code;

    /**
     * 创建时间
     */
    Date createTime;

    /**
     * 更新时间
     */
    Date updateTime;

    /**
     * 角色的类型
     */
    RoleType type;

    /**
     * 状态
     * 0删除 1 可用
     *
     * @see DataStatus
     */
    DataStatus status;

    Integer createUserId;

    Integer updateUserId;

    /**
     * 赋予的权限
     */
    List<PermissionVO> permissions;

    /**
     * 撤销的权限
     */
    List<PermissionVO> revokePermissions;

}
