package com.quicksand.bigdata.metric.management.datasource.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * ClusterInfoDBVO
 *
 * @author page
 * @date 2022/7/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterInfoVO {

    Integer id;

    String name;

    String type;

    String address;

    String defaultDatabase;

    String defaultSchema;

    String userName;

    String password;

    String comment;

    Date createTime;

    Date updateTime;

    /**
     * 数据状态（逻辑删除）
     */
    DataStatus status;

}
