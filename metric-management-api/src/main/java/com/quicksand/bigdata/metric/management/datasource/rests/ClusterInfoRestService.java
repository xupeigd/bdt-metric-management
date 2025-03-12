package com.quicksand.bigdata.metric.management.datasource.rests;

import com.quicksand.bigdata.metric.management.datasource.models.ClusterInfoModel;
import com.quicksand.bigdata.metric.management.datasource.models.TableColumnModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * ClusterInfoService
 *
 * @author page
 * @date 2022/7/26
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.ClusterInfoService:}",
        contextId = "ClusterInfoService")
public interface ClusterInfoRestService {

    /**
     * 获取集群信息
     *
     * @param keyword 简单的过滤key
     * @return List of ClusterInfoModel
     */
    @GetMapping("/datasource/clusters")
    Response<List<ClusterInfoModel>> queryClusterInfos(@RequestParam(required = false, defaultValue = "") String keyword);


    /**
     * 获取集群对应的表
     * <p>
     * （集群必须有默认的库）
     * （从db/dwh fetch信息）
     *
     * @return list of tableName
     */
    @GetMapping("/datasource/clusters/{clusterId}/tables")
    Response<List<String>> queryTables(@PathVariable("clusterId") @Min(value = 1L, message = "无效的的集群")
                                       @Max(value = Integer.MAX_VALUE, message = "无效的集群") int clusterId,
                                       @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) throws Exception;

    /**
     * 获取表的字段信息
     * <p>
     * （从db/dwh fetch信息）
     *
     * @param clusterId 集群id
     * @param tableName 表名称
     * @param keyword   检索关键字
     * @return list of DatasetColumnModel
     */
    @GetMapping("/datasource/clusters/{clusterId}/tables/{tableName}")
    Response<List<TableColumnModel>> queryColumns(@PathVariable("clusterId") @Min(value = 1L, message = "无效的的集群")
                                                  @Max(value = Integer.MAX_VALUE, message = "无效的集群") int clusterId,
                                                  @PathVariable("tableName") @NotBlank String tableName,
                                                  @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) throws Exception;


}
