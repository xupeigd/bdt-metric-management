package com.quicksand.bigdata.metric.management.identify.vos;

import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * OperationLogVO
 *
 * @author page
 * @date 2020/8/26 12:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogVO {

    Long id;

    Date operationTime;

    String ip;

    String address;

    /**
     * 操作用户名
     */
    Integer userId;

    /**
     * 日志详情
     */
    String detail;

    /**
     * 分类类型
     */
    OperationLogType type;

}
