package com.quicksand.bigdata.metric.management.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_metric_job_registry")
public class JobRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    int id;

    @Column(name = "registry_group")
    String registryGroup;

    @Column(name = "registry_key")
    String registryKey;

    @Column(name = "registry_value")
    String registryValue;

    @Column(name = "update_time")
    Date updateTime;

}
