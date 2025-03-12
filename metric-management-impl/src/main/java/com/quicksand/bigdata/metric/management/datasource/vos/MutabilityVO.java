package com.quicksand.bigdata.metric.management.datasource.vos;

import com.quicksand.bigdata.metric.management.consts.Mutability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

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
public class MutabilityVO {

    /**
     * 可变类型
     */
    Mutability type;

    /**
     * 变化周期corn
     */
    String updateCron;

    /**
     * 变化标识列
     */
    String alongColumn;

}
