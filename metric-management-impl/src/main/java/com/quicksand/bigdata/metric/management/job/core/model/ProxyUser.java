package com.quicksand.bigdata.metric.management.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_metric_job_proxy_users")
public class ProxyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    int id;

    /**
     * 关联的用户Id
     */
    @Column(name = "user_id")
    int userId;

    /**
     * 角色：0-普通用户、1-管理员
     */
    @Column
    int role;

    /**
     * 权限：执行器ID列表，多个逗号分割
     */
    @Column
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
