package com.quicksand.bigdata.metric.management.datasource.dbvos;

import com.quicksand.bigdata.metric.management.consts.Mutability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * MutabilityDBVO
 * (可变性配置)
 *
 * @author page
 * @date 2022/8/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MutabilityDBVO {

    /**
     * 可变类型
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 COMMENT '数据可变性 0亘古不变 1全量变化 2仅追加' ")
    Mutability type;

    /**
     * 变化周期corn
     */
    @Column(name = "update_corn", columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '变化周期corn' ")
    String updateCron;

    /**
     * 变化标识列
     */
    @Column(name = "along_column", columnDefinition = " VARCHAR(32) NOT NULL DEFAULT '' COMMENT '变化周期corn' ")
    String alongColumn;

}
