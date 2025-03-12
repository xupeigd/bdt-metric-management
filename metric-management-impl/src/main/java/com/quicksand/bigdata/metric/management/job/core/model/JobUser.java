package com.quicksand.bigdata.metric.management.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobUser {

    int id;

    /**
     * 账号
     */
    String username;

    /**
     * 密码
     */
    String password;

    /**
     * 角色：0-普通用户、1-管理员
     */
    int role;

    /**
     * 权限：执行器ID列表，多个逗号分割
     */
    String permission;

    // plugin
    public boolean validPermission(int jobGroup) {
        if (this.role == 1) {
            return true;
        } else {
            if (StringUtils.hasText(this.permission)) {
                for (String permissionItem : this.permission.split(",")) {
                    if (String.valueOf(jobGroup).equals(permissionItem)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
