package com.quicksand.bigdata.metric.management.identify.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * UserOverviewModel
 *
 * @author page
 * @date 2020/8/18 13:54
 */
@Data
@Schema
public class UserOverviewModel {

    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 当前运作的角色Id
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer roleId;

    /**
     * 拥有的所有角色Id
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Integer> roleIds;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    UserStatus status;

}
