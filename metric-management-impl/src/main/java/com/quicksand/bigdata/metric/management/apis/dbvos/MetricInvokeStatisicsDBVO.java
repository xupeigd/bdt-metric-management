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
 * MetricInvokeStatisicsDBVO
 * (指标调用统计)
 *
 * @author page
 * @date 2022/12/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_APIS_METRIC_INVOKE_STATISTICS)
@Where(clause = " status = 1 ")
public class MetricInvokeStatisicsDBVO {

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
     * 日期标识
     * （刷新截止点）
     */
    @Column(name = "date_flag", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '标识时间' ")
    Date dateFlag;

    /**
     * 记录时间点日期
     * （当日时间归零处理）
     */
    @Column(name = "log_date", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间点日期' ")
    Date logDate;

    /**
     * 额度值
     */
    @Column(columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '额度值' ")
    Long quota;

    /**
     * 当前配额消耗值
     * （累积）
     */
    @Column(name = "cur_cost", columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '当前配额消耗值（累积）' ")
    Long curCost;


    /**
     * 日计消耗值
     */
    @Column(name = "day_cost", columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '日计消耗值' ")
    Long dayCost;

    /**
     * 最高QPS
     * (天计)
     */
    @Column(name = "max_qps", columnDefinition = "bigint(11) NOT NULL DEFAULT 0 COMMENT '最高QPS(天计) ' ")
    Integer maxQps;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

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
