package com.quicksand.bigdata.metric.management.identify.models;

import com.quicksand.bigdata.metric.management.consts.UserStatus;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * UserStatusModifyModel
 *
 * @author page
 * @date 2020/8/18 14:40
 */
@Data
public class UserStatusModifyModel {

    /**
     * 预期的用户状态
     * <p>
     * 1 非活跃 2 活跃
     *
     * @see UserStatus
     */
    @NotNull(message = "请选择需要修改的状态！")
    @Min(value = 1, message = "请选择正确的状态！")
    @Min(value = 2, message = "请选择正确的状态！")
    private Integer status;

}
