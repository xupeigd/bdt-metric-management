package com.quicksand.bigdata.metric.management.engine;

import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.models.TableColumnModel;
import com.quicksand.bigdata.metric.management.datasource.vos.ClusterInfoVO;
import com.quicksand.bigdata.metric.management.engine.impls.EngineServiceImpl;

import java.util.List;

/**
 * EngineService
 *
 * @author page
 * @date 2022/8/12
 */
public interface EngineService {

    /**
     * 获取集群下库的表
     *
     * @param clusterInfo instance of ClusterInfoDBVO
     * @return list of String / empty list
     */
    List<String> queryTables(ClusterInfoDBVO clusterInfo) throws Exception;

    /**
     * 获取表的字段信息
     *
     * @param clusterInfo clusterInfo instance of ClusterInfoDBVO
     * @param tableName   表名称
     * @param keyword     模糊关键字
     * @return list of TableColumnModel / empty list
     */
    List<TableColumnModel> queryColumInfos(ClusterInfoDBVO clusterInfo, String tableName, String keyword) throws Exception;

    /**
     * 通用查询
     *
     * @param <T>
     * @param clusterInfo 集群信息
     * @param sql         sql
     * @param mapper      映射器
     * @return instnace of T / null
     */
    <T> T commonQuery(ClusterInfoDBVO clusterInfo, String sql, EngineServiceImpl.Mapper<T> mapper);

    /**
     * 通用查询
     *
     * @param <T>
     * @param clusterInfo 集群信息
     * @param sql         sql
     * @param mapper      映射器
     * @return instnace of T / null
     */
    <T> T commonQuery(ClusterInfoVO clusterInfo, String sql, EngineServiceImpl.Mapper<T> mapper);

}
