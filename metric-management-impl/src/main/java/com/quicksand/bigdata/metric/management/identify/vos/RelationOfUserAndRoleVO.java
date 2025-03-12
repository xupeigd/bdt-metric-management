package com.quicksand.bigdata.metric.management.identify.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
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
public class RelationOfUserAndRoleVO {

    // RefIdOfUserAndRole id;

    Integer id;

    Integer createUserId;

    /**
     * 创建时间
     */
    Date createTime;

    Integer updateUserId;

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


}
