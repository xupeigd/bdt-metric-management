package com.quicksand.bigdata.metric.management.tools.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FailedModel
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/12/16 15:45
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedModel {
    Integer rowNumber;
    String tableName;
    String cnName;
    String reason;
}