package com.quicksand.bigdata.metric.management.identify.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Permission
 *
 * @author page
 * @date 2020/8/18 15:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionVO {

    /**
     * 权限Id
     */
    Integer id;

    /**
     * 权限code
     */
    String code;

    /**
     * 权限名称
     */
    String name;

    /**
     * 排序
     */
    Integer serial;

    /**
     * 图标
     */
    String icon;

    /**
     * 权限的类型
     *
     * @see PermissionType
     */
    PermissionType type;

    /**
     * 父权限的Id
     */
    Integer parentId;

    /**
     * 数据状态（逻辑删除）
     */
    DataStatus status;

    Date createTime;

    Date updateTime;

    /**
     * 路径
     */
    String path;

    String module;

    /**
     * 子权限
     */
    List<PermissionVO> children;

}
