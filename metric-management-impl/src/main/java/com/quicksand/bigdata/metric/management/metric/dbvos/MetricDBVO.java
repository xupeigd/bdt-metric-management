package com.quicksand.bigdata.metric.management.metric.dbvos;

import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.datasource.converters.StatisticPeriodListToStringConveter;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * MetricDBVO
 *
 * @author page
 * @date 2022-07-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_METRIC_METRICS,
        indexes = {
                @Index(name = "uniq_serial_number", columnList = "serial_number", unique = true),
                @Index(name = "idx_business_id", columnList = "business_id"),
                @Index(name = "idx_dataset_id", columnList = "dataset_id"),
                @Index(name = "idx_topic_id", columnList = "topic_id"),
        })
@Where(clause = " status = 1")
public class MetricDBVO {

    public static final int QUOTA_PERIOD_MONTH = 1;
    public static final int QUOTA_PERIOD_WEEK = 2;
    public static final int QUOTA_PERIOD_DAY = 3;
    public static final int QUOTA_PERIOD_HOUR = 4;

    public static final String CRON_MONTH = "0 0 0 1 */1 *";
    public static final String CRON_WEEK = "0 0 0 * * 1";
    public static final String CRON_DAY = "0 0 0 */1 * *";
    public static final String CRON_HOUR = "0 0 */1 * * *";


    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    /**
     * 指标编号
     */
    @Column(name = "serial_number", length = 32, columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '指标编号' ")
    String serialNumber;

    /**
     * 英文名称
     */
    @Column(name = "en_name", length = 256, columnDefinition = " VARCHAR(256) NOT NULL DEFAULT '' COMMENT '英文名称' ")
    String enName;

    /**
     * 中文名称
     */
    @Column(name = "cn_name", length = 512, columnDefinition = " VARCHAR(512) NOT NULL DEFAULT '' COMMENT '中文名称' ")
    String cnName;

    /**
     * 数据集数据
     */
    @ManyToOne(targetEntity = DatasetDBVO.class)
    @JoinColumn(name = "dataset_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11)  COMMENT '数据集id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    DatasetDBVO dataset;

    /**
     * 主题域实体
     */
    @ManyToOne(targetEntity = MetricCatalogDBVO.class)
    @JoinColumn(name = "topic_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11)  COMMENT '主题域id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    MetricCatalogDBVO topicDomain;

    /**
     * 业务域实体
     */
    @ManyToOne(targetEntity = MetricCatalogDBVO.class)
    @JoinColumn(name = "business_id",
            referencedColumnName = "id",
            columnDefinition = " bigint(11)  COMMENT '业务域id' ",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    MetricCatalogDBVO businessDomain;

    /**
     * 指标级别
     * <p>
     * T0，T1
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "metric_level", columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT '指标等级 0 T0 1 T1' ")
    MetricLevel metricLevel;

    /**
     * 数据安全级别
     * <p>
     * 0/1/2
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "data_security_level", columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT '安全等级 0 S0 1 S1 2 S2' ")
    DataSecurityLevel dataSecurityLevel;

    /**
     * 指标别名
     */
    @Column(name = "en_alias", length = 256, columnDefinition = " VARCHAR(256) NOT  NULL DEFAULT '' COMMENT '英文别名' ")
    String enAlias;

    /**
     * 指标中文别名
     */
    @Column(name = "cn_alias", length = 512, columnDefinition = " VARCHAR(512) NOT  NULL DEFAULT '' COMMENT '中文别名' ")
    String cnAlias;

    /**
     * 数据类型
     */
    @Column(name = "data_type", columnDefinition = " varchar(16)  NULL DEFAULT '' COMMENT '数据类型' ")
    String dataType;

    /**
     * 统计周期
     */
    @Convert(converter = StatisticPeriodListToStringConveter.class)
    @Column(name = "statistic_periods", columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '统计周期' ")
    List<StatisticPeriod> statisticPeriods;

    /**
     * 加工逻辑
     */
    @Column(name = "process_logic", length = 1024, columnDefinition = " text COMMENT '加工逻辑' ")
    String processLogic;

    /**
     * 指标说明
     */
    @Column(name = "description", length = 1024, columnDefinition = " text COMMENT '指标说明' ")
    String description;

    /**
     * 业务负责人
     */
    @ManyToMany(targetEntity = UserDBVO.class)
    @JoinTable(name = TableNames.TABLE_METRIC_REL_BUSINESS_OWNER,
            joinColumns = {@JoinColumn(name = "metric_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")},
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    List<UserDBVO> businessOwners;

    /**
     * 技术负责人
     */
    @ManyToMany(targetEntity = UserDBVO.class)
    @JoinTable(name = TableNames.TABLE_METRIC_REL_TECH_OWNER,
            joinColumns = {@JoinColumn(name = "metric_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")},
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    List<UserDBVO> techOwners;

    /**
     * 度量名称
     */
    @Column(name = "measure", length = 128, columnDefinition = " varchar(128) NOT NULL DEFAULT '' COMMENT '对应度量名称' ")
    String measure;

    /**
     * 上线/下线
     * <p>
     * 1 上线 0 下线
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "pub_sub", columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 COMMENT '发布状态 0 下线 1 上线' ")
    PubsubStatus pubsub;

    @Column(name = "create_user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '创建用户Id' ")
    Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

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
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据状态 0删除 1 可用' ")
    DataStatus status;

    @ElementCollection
    @CollectionTable(name = TableNames.TABLE_METRIC_MEASURES,
            joinColumns = @JoinColumn(name = "metric_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    List<MetricMeasureDBVO> measures;

    @ElementCollection
    @CollectionTable(name = TableNames.TABLE_METRIC_DIMENSIONS,
            joinColumns = @JoinColumn(name = "metric_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    List<MetricDimensionDBVO> dimensions;

    /**
     * 指标表达式
     */
    @Column(name = "metric_expr", columnDefinition = " varchar(256)  NULL DEFAULT ''  COMMENT '指标表达式' ")
    String metricExpr;

    /**
     * 指标表达式
     */
    @Column(name = "metric_type", columnDefinition = " varchar(64)  NULL DEFAULT ''  COMMENT '指标metricFLow类型' ")
    String metricType;

    /**
     * 指标表达式
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "cluster_type", columnDefinition = " tinyint(2) NOT NULL DEFAULT 0 COMMENT '指标等级 0 离线， 1 实时' ")
    ClusterType clusterType;

    /**
     * 指标的默认配额
     * <p>
     * 默认 1000L
     */
    @Column(name = "default_quota", columnDefinition = " bigint(20) NOT NULL DEFAULT 1000 COMMENT '指标的默认配额' ")
    Long defaultQuota;

    /**
     * 默认配额的刷新周期
     * <p>
     * 1月 2周 3日 4小时 默认 1月
     */
    @Column(name = "default_quota_period", columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '默认配额刷新周期 1月 2周 3日 4小时 默认 月' ")
    Integer defaultQuotaPeriod;

    /**
     * 默认配额刷新cron
     * <p>
     * 默认
     */
    @Column(name = "default_cronn_express", columnDefinition = " varchar(24) NOT NULL DEFAULT '' COMMENT '默认配额刷新cron' ")
    String defaultCronExpress;

    /**
     * 指标聚合类型
     * 0:原子指标，1:衍生指标，2:复合指标
     * 默认0
     */
    @Column(name = "metric_aggregation_type", columnDefinition = " tinyint DEFAULT 0 NOT NULL COMMENT '指标聚合类型(0:原子指标，1:衍生指标，2:复合指标)' ")
    MetricAggregation metricAggregationType;


    /**
     * 指标是否可累积
     * 0:不可，1:可累加
     * 默认0
     */
    @Column(name = "metric_accumulative", columnDefinition = " tinyint DEFAULT 0 NOT NULL COMMENT  '指标是否可累积(0:不可，1:可累加)' ")
    WhetherStatus metricAccumulative;

    /**
     * 指标是否被认证
     * 指标是否被认证(0:非，1:是)
     * 默认0
     */
    @Column(name = "metric_authentication", columnDefinition = " tinyint DEFAULT 0 NOT NULL COMMENT  '' COMMENT '指标是否被认证(0:非，1:是)' ")
    WhetherStatus metricAuthentication;


}
