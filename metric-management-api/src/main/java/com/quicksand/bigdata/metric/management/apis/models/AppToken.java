package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * AppModel
 *
 * @author page
 * @date 2022/9/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppToken {
    /**
     * 逻辑Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    /**
     * 关联的应用Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer appId;

    /**
     * 鉴权的token
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String token;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer createUserId;

    /**
     * 创建时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date createTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer updateUserId;

    /**
     * 更新时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date updateTime;

}
