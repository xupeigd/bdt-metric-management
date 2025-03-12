package com.quicksand.bigdata.metric.management.apis.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * AuthTokenDBVO
 *
 * @author page
 * @date 2022/10/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenVO {

    /**
     * 逻辑Id
     */
    Integer id;

    /**
     * 关联的应用Id
     */
    Integer appId;

    /**
     * 鉴权的token
     */
    String token;

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
     * 0删除 1 可用
     *
     * @see DataStatus
     */
    DataStatus status;

}
