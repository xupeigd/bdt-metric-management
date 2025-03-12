package com.quicksand.bigdata.metric.management.datasource.dbvos;

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
 * ClusterInfoDBVO
 *
 * @author page
 * @date 2022/7/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_CLUSTER_INFOS,
        indexes = {
                @Index(name = "uniq_name", columnList = "name", unique = true)
        })
@Where(clause = " status = 1 ")
public class ClusterInfoDBVO {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Integer id;

    @Column(length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '数据源名称' ")
    String name;

    @Column(length = 16, columnDefinition = " varchar(16) NOT NULL DEFAULT '' COMMENT '类型,eg: doris' ")
    String type;

    @Column(length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '连接地址 host:port格式' ")
    String address;

    @Column(name = "default_database", length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '默认数据库' ")
    String defaultDatabase;

    @Column(name = "default_schema", length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '默认schema' ")
    String defaultSchema;

    @Column(name = "user_name", length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '用户名' ")
    String userName;

    @Column(name = "password", length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '密码' ")
    String password;

    @Column(length = 64, columnDefinition = " varchar(255) NOT NULL DEFAULT '' COMMENT '数据源名称' ")
    String comment;

    @Column(name = "create_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ")
    Date createTime;

    @Column(name = "update_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间' ")
    Date updateTime;

    /**
     * 数据状态（逻辑删除）
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 0 COMMENT '数据状态 0 删除 1 可用' ")
    DataStatus status;

}
