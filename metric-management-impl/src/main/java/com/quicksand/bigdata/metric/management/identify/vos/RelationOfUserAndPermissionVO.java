package com.quicksand.bigdata.metric.management.identify.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.PermissionGrantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RelationOfUserAndRoleDBVO
 *
 * @author page
 * @date 2020/8/18 15:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationOfUserAndPermissionVO {

    Integer id;

    /**
     * 授权的类型
     * <p>
     * 0撤销授权 1授权
     */
    PermissionGrantType type;

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

    PermissionVO permission;

}
