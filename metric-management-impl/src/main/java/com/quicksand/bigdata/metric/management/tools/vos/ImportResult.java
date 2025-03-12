package com.quicksand.bigdata.metric.management.tools.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ImportResult
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
public class ImportResult {
    Integer successCount;
    Integer failedCount;
    List<FailedModel> failedList;

}
