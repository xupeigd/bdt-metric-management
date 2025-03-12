package com.quicksand.bigdata.metric.management.metric.services;

import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.models.*;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricMergeSegment;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * MetricCatalogService
 *
 * @author zhaoxin3
 * @date 2022-08-08
 */
public interface MetricService {


    /**
     * @param metricId 指标id
     * @return com.quicksand.bigdata.metric.management.metric.models.MetricDetailModel
     * @author zhao_xin
     * @description 获取指标详情
     **/
    MetricDetailModel findMetricById(int metricId);

    /**
     * @param metricId 指标id
     * @return com.quicksand.bigdata.metric.management.metric.models.MetricSegmentModel
     * @author zhao_xin
     * @description 获取指标模型片段
     **/
    MetricSegmentModel findMetricYamlSegmentById(int metricId);


    /**
     * 验证yaml内容的正确性
     *
     * @param metricInsertModel 验证内容
     * @param buildSqlContent   获取sql内容
     * @return List of MetricCatalogVO / emptyList
     */
    MetricMergeSegment buildMetricMergeSegment(MetricInsertModel metricInsertModel, Boolean buildSqlContent);


    /**
     * 更新/新建指标实体
     *
     * @param model 更新/新建参数
     * @return instance of MetricVO
     */
    Response<MetricDetailModel> upsertMetric(MetricInsertModel model);


    /**
     * @param metricId 指标id
     * @return string
     * @author zhao_xin
     * @description 根据指标id获取指标执行sql
     **/
    String getMetricQuerySql(Integer metricId);

    /**
     * @param queryModel 查询信息
     * @param pageable   分页信息
     * @return Page<MetricOverviewModel>
     * @author zhao_xin
     * @description
     **/
    PageImpl<MetricOverviewModel> queryAllMetrics(MetricQueryModel queryModel, Pageable pageable);

    /**
     * @param metricId 指标id
     * @return java.lang.Void
     * @author zhao_xin
     * @description 逻辑删除指标
     **/
    Void markDeleteByMetricId(Integer metricId);

    /**
     * @param metricId        指标id
     * @param newPubsubStatus 目标新发布状态
     * @param quotaModel
     * @return com.quicksand.bigdata.metric.management.consts.PubsubStatus
     * @author zhao_xin
     * @description 修改指标上下线状态
     **/
    PubsubStatus modifyMetricPubsubStatus(Integer metricId, PubsubStatus newPubsubStatus, MetricQuotaModel quotaModel);


    // Boolean verifyMetricSerialNumber(MetricModifyModel metricModifyModel);

    /**
     * @param metricMergeSegment 指标聚合对象
     * @param metricModel        指标存储对象
     * @return
     * @author zhao_xin
     * @description 为指标追究维度、度量等信息
     **/
    void appendMetricDimsAndMeasures(MetricMergeSegment metricMergeSegment, MetricDBVO metricModel);

    /**
     * 将MetricYamlBuilderModel转化为用户配置yaml
     * 为流程创建指标时生产yaml文件
     *
     * @param metricYamlBuilderModel 实体
     * @return String
     */
    String buildYamlContentFromUserModel(MetricYamlBuilderModel metricYamlBuilderModel);

    List<MetricVO> findMetricByIds(List<Integer> metricIds);

    /**
     * @param metricDBVO  指标实体
     * @param segmentDBVO segment实体
     * @return java.lang.String
     * @author zhao_xin
     * @description 根据数据库中记录组合生成最新指标展示yaml(数据源更新或者目录信息更新)
     **/
    String buildUserViewYamlContentFromDB(MetricDBVO metricDBVO, SegmentDBVO segmentDBVO);

    /**
     * @return List<DropDownListModel>
     * @author zhao_xin
     * @description 获取应用可选指标下拉列表
     **/
    List<DropDownListModel> getAppMetricsDropDownList(int appId);

    /**
     * @return List<DropDownListModel>
     * @author zhao_xin
     * @description 获取指标关联应用信息
     **/
    PageImpl<AppInvokeDetailModel> findAppInvokeInfoByMetricId(int metricId, Pageable pageable);

    /**
     * @param topDomainId      主题域
     * @param businessDomainId 业务域
     * @return java.lang.String
     * @author zhao_xin
     * @description 根据主题域和业务域自动生成指标编码
     **/
    String getMetricSerialNumber(int topDomainId, int businessDomainId);

    /**
     * @param name         指标英文名
     * @param serialNumber 指标编码
     * @return MetricVO
     * @author zhao_xin
     * @description 根据指标英文名或者唯一编号查询指标
     **/
    MetricVO findMetricByEnNameOrSerialNumber(String name, String serialNumber);

    /**
     * @param name 指标英文名
     * @return MetricVO
     * @author zhao_xin
     * @description 根据指标英文名查询指标
     **/
    MetricVO findMetricByName(String name);

    /**
     * @param serialNumber 指标编码
     * @return jMetricVO
     * @author zhao_xin
     * @description 根据指标唯一编号查询指标
     **/
    MetricVO findMetricBySerialNumber(String serialNumber);

    /**
     * @param metricId 根据指标
     * @return List<MetricSegmentVersionModel>
     * @author zhao_xin
     * @description 获取指标id下所有历史版本内容
     **/
    List<MetricSegmentVersionModel> getMetricSegmentAllVersion(int metricId);

}
