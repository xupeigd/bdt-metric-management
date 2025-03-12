package com.quicksand.bigdata.metric.management.tools.vos;

import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.models.TableColumnModel;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricCatalogDBVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * MetricImportDBVO
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/31 16:14
 * @description
 */
@Data
@Builder
public class MetricImportDBVO {
    MetricImportModel metricImportModel;
    List<TableColumnModel> tableColumnModels;
    DatasetDBVO dataset;
    ClusterInfoDBVO clusterInfoDBVO;
    List<UserDBVO> businessOwners;
    List<UserDBVO> techOwners;
    MetricCatalogDBVO businessDomain;
    MetricCatalogDBVO topicDomain;

}
