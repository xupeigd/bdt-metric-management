package com.quicksand.bigdata.metric.management.datasource.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DatasetVO
 *
 * @author page
 * @date 2022/8/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatasetVO {

    Integer id;

    String name;

    String tableName;

    String description;

    List<IdentifierVO> identifiers;

    UserVO owner;

    ClusterInfoVO cluster;

    List<DatasetColumnVO> columns;

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

    DataStatus status;

    MutabilityVO mutability;

}
