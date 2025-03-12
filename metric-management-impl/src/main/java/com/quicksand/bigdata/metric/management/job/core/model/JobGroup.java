package com.quicksand.bigdata.metric.management.job.core.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Data
@Entity
@Table(name = "tbl_metric_job_group")
public class JobGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    @Column(name = "app_name")
    String appname;

    @Column
    String title;

    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     */
    @Column(name = "address_type")
    int addressType;

    /**
     * 执行器地址列表，多地址逗号分隔(手动录入)
     */
    @Column(name = "address_list")
    String addressList;

    @Column(name = "update_time")
    Date updateTime;

    /**
     * 执行器地址列表(系统注册)
     */
    // registry list
    transient List<String> registryList;

    public List<String> getRegistryList() {
        if (addressList != null && addressList.trim().length() > 0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

}
