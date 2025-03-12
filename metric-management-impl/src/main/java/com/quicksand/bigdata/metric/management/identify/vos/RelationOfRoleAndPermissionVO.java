package com.quicksand.bigdata.metric.management.identify.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RelationOfRoleAndPermissionDBVO
 *
 * @author page
 * @date 2020/8/18 16:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationOfRoleAndPermissionVO {

    // RefIdOfRoleAndPermission id;

    Integer id;

    /**
     * 创建时间
     */
    Date createTime;

    /**
     * 更新时间
     */
    Date updateTime;

    /**
     * 状态
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    DataStatus status;

    Integer createUserId;

    Integer updateUserId;

}
