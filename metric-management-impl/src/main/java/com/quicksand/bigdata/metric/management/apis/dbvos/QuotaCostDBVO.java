package com.quicksand.bigdata.metric.management.apis.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

/**
 * QuotaCostDBVO
 * (按照批次记录)
 *
 * @author page
 * @date 2022/12/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_APIS_QUOTA_COSTS,
        indexes = {
                @Index(name = "uniq_app_metric_lockdown", columnList = "app_id,metric_id,lock_down_flag", unique = true)
        })
@Where(clause = " status = 1 ")
public class QuotaCostDBVO {

    /**
     * 逻辑Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = " bigint(11) NOT NULL AUTO_INCREMENT COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 关联的应用Id
     */
    @Column(name = "app_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '应用Id' ")
    Integer appId;

    /**
     * 关联的指标Id
     */
    @Column(name = "metric_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '指标Id' ")
    Integer metricId;

    /**
     * lockdown标识 （正在使用的为空串，失效的为yyyy-MM-dd HH:mm:ss）
     */
    @Column(name = "lock_down_flag", columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT 'lockdown标识' ")
    String lockDownFlag;

    /**
     * 额度值
     */
    @Column(columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '额度值' ")
    Long quota;

    /**
     * 消耗值
     */
    @Column(columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '消耗值' ")
    Long cost;


    /**
     * 创建用户Id
     */
    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

    /**
     * 更新用户Id
     */
    @Column(name = "update_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '更新用户Id' ")
    Integer updateUserId;

    /**
     * 更新时间
     */
    @Column(name = "update_time", columnDefinition = " datetime  NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
    Date updateTime;

    /**
     * 状态
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1可用' ")
    @Enumerated(EnumType.ORDINAL)
    DataStatus status;
}
