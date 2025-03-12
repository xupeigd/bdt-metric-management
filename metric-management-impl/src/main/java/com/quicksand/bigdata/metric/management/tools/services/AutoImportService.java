package com.quicksand.bigdata.metric.management.tools.services;

import com.quicksand.bigdata.metric.management.tools.vos.ImportResult;
import com.quicksand.bigdata.metric.management.tools.vos.MetricImportModel;

import java.util.List;

/**
 * AutoImportService
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/31 09:34
 * @description
 */
public interface AutoImportService {

    ImportResult ImportMetricDataByList(List<MetricImportModel> importModels);

}
