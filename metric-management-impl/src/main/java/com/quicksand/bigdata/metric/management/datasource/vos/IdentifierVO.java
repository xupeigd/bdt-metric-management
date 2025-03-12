package com.quicksand.bigdata.metric.management.datasource.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.IdentifierType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * IdentifierVO
 *
 * @author page
 * @date 2022/8/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentifierVO {

    String name;

    IdentifierType type;

    String expr;

    /**
     * Composite key had
     */
    List<Integer> identifiers;

    Integer createUserId;

    /**
     * 创建时间
     */
    Date createTime;

    Integer updateUserId;

    /**
     * 更新时间
     */
    Date updateTime;

    /**
     * 状态
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    DataStatus status;

}
