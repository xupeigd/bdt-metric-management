package com.quicksand.bigdata.metric.management.datasource.dbvos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.IdentifierType;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import com.quicksand.bigdata.metric.management.datasource.converters.ListToIntegerConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * identifierDBVO
 *
 * @author page
 * @date 2022/8/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = TableNames.TABLE_DATASOURCE_DATASET_IDENTIFIERS,
        indexes = {
                @Index(name = "uniq_id", columnList = "id", unique = true),
                @Index(name = "uniq_name", columnList = "name", unique = true),
        })
@Embeddable
@Where(clause = " status = 1 ")
public class IdentifierDBVO {

    @Column(columnDefinition = " VARCHAR(64) NOT NULL DEFAULT '' COMMENT '名称' ")
    String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = " tinyint(2) NOT NULL DEFAULT 1 COMMENT '类型 0 primary 1 foreign 2 unique' ")
    IdentifierType type;

    @Column(columnDefinition = " varchar(128) NOT NULL DEFAULT '' COMMENT '表达式' ")
    String expr;

    /**
     * Composite key had
     */
    @Convert(converter = ListToIntegerConverter.class)
    @Column(columnDefinition = "VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Composite keys Identifier ' ")
    List<Integer> identifiers;

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

}
