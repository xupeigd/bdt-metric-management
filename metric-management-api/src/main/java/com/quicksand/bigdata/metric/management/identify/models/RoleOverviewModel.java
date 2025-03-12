package com.quicksand.bigdata.metric.management.identify.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RoleOverviewModel
 *
 * @author page
 * @date 2020/8/18 13:40
 * <p>
 * eg:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleOverviewModel {

    /*
     * {
     * "id": 10,
     * "name": "Admin",
     * "code": "admin"
     * }
     */

    /**
     * 角色id
     */
    Integer id;
    /**
     * 角色name
     */
    String name;
    /**
     * 角色code
     */
    String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    RoleType type;

    /**
     * 创建时间
     */
    Date createTime;

    /**
     * 更新时间
     */
    Date updateTime;

}
