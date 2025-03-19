package com.quicksand.bigdata.metric.management.metric.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dbvos.IdentifierDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetColumnModel;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetOverviewModel;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetColumnVO;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.datasource.vos.IdentifierVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricCatalogDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDimensionDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricMeasureDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricCatalogDataManager;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.*;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.services.MetricTransformService;
import com.quicksand.bigdata.metric.management.metric.services.MetricUtil;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.tools.services.CacheService;
import com.quicksand.bigdata.metric.management.utils.*;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;
import com.quicksand.bigdata.metric.management.yaml.dms.SegmentDataManager;
import com.quicksand.bigdata.metric.management.yaml.services.YamlService;
import com.quicksand.bigdata.metric.management.yaml.services.impls.MetricFileService;
import com.quicksand.bigdata.metric.management.yaml.vos.*;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * MetricServiceImpl
 *
 * @author zhaoxin3
 * @date 2022-08-08
 */
@Service
@Slf4j
public class MetricServiceImpl
        implements MetricService {

    @Resource
    MetricDataManager metricDataManager;
    @Resource
    YamlService yamlService;
    @Resource
    UserDataManager userDataManager;
    @Resource
    MetricCatalogDataManager metricCatalogDataManager;
    @Resource
    DatasetDataManager datasetDataManager;
    @Resource
    SegmentDataManager segmentDataManager;
    @Resource
    MetricFileService metricFileService;
    @Resource
    MetricTransformService metricTransformService;
    @Resource
    CacheService cacheService;

    @Value("${metricflow.enable}")
    boolean metricflowEnable;

    private static void inspectDimPartInfo(MetricMergeSegment metricMergeSegment, Map<String, List<DatasetColumnVO>> allColumnMap, Set<String> identifierSet, Set<String> measureNameSet) {
        Set<String> dimNameSet = new HashSet<>();
        for (DimensionsSegment.UserDimension dimension : metricMergeSegment.getDimensions()) {
            //返回类型检查
            //Assert.isTrue(StringUtils.hasText(dimension.getData_type()), String.format("维度名称%s缺失返回值类型", dimension.getName()));
            //维度描述检查
            Assert.isTrue(StringUtils.isNotBlank(dimension.getDescription()), String.format("维度名称%s缺失描述信息", dimension.getName()));
            //名称格式检查
            Assert.isTrue(Pattern.matches(YamlSegmentKeys.YAML_NAME_RULE, dimension.getName()), String.format("yaml 维度名称%s格式错误,请参考(?!.*__).*^[a-z][a-z0-9_]*[a-z0-9]$", dimension.getName()));
            //名称重复检查
            Assert.isTrue(!dimNameSet.contains(dimension.getName()), String.format("维度名称%s重复", dimension.getName()));
            dimNameSet.add(dimension.getName());

            //数据源主外键同名检查，影响sql生成
            Assert.isTrue(!identifierSet.contains(dimension.getName()), String.format("维度(dimension)名称与数据源主外键%s重复,建议使用expr别名", dimension.getName()));
            //measures和dim名称重复检查
            Assert.isTrue(!measureNameSet.contains(dimension.getName()), String.format("measures名称%s与dimensions重复", dimension.getName()));
            //时间维度检查
            DimType byValue = DimType.getByValue(dimension.getType());
            Assert.notNull(byValue, "维度类型错误，取值范围" + Arrays.toString(DimType.values()));
            if (Objects.equals(DimType.time, byValue)) {
                Assert.notNull(dimension.getType_params(), "时间维度需要配置 type_params");
                Assert.notNull(dimension.getType_params().getIs_primary(), "时间维度需要配置 type_params.is_primary");
            } else {
                //无表达式时，维度名称必须为表中字段
                if (dimension.getExpr() == null) {
                    String name = dimension.getName();
                    Assert.isTrue(allColumnMap.containsKey(name), "如没使用expr，维度名称必须取表中字段名称。异常维度名称：" + dimension.getName());
                }
            }
        }
    }

    @Override
    public MetricDetailModel findMetricById(int metricId) {
        MetricDBVO metricDBVO = metricDataManager.findByMetricId(metricId);
        Assert.notNull(metricDBVO, "指标不存在或已删除");
        MetricDetailModel model = JsonUtils.transfrom(metricDBVO, MetricDetailModel.class);
        List<MetricMeasureDBVO> measures = metricDBVO.getMeasures();
        List<MetricColumnsModel> measureColumns = measures.stream()
                .map(m -> {
                    DatasetColumnModel columnModel = new DatasetColumnModel();
                    columnModel.setName(m.getName());
                    columnModel.setType(m.getDataType());
                    columnModel.setDataset(JsonUtils.transfrom(metricDBVO.getDataset(), DatasetOverviewModel.class));
                    return MetricColumnsModel.builder()
                            .columnModel(columnModel)
                            .description(m.getDescription())
                            .aggregationType(m.getAgg().getYamlValue())
                            .build();
                })
                .collect(Collectors.toList());
        List<MetricDimensionDBVO> dimensions = metricDBVO.getDimensions();
        List<MetricColumnsModel> dimColumns = dimensions.stream()
                .map(m -> {
                    DatasetColumnModel columnModel = new DatasetColumnModel();
                    columnModel.setName(m.getName());
                    columnModel.setType(m.getDataType());
                    columnModel.setDataset(JsonUtils.transfrom(metricDBVO.getDataset(), DatasetOverviewModel.class));
                    return MetricColumnsModel.builder()
                            .columnModel(columnModel)
                            .description(m.getDescription())
                            .build();
                })
                .collect(Collectors.toList());
        List<SegmentDBVO> segmentDBVOList = segmentDataManager.findSegmentByMetricId(YamlSegmentType.Metric, metricId);
        Assert.isTrue(!CollectionUtils.isEmpty(segmentDBVOList), "指标片段不存在或已删除");
        String yamlSegment = buildUserViewYamlContentFromDB(metricDBVO, segmentDBVOList.get(0));
        model.setDimColumns(dimColumns);
        model.setMeasureColumns(measureColumns);
        model.setYamlSegment(yamlSegment);
        return model;
    }

    @Override
    public MetricSegmentModel findMetricYamlSegmentById(int metricId) {
        MetricDBVO metricDBVO = metricDataManager.findByMetricId(metricId);
        Assert.notNull(metricDBVO, "指标不存在或已删除");
        //SegmentVO metricSegment = yamlService.getLastEnableSegmentByMetricIdAndType(YamlSegmentType.Metric, metricId);

        List<SegmentDBVO> segmentDBVOList = segmentDataManager.findSegmentByMetricId(YamlSegmentType.Metric, metricId);
        Assert.isTrue(!CollectionUtils.isEmpty(segmentDBVOList), "指标片段不存在或已删除");
        MetricSegmentModel model = new MetricSegmentModel();
        model.setId(metricId);
        model.setDatasetId(segmentDBVOList.get(0).getDataset().getId());
        String yamlSegment = this.buildUserViewYamlContentFromDB(metricDBVO, segmentDBVOList.get(0));
        model.setYamlSegment(yamlSegment);
        return model;
    }

    /**
     * @param metricDBVO  指标实体
     * @param segmentDBVO segment实体
     * @return java.lang.String
     * @author zhao_xin
     * @description 根据数据库中记录组合生成最新指标展示yaml(数据源更新或者目录信息更新)
     **/
    @Override
    public String buildUserViewYamlContentFromDB(MetricDBVO metricDBVO, SegmentDBVO segmentDBVO) {
        String oldYamlSegment = MetricYamlContentUtil.encodeYamlContent(segmentDBVO.getContent());
        MetricMergeSegment metricMergeSegment = MetricMergeSegment.builder().verifySuccess(false).build();
        this.inspectYamlFormat(oldYamlSegment);
        metricMergeSegment = metricTransformService.convertYamlToSegment(oldYamlSegment);
        DataSourceSegment dataSourceSegment = metricMergeSegment.getData_source();
        //数据源名称按最新数据集名称更新
        DatasetDBVO dataset = metricDBVO.getDataset();
        DatasetSegment datasetSegment = yamlService.getDatasetSegment(JsonUtils.transfrom(dataset, DatasetVO.class));
        datasetSegment.setName(dataset.getName());
        dataSourceSegment.setData_source(datasetSegment);
        DimensionsSegment dimensionsSegment = DimensionsSegment.builder().dimensions(metricMergeSegment.getDimensions()).build();
        MeasuresSegment measuresSegment = MeasuresSegment.builder().measures(metricMergeSegment.getMeasures()).build();
        MetricSegment.UserMetric metric = metricMergeSegment.getMetric();
        //更新主题域名、业务域名、技术负责人、业务负责人
        metric.setBusiness(metricDBVO.getBusinessDomain().getName());
        metric.setTheme(metricDBVO.getTopicDomain().getName());
        metric.setBusiness_owners(metricDBVO.getBusinessOwners().stream().map(UserDBVO::getName).collect(Collectors.toList()));
        metric.setTech_owners(metricDBVO.getTechOwners().stream().map(UserDBVO::getName).collect(Collectors.toList()));
        MetricSegment metricSegment = MetricSegment.builder().metric(metric).build();
        return MetricUtil.getCombineSegment(dataSourceSegment, dimensionsSegment, measuresSegment, metricSegment);
    }

    /**
     * @param insertModel     指标片段对象
     * @param buildSqlContent 是否为对象生产sql内容
     * @return com.quicksand.bigdata.metric.management.yaml.vos.MetricMergeSegment
     * @author zhao_xin
     * @description 创建MetricMergeSegment实体
     **/
    @Override
    public MetricMergeSegment buildMetricMergeSegment(MetricInsertModel insertModel, Boolean buildSqlContent) {
        StopWatch stopWatch = new StopWatch();
        MetricMergeSegment metricMergeSegment = MetricMergeSegment.builder().verifySuccess(false).build();
        //参数检查
        if (StringUtils.isBlank(insertModel.getYamlSegment())) {
            return metricMergeSegment;
        }
        stopWatch.start("基本格式检查");
        //特殊字符转义
        String encodeYamlSegment = MetricYamlContentUtil.encodeYamlContent(insertModel.getYamlSegment());
        //yaml基本格式检查
        boolean baseCheck = inspectYamlFormat(encodeYamlSegment);
        if (!baseCheck) {
            return metricMergeSegment;
        }
        //转换对象
        metricMergeSegment = metricTransformService.convertYamlToSegment(encodeYamlSegment);

        //数据信息检查
        stopWatch.stop();
        stopWatch.start("数据信息检查");
        //数据源匹配
        Assert.isTrue(StringUtils.isNotBlank(metricMergeSegment.getData_source().getData_source().getName()), "数据源名称不能为空");
        DatasetDBVO datasetByName = datasetDataManager.findDatasetByName(metricMergeSegment.getData_source().getData_source().getName());
        Assert.notNull(datasetByName, "数据源不存在");

        DatasetVO datasetVO = JsonUtils.transfrom(datasetByName, DatasetVO.class);
        //构建信息存储对象
        MetricDBVO metricDBVO = MetricDBVO.builder().dataset(datasetByName).build();
        metricMergeSegment.setTmpMetric(metricDBVO);

        //内容正确性检查
        stopWatch.stop();
        stopWatch.start("内容检查");
        inspectYamlKVAndBuildTmpMetricModel(datasetVO, metricMergeSegment, insertModel);
        //如果开启执行验证，开始执行验证
        if (buildSqlContent) {
            stopWatch.stop();
            stopWatch.start("metricFlow执行检查");
            DatasetSegment datasetSegment = yamlService.getDatasetSegment(datasetVO);
            String explainSql = getExplainSql(datasetVO, datasetSegment, metricMergeSegment);
            Assert.notNull(explainSql, "底层执行生成sql异常");
        }
        metricMergeSegment.setVerifySuccess(true);
        stopWatch.stop();
        log.info("verifyYamlMergeSegment 方法执行分析：{}", StopWatchUtils.getAllTaskInfo(stopWatch));
        return metricMergeSegment;
    }

    /**
     * @param metricMergeSegment 容器
     * @author zhao_xin
     * @description 检查配置项是否符合并填充临时MetricDBVO对象
     **/
    private void inspectYamlKVAndBuildTmpMetricModel(DatasetVO datasetVO, MetricMergeSegment metricMergeSegment, MetricInsertModel insertModel) {
        Map<String, List<DatasetColumnVO>> allColumnMap = datasetVO.getColumns().stream().collect(Collectors.groupingBy(DatasetColumnVO::getName));
        Set<String> identifierSet = datasetVO.getIdentifiers().stream().map(IdentifierVO::getName).collect(Collectors.toSet());
        //度量检查
        Set<String> measureNameSet = inspectMeasurePartInfo(metricMergeSegment, allColumnMap, identifierSet);

        //维度检查
        inspectDimPartInfo(metricMergeSegment, allColumnMap, identifierSet, measureNameSet);

        //指标信息检查
        inspectMetricPartInfo(metricMergeSegment, insertModel, measureNameSet);
    }

    /**
     * @param metricMergeSegment 检查对象
     * @param insertModel        新增或更新yaml对象
     * @param measureNameSet     上游已认证过的度量信息
     * @return
     * @author zhao_xin
     * @description 指标信息正确性检查
     **/
    private void inspectMetricPartInfo(MetricMergeSegment metricMergeSegment, MetricInsertModel insertModel, Set<String> measureNameSet) {
        MetricDBVO tmpMetric = metricMergeSegment.getTmpMetric();
        MetricSegment.UserMetric metricPart = metricMergeSegment.getMetric();
        //名称检查
        Assert.isTrue(Pattern.matches(YamlSegmentKeys.YAML_NAME_RULE, metricPart.getName()), "yaml指标名称格式错误，请参考(?!.*__).*^[a-z][a-z0-9_]*[a-z0-9]$");
        tmpMetric.setEnName(metricPart.getName());
        //指标类型检查
        MetricType metricType = MetricType.getByValue(metricPart.getType());
        Assert.notNull(metricType, "指标类型错误，取值范围" + Arrays.toString(MetricType.values()));
        tmpMetric.setMetricType(metricType.getYamlValue());

        //指标type_params检查
        MetricSegment.TypeParams type_params = metricPart.getType_params();
        Assert.notNull(type_params, "指标type_params不能为空");
        Assert.isTrue(null == type_params.getMeasures() ^ null == type_params.getMeasure(), "yaml指标measure与measures需要选择其中一个");
        //指标度量应用信息检查
        //汇总所需要的度量
        List<String> metricUseMeasureList = new ArrayList<>();
        if (type_params.getMeasures() != null) {
            metricUseMeasureList.addAll(type_params.getMeasures());
        }
        if (type_params.getMeasure() != null) {
            metricUseMeasureList.add(type_params.getMeasure());
        }
        Assert.isTrue(!CollectionUtils.isEmpty(metricUseMeasureList), "指标信息解析异常，请填写指标度量信息");
        //指标所用度量名称必须都在度量中声明过
        for (String measureName : metricUseMeasureList) {
            //保证每个指标的度量都在度量列表中声明过
            Assert.isTrue(measureNameSet.contains(measureName), String.format("指标使用的度量%s不在度量配置中", measureName));
        }
        tmpMetric.setMeasure(metricUseMeasureList.toString());


        //指标等级检查
        MetricLevel metricLevel = MetricLevel.getByFlag(metricPart.getMetric_level());
        Assert.notNull(metricLevel, "指标等级metric_level错误，取值范围" + Arrays.toString(MetricLevel.values()));
        tmpMetric.setMetricLevel(metricLevel);

        //指标安全等级检查
        DataSecurityLevel dataSecurityLevel = DataSecurityLevel.getByFlag(metricPart.getData_security_level());
        Assert.notNull(dataSecurityLevel, "指标安全类型data_security_level错误，取值范围" + Arrays.toString(DataSecurityLevel.values()));
        tmpMetric.setDataSecurityLevel(dataSecurityLevel);
        //指标中文名检查
        Assert.isTrue(StringUtils.isNotBlank(metricPart.getCn_name()), "指标中文名称不能为空");
        tmpMetric.setCnName(metricPart.getCn_name());

        //指标英文名、中文名唯一性检查
        Assert.isTrue(this.isUniqueName(insertModel, metricPart.getName(), metricPart.getCn_name()), "指标中、英文名称重复");

        //可空默认空字符串
        tmpMetric.setDataType(StringUtils.defaultIfBlank(metricPart.getData_type(), StringUtils.EMPTY));
        tmpMetric.setEnAlias(StringUtils.defaultIfBlank(metricPart.getAlias(), StringUtils.EMPTY));
        tmpMetric.setCnAlias(StringUtils.defaultIfBlank(metricPart.getCn_alias(), StringUtils.EMPTY));
        tmpMetric.setMetricExpr(StringUtils.defaultIfBlank(metricPart.getType_params().getExpr(), StringUtils.EMPTY));

        //指标编码检查
        Assert.isTrue(StringUtils.isNotBlank(metricPart.getMetric_code()), "指标编码不能为空");
        //指标编码重复检查
        Assert.isTrue(this.isUniqueSerialNumber(insertModel, metricPart.getMetric_code()), "指标编码已被占用");
        tmpMetric.setSerialNumber(metricPart.getMetric_code());
//        //加工逻辑检查
//        Assert.isTrue(StringUtils.isNotBlank(metricPart.getProcessing_logic()), "指标加工逻辑不能为空");
        tmpMetric.setProcessLogic(StringUtils.EMPTY);

        //指标说明检查
        Assert.isTrue(StringUtils.isNotBlank(metricPart.getDescription()), "指标说明不能为空");
        tmpMetric.setDescription(MetricYamlContentUtil.decodeYamlContent(metricPart.getDescription()));

        //指标实效检查
        ClusterType clusterType = ClusterType.getByFlag(metricPart.getCluster_type());
        String availableValues = Arrays.stream(ClusterType.values()).map(ClusterType::getFlag).collect(Collectors.joining(","));
        Assert.notNull(clusterType, "指标时效类型cluster_type错误，取值范围:" + availableValues);
        tmpMetric.setClusterType(clusterType);

        //主题域检查
        Assert.isTrue(StringUtils.isNotBlank(metricPart.getTheme()), "指标主题域不能为空");
        MetricCatalogDBVO topicDomain = metricCatalogDataManager.findByName(metricPart.getTheme());
        Assert.notNull(topicDomain, String.format("指标主题域%s未找到", metricPart.getTheme()));
        tmpMetric.setTopicDomain(topicDomain);
        //业务域检查
        Assert.isTrue(StringUtils.isNotBlank(metricPart.getBusiness()), "指标业务域不能为空");
        MetricCatalogDBVO businessDomain = metricCatalogDataManager.findByName(metricPart.getBusiness());
        Assert.notNull(businessDomain, String.format("指标业务域%s未找到", metricPart.getBusiness()));
        tmpMetric.setBusinessDomain(businessDomain);

        Assert.isTrue(Objects.equals(topicDomain.getId(), businessDomain.getParent().getId()), String.format("指标业务域%s必须在主题域%s目录下", metricPart.getBusiness(), metricPart.getTheme()));
        //技术负责人检查
        Assert.isTrue(!CollectionUtils.isEmpty(metricPart.getTech_owners()), "指标技术负责人不能为空");
        List<UserDBVO> techUserList = new ArrayList<>();
        for (String userName : metricPart.getTech_owners()) {
            UserDBVO user = userDataManager.findUserByName(userName);
            Assert.notNull(user, String.format("技术负责人用户%s未找到", userName));
            techUserList.add(user);
        }
        tmpMetric.setTechOwners(techUserList);
        //业务术负责人检查
        Assert.isTrue(!CollectionUtils.isEmpty(metricPart.getBusiness_owners()), "指标业务负责人不能为空");
        List<UserDBVO> businessUserList = new ArrayList<>();
        for (String userName : metricPart.getBusiness_owners()) {
            UserDBVO user = userDataManager.findUserByName(userName);
            Assert.notNull(user, String.format("业务负责人用户%s未找到", userName));
            businessUserList.add(user);
        }
        tmpMetric.setBusinessOwners(businessUserList);

        //指标周期检查
        Assert.isTrue(!CollectionUtils.isEmpty(metricPart.getStatisticPeriods()), "指标周期不能为空");
        List<StatisticPeriod> statisticPeriodList = new ArrayList<>();
        for (String period : metricPart.getStatisticPeriods()) {
            StatisticPeriod byCn = StatisticPeriod.findByCn(period);
            Assert.notNull(byCn, String.format("指标周期%s未找到", period));
            statisticPeriodList.add(byCn);
        }
        tmpMetric.setStatisticPeriods(statisticPeriodList);

        //指标聚合类型
        MetricAggregation metricAggregation = MetricAggregation.findByName(metricPart.getMetric_aggregation_type().trim());
        Assert.notNull(metricAggregation, "指标聚合类型字段metric_aggregation_type错误，取值范围" + Arrays.toString(MetricAggregation.values()));
        tmpMetric.setMetricAggregationType(metricAggregation);

        //指标是否可累加
        WhetherStatus isAccumulative = WhetherStatus.findByName(metricPart.getMetric_accumulative().trim());
        Assert.notNull(isAccumulative, "指标是否可累加字段metric_accumulative错误，取值范围" + Arrays.toString(WhetherStatus.values()));
        tmpMetric.setMetricAccumulative(isAccumulative);
        //指标是否认证
        WhetherStatus hadAuthentication = WhetherStatus.findByName(metricPart.getMetric_authentication().trim());
        Assert.notNull(hadAuthentication, "指标是否认证字段metric_authentication错误，取值范围" + Arrays.toString(WhetherStatus.values()));
        tmpMetric.setMetricAuthentication(hadAuthentication);
    }

    private Set<String> inspectMeasurePartInfo(MetricMergeSegment metricMergeSegment, Map<String, List<DatasetColumnVO>> allColumnMap, Set<String> identifierSet) {
        Set<String> measureNameSet = new HashSet<>();
        for (MeasuresSegment.UserMeasures measure : metricMergeSegment.getMeasures()) {
            //返回类型检查
            //Assert.isTrue(StringUtils.hasText(measure.getData_type()), String.format("度量(measures)名称%s缺失返回值类型", measure.getName()));
            //度量描述检查
            Assert.isTrue(StringUtils.isNotBlank(measure.getDescription()), String.format("度量(measures)名称%s缺失描述信息", measure.getName()));
            //名称检查
            Assert.isTrue(Pattern.matches(YamlSegmentKeys.YAML_NAME_RULE, measure.getName()), String.format("yaml度量(measures)名称%s格式错误,请参考(?!.*__).*^[a-z][a-z0-9_]*[a-z0-9]$", measure.getName()));
            //名称重复检查
            Assert.isTrue(!measureNameSet.contains(measure.getName()), String.format("度量(measures)名称%s重复", measure.getName()));
            measureNameSet.add(measure.getName());
            //数据源主外键同名检查，影响sql生成
            Assert.isTrue(!identifierSet.contains(measure.getName()), String.format("度量(measures)名称与数据源主外键%s重复,建议使用expr别名", measure.getName()));

            //聚合方式检查
            String availableValues = Arrays.stream(AggregationType.values()).map(AggregationType::getYamlValue).collect(Collectors.joining(","));
            AggregationType aggregationType = AggregationType.getByValue(measure.getAgg());
            Assert.notNull(aggregationType, String.format("yaml度量(measures)聚合方式%s格式错误,请参考%s", measure.getAgg(), availableValues));
            //规范大小写
            measure.setAgg(aggregationType.getYamlValue());
            //无表达式检查或者表达式与名称一致时
            if (StringUtils.isEmpty(measure.getExpr()) || Objects.equals(measure.getExpr(), measure.getName())) {
                List<DatasetColumnVO> existColumns = allColumnMap.get(measure.getName());
                Assert.isTrue(!CollectionUtils.isEmpty(existColumns), String.format("如没使用expr，度量(measures)名称%s必须取表中字段名称", measure.getName()));
                MetricUtil.checkAggTypeMatchColumnType(aggregationType, existColumns.get(0).getType());
            }
        }
        return measureNameSet;
    }


    /**
     * @param yamlContent yaml内容
     * @author zhao_xin
     * @description 分段检查格式是否正确
     **/
    private boolean inspectYamlFormat(String yamlContent) {
        boolean hasDataSource = false;
        boolean hasDimension = false;
        boolean hasMeasure = false;
        boolean hasMetric = false;
        //分段落验证
        for (String segment : yamlContent.split(YamlSegmentKeys.SEGMENT_SEPARATOR)) {
            if (StringUtils.isBlank(segment)) {
                continue;
            }
            Try<JsonNode> obj = YamlUtil.yamlToObject(segment, JsonNode.class);
            Assert.isTrue(obj.isSuccess(), "yaml内容格式化错误");
            Iterator<String> fields = obj.get().fieldNames();
            while (fields.hasNext()) {
                String keys = fields.next();
                switch (keys) {
                    //验证数据源格式信息
                    case YamlSegmentKeys.DATASOURCE:
                        Try<DataSourceSegment> dataSourceSegments = YamlUtil.yamlToObject(segment, DataSourceSegment.class);
                        if (dataSourceSegments.isSuccess()) {
                            hasDataSource = true;
                        }
                        break;
                    //验证度量信息
                    case YamlSegmentKeys.MEASURES:
                        Try<MeasuresSegment> measuresSegment = YamlUtil.yamlToObject(segment, MeasuresSegment.class);
                        if (measuresSegment.isSuccess()) {
                            hasMeasure = true;
                        }
                        break;
                    //验证维度信息
                    case YamlSegmentKeys.DIMENSIONS:
                        Try<DimensionsSegment> dimensionsSegments = YamlUtil.yamlToObject(segment, DimensionsSegment.class);
                        if (dimensionsSegments.isSuccess()) {
                            hasDimension = true;
                        }
                        break;
                    //验证指标信息
                    case YamlSegmentKeys.METRIC:
                        Try<MetricSegment> metricSegments = YamlUtil.yamlToObject(segment, MetricSegment.class);
                        if (metricSegments.isSuccess()) {
                            hasMetric = true;
                        }
                        break;
                    default:
                }
            }
        }
        Assert.isTrue(hasDataSource, "数据源信息格式解析异常，请检查格式是否正常，是否按---分割");
        Assert.isTrue(hasMeasure, "度量信息格式解析异常，请检查格式是否正常，是否按---分割");
        Assert.isTrue(hasDimension, "维度信息格式解析异常，，请检查格式是否正常，是否按---分割");
        Assert.isTrue(hasMetric, "指标信息格式解析异常，，请检查格式是否正常，是否按---分割");
        return hasDataSource && hasMeasure && hasDimension && hasMetric;
    }


    /*
      yaml内容整合执行验证
     */
    private String getExplainSql(DatasetVO datasetVO, DatasetSegment datasetSegment, MetricMergeSegment metricMergeSegment) {
        // pattern="(?!.*__).*^[a-z][a-z0-9_]*[a-z0-9]$";
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("基本信息封装");
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "用户信息获取异常");
        //没有时间类型需要加上默认时间维度
        DimensionsSegment.UserDimension defaultTimeDim = null;
        if (metricMergeSegment.getDimensions().stream().noneMatch(m -> Objects.equals(m.getType(), "time"))) {
            DimensionsSegment.TypeParams day = DimensionsSegment.TypeParams.builder().is_primary(true).time_granularity("day").build();
            defaultTimeDim = DimensionsSegment.UserDimension.builder()
                    .name(YamlSegmentKeys.DEFAULT_TIME_DIM_DS)
                    .type("time")
                    .expr(String.format("cast('%s' as datetime)", LocalDate.now()))
                    .type_params(day)
                    .build();
            metricMergeSegment.getDimensions().add(defaultTimeDim);
        }
        //数据源
        datasetSegment.setDimensions(metricMergeSegment.getDimensions().stream().map(f -> JsonUtils.transfrom(f, DimensionsSegment.Dimension.class)).collect(Collectors.toList()));
        datasetSegment.setMeasures(metricMergeSegment.getMeasures().stream().map(f -> JsonUtils.transfrom(f, MeasuresSegment.Measures.class)).collect(Collectors.toList()));
        DataSourceSegment dataSourceSegment = DataSourceSegment.builder().data_source(datasetSegment).build();
        //数据源+维度+度量
        String dataSourceYamlContent = YamlUtil.toYamlWithQuotation(dataSourceSegment);
        //指标部分
        String metricYamlContent = YamlUtil.toYamlWithQuotation(MetricSegmentVO.builder().metric(JsonUtils.transfrom(metricMergeSegment.getMetric(), MetricSegment.Metric.class)).build());
        //整合拼接
        String executeYamlContent = dataSourceYamlContent + metricYamlContent;
        String metricName = metricMergeSegment.getMetric().getName();
        stopWatch.stop();
        stopWatch.start("环境文件替换");
        if (metricFileService.replaceConfigFile(datasetVO.getCluster())
                && metricFileService.createYamlFiles(userDetail.getUsername(), metricName, executeYamlContent)) {
            Try<String> option = Try.of(() -> {
                        stopWatch.stop();
                        stopWatch.start("Process阶段执行");
                        if (Objects.equals(true, metricflowEnable)) {
                            String explainSql = MetricFlowProcessUtil.getMetricFlowParseSql(metricMergeSegment);
                            if (StringUtils.isBlank(explainSql)) {
                                metricFileService.keepYamlFiles(userDetail.getUsername(), metricName);
                            }
                            if (explainSql.contains("ERROR")) {
                                log.error("explainSql result :{}", explainSql);
                                metricFileService.keepYamlFiles(userDetail.getUsername(), metricName);
                            }
                            return explainSql;
                        } else {
                            return "Success . query completed after . SELECT 1+1 as " + metricName;
                        }
                    })
                    .onFailure(ex -> {
                        metricFileService.keepYamlFiles(userDetail.getUsername(), metricName);
                        log.error(String.format("getExplainSql fail ! userName:%s,metric:%s", userDetail.getUsername(), metricName), ex);
                    })
                    .andFinally(() -> metricFileService.deleteYamlFiles(userDetail.getUsername(), metricName));
            Assert.isTrue(option.isSuccess(), "指标生成sql异常");
            String result = option.get();
            if (result.contains("Success") && result.contains("query completed after") && result.contains("SELECT")) {
                if (defaultTimeDim != null) {
                    metricMergeSegment.getDimensions().remove(defaultTimeDim);
                }
                stopWatch.stop();
                log.info("getExplainSql 方法执行分析：{}", StopWatchUtils.getAllTaskInfo(stopWatch));
                return result.substring(result.indexOf("SELECT"));
            }
            stopWatch.stop();
        } else {
            stopWatch.stop();
            log.warn("create yaml config file error !");
        }
        return null;
    }

    @Override
    public String getMetricQuerySql(Integer metricId) {
        List<SegmentDBVO> segmentDBVOList = segmentDataManager.findSegmentByMetricId(YamlSegmentType.Sql, metricId);
        return segmentDBVOList.isEmpty() ? "" : segmentDBVOList.get(0).getContent();
    }

    /**
     * @param model 更新实体
     * @return com.quicksand.bigdata.vars.http.model.Response<com.quicksand.bigdata.metric.management.metric.models.MetricDetailModel>
     * @author zhao_xin
     * @description 新增或修改数据集方法
     **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<MetricDetailModel> upsertMetric(MetricInsertModel model) {
        StopWatch stopWatch = new StopWatch();
        MetricVO metricVO;
        boolean isCreate = null == model.getId() || 0 >= model.getId();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        MetricDBVO metricModel;

        stopWatch.start("执行格式内容检查");
        MetricMergeSegment metricMergeSegment = this.buildMetricMergeSegment(model, true);
        Assert.isTrue(metricMergeSegment.getVerifySuccess(), "yaml内容配置异常");
        Date operatorTime = new Date();
        stopWatch.stop();
        stopWatch.start("执行指标插入或更新");
        if (isCreate) {
            metricModel = MetricDBVO.builder()
                    .pubsub(PubsubStatus.Offline)
                    .updateTime(operatorTime)
                    .createTime(operatorTime)
                    .updateUserId(null == userDetail ? 0 : userDetail.getId())
                    .createUserId(null == userDetail ? 0 : userDetail.getId())
                    .defaultQuotaPeriod(MetricDBVO.QUOTA_PERIOD_MONTH)
                    .defaultCronExpress(MetricDBVO.CRON_MONTH)
                    .defaultQuota(1000L)
                    .processLogic(StringUtils.EMPTY)
                    .status(DataStatus.ENABLE).build();
        } else {
            metricModel = metricDataManager.findByMetricId(model.getId());
            Assert.notNull(metricModel, "更新指标不存在!");
            metricModel.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
            metricModel.setUpdateTime(operatorTime);
        }
        MetricDBVO tmpMetric = metricMergeSegment.getTmpMetric();
        metricModel.setCnName(tmpMetric.getCnName());
        metricModel.setEnName(tmpMetric.getEnName());
        metricModel.setCnAlias(tmpMetric.getCnAlias());
        metricModel.setEnAlias(tmpMetric.getEnAlias());
        metricModel.setTopicDomain(tmpMetric.getTopicDomain());
        metricModel.setBusinessDomain(tmpMetric.getBusinessDomain());
        metricModel.setBusinessOwners(tmpMetric.getBusinessOwners());
        metricModel.setTechOwners(tmpMetric.getTechOwners());
        metricModel.setDataset(tmpMetric.getDataset());
        metricModel.setDataType(tmpMetric.getDataType() == null ? "" : tmpMetric.getDataType());
        metricModel.setDescription(tmpMetric.getDescription());
        metricModel.setDataSecurityLevel(tmpMetric.getDataSecurityLevel());
        metricModel.setSerialNumber(tmpMetric.getSerialNumber());
        metricModel.setMetricLevel(tmpMetric.getMetricLevel());
        metricModel.setStatisticPeriods(tmpMetric.getStatisticPeriods());
        metricModel.setMetricType(tmpMetric.getMetricType());
        metricModel.setClusterType(tmpMetric.getClusterType());
        metricModel.setMeasure(tmpMetric.getMeasure());
        metricModel.setMetricExpr(tmpMetric.getMetricExpr());
        metricModel.setMetricAggregationType(tmpMetric.getMetricAggregationType());
        metricModel.setMetricAuthentication(tmpMetric.getMetricAuthentication());
        metricModel.setMetricAccumulative(tmpMetric.getMetricAccumulative());
        this.appendMetricDimsAndMeasures(metricMergeSegment, metricModel);
        if (isCreate) {
            metricDataManager.saveMetric(metricModel);
        } else {
            metricDataManager.updateMetric(metricModel);
        }
        stopWatch.stop();
        stopWatch.start("执行指标segment更新");
        //生成/更新指标yaml片段
        metricVO = JsonUtils.transfrom(metricModel, MetricVO.class);
        metricVO.setYamlSegment(model.getYamlSegment());
        yamlService.upsertSegment(metricVO, YamlSegmentType.Metric);
        stopWatch.stop();
        stopWatch.start("执行Sql的segment更新");
        //生成/更新指标sql
        MetricVO sqlMetricVo = new MetricVO();
        BeanUtils.copyProperties(metricVO, sqlMetricVo);
        sqlMetricVo.setYamlSegment(metricMergeSegment.getSqlContent());
        yamlService.upsertSegment(sqlMetricVo, YamlSegmentType.Sql);
        stopWatch.stop();
        log.info("upsertMetric 方法执行分析：{}", StopWatchUtils.getAllTaskInfo(stopWatch));
        return Response.ok(JsonUtils.transfrom(metricModel, MetricDetailModel.class));
    }

    @Override
    public void appendMetricDimsAndMeasures(MetricMergeSegment metricMergeSegment, MetricDBVO metricModel) {
        boolean isCreate = null == metricModel.getId() || 0 >= metricModel.getId();
        List<MetricMeasureDBVO> metricMeasureDBVOS = new ArrayList<>();
        List<MetricDimensionDBVO> metricDimensionDBVOS = new ArrayList<>();
        Date operationTime = new Date();
        UserSecurityDetails opUser = AuthUtil.getUserDetail();
        if (!isCreate) {
            List<MetricMeasureDBVO> oldMeasures = metricModel.getMeasures();
            if (!CollectionUtils.isEmpty(oldMeasures)) {
                oldMeasures.forEach(v -> {
                    v.setName(String.format("%s-%d", v.getName(), v.getCreateTime().getTime()));
                    v.setUpdateTime(operationTime);
                    v.setStatus(DataStatus.DISABLE);
                    v.setUpdateUserId(null == opUser ? 0 : opUser.getId());
                });
                metricMeasureDBVOS.addAll(oldMeasures);
            }
            List<MetricDimensionDBVO> oldDimensions = metricModel.getDimensions();
            if (!CollectionUtils.isEmpty(oldDimensions)) {
                oldDimensions.forEach(v -> {
                    v.setName(String.format("%s-%d", v.getName(), v.getCreateTime().getTime()));
                    v.setUpdateTime(operationTime);
                    v.setStatus(DataStatus.DISABLE);
                    v.setUpdateUserId(null == opUser ? 0 : opUser.getId());
                });
                metricDimensionDBVOS.addAll(oldDimensions);
            }
        }
        //create new
        Try.run(() -> {
                    if (!CollectionUtils.isEmpty(metricMergeSegment.getMeasures())) {
                        List<MetricMeasureDBVO> newColumns = metricMergeSegment.getMeasures().stream()
                                .map(v -> MetricMeasureDBVO.builder()
                                        .name(v.getName())
                                        .tableName(metricModel.getDataset().getTableName())
                                        .description(StringUtils.defaultIfBlank(v.getDescription(), StringUtils.EMPTY))
                                        .dataType(StringUtils.defaultIfBlank(v.getData_type(), StringUtils.EMPTY))
                                        .agg(AggregationType.getByValue(v.getAgg()))
                                        .processingLogic(StringUtils.defaultIfBlank(v.getProcessing_logic(), StringUtils.EMPTY))
                                        .expr(StringUtils.defaultIfBlank(v.getExpr(), StringUtils.EMPTY))
                                        .createTime(operationTime)
                                        .createUserId(null == opUser ? 0 : opUser.getId())
                                        .updateTime(operationTime)
                                        .updateUserId(null == opUser ? 0 : opUser.getId())
                                        .status(DataStatus.ENABLE)
                                        .build())
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(newColumns)) {
                            metricMeasureDBVOS.addAll(newColumns);
                        }
                    }
                    if (!CollectionUtils.isEmpty(metricMergeSegment.getDimensions())) {
                        List<MetricDimensionDBVO> newColumns = metricMergeSegment.getDimensions().stream()
                                .map(v -> MetricDimensionDBVO.builder()
                                        .name(v.getName())
                                        .tableName(metricModel.getDataset().getTableName())
                                        .type(DimType.getByValue(v.getType()))
                                        .description(StringUtils.defaultIfBlank(v.getDescription(), StringUtils.EMPTY))
                                        .dataType(StringUtils.defaultIfBlank(v.getData_type(), StringUtils.EMPTY))
                                        .isPrimary(v.getType_params() == null ? Boolean.FALSE : v.getType_params().getIs_primary())
                                        .timeGranularity(v.getType_params() == null ? StringUtils.EMPTY : StringUtils.defaultIfBlank(v.getType_params().getTime_granularity(), StringUtils.EMPTY))
                                        .expr(StringUtils.defaultIfBlank(v.getExpr(), StringUtils.EMPTY))
                                        .createTime(operationTime)
                                        .createUserId(null == opUser ? 0 : opUser.getId())
                                        .updateTime(operationTime)
                                        .updateUserId(null == opUser ? 0 : opUser.getId())
                                        .status(DataStatus.ENABLE)
                                        .build())
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(newColumns)) {
                            metricDimensionDBVOS.addAll(newColumns);
                        }
                    }
                })
                .onFailure(ex -> log.error("appendMetricMeasures error! ex:{}", ex));
        if (!CollectionUtils.isEmpty(metricMeasureDBVOS)) {
            metricModel.setMeasures(metricMeasureDBVOS);
        }
        if (!CollectionUtils.isEmpty(metricDimensionDBVOS)) {
            metricModel.setDimensions(metricDimensionDBVOS);
        }
    }

    @Override
    public List<MetricVO> findMetricByIds(List<Integer> metricIds) {
        return metricDataManager.findByMetricIds(metricIds).stream()
                .map(v -> JsonUtils.transfrom(v, MetricVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PageImpl<MetricOverviewModel> queryAllMetrics(MetricQueryModel queryModel, Pageable pageable) {
        Page<MetricDBVO> metricDBVOS = metricDataManager.queryAllMetricsByConditions(queryModel, pageable);
        PageImpl<MetricOverviewModel> page = PageImpl.buildEmptyPage(pageable.getPageNumber(), pageable.getPageSize());
        page.setTotal(metricDBVOS.getTotalElements());
        page.setTotalPage(metricDBVOS.getTotalPages());
        page.setItems(null);
        List<MetricOverviewModel> collect = metricDBVOS.getContent().stream().map(m -> JsonUtils.transfrom(m, MetricOverviewModel.class)).collect(Collectors.toList());
        page.setItems(collect);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void markDeleteByMetricId(Integer metricId) {
        MetricDBVO metricDBVO = metricDataManager.findByMetricId(metricId);
        Assert.notNull(metricDBVO, "指标不存在或已删除");
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Date operationTime = new Date();
        metricDBVO.setSerialNumber(String.format("%s_%d", metricDBVO.getSerialNumber(), metricId));
        assert userDetail != null;
        metricDBVO.setUpdateUserId(userDetail.getId());
        metricDBVO.setUpdateTime(new Date());
        metricDBVO.setStatus(DataStatus.DISABLE);
        metricDataManager.updateMetric(metricDBVO);
        //相关片段也标记删除
        List<SegmentDBVO> metricSegment = segmentDataManager.findSegmentByMetricId(YamlSegmentType.Metric, metricId);
        List<SegmentDBVO> sqlSegment = segmentDataManager.findSegmentByMetricId(YamlSegmentType.Sql, metricId);
        if (CollectionUtils.isEmpty(metricSegment) || CollectionUtils.isEmpty(metricSegment)) {
            return null;
        }
        metricSegment.addAll(sqlSegment);
        metricSegment.forEach(m -> {
            m.setUpdateUser(UserDBVO.builder().id(null == userDetail ? 0 : userDetail.getId()).build());
            m.setUpdateTime(operationTime);
            m.setStatus(DataStatus.DISABLE);
        });
        segmentDataManager.updateSegments(metricSegment);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PubsubStatus modifyMetricPubsubStatus(Integer metricId, PubsubStatus newPubsubStatus, MetricQuotaModel quotaModel) {
        MetricDBVO metricDBVO = metricDataManager.findByMetricId(metricId);
        Assert.notNull(metricDBVO, "指标不存在或已删除");
        if (null != quotaModel) {
            metricDBVO.setDefaultQuota(-1L == quotaModel.getQuota() ? 10000L : quotaModel.getQuota());
            metricDBVO.setDefaultQuotaPeriod(quotaModel.getPeriod());
            switch (quotaModel.getPeriod()) {
                case MetricDBVO.QUOTA_PERIOD_WEEK:
                    metricDBVO.setDefaultCronExpress(MetricDBVO.CRON_WEEK);
                    break;
                case MetricDBVO.QUOTA_PERIOD_DAY:
                    metricDBVO.setDefaultCronExpress(MetricDBVO.CRON_DAY);
                    break;
                case MetricDBVO.QUOTA_PERIOD_HOUR:
                    metricDBVO.setDefaultCronExpress(MetricDBVO.CRON_HOUR);
                    break;
                case MetricDBVO.QUOTA_PERIOD_MONTH:
                default:
                    metricDBVO.setDefaultCronExpress(MetricDBVO.CRON_MONTH);
                    break;
            }
        }
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        metricDBVO.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
        metricDBVO.setUpdateTime(new Date());
        metricDBVO.setPubsub(newPubsubStatus);
        metricDataManager.updateMetric(metricDBVO);
        //todo 上线 下线是否需要操作
        return metricDBVO.getPubsub();
    }


    @Override
    public String buildYamlContentFromUserModel(MetricYamlBuilderModel model) {
        List<MetricColumnsModel> dimColumns = model.getDimColumns();
        List<MetricColumnsModel> measureColumns = model.getMeasureColumns();
        List<DimensionsSegment.UserDimension> dimensionBuilderList = new ArrayList<>();
        DatasetDBVO datasetDBVO = datasetDataManager.findDatasetById(model.getBaseInfo().getDatasetId());
        Set<String> columnSet = datasetDBVO.getIdentifiers().stream().map(IdentifierDBVO::getName).collect(Collectors.toSet());
        //第一个时间维度，置为is_primary true
        boolean singleTimeDim = true;
        int orderNum = 0;
        for (MetricColumnsModel dimColumn : dimColumns) {
            DimType type = MetricUtil.getDimColumnsType(dimColumn.getColumnModel().getType());
            DimensionsSegment.TypeParams typeParams = null;
            if (Objects.equals(DimType.time, type)) {
                typeParams = DimensionsSegment.TypeParams.builder()
                        .is_primary(singleTimeDim)
                        .time_granularity("day")
                        .build();
                singleTimeDim = false;
            }
            String dimName = dimColumn.getColumnModel().getName().toLowerCase();//规避metricflow name验证
            //与其他字段名称冲突，则增加特殊编号
            if (columnSet.contains(dimName)) {
                dimName = dimName + "_dim" + orderNum;
                orderNum++;
                columnSet.add(dimName);
            }
            dimensionBuilderList.add(DimensionsSegment.UserDimension.builder()
                    .name(dimName)
                    .expr(dimColumn.getColumnModel().getName())
                    .type(type.getYamlValue())
                    .type_params(typeParams)
                    .data_type(dimColumn.getColumnModel().getType())
                    .description(dimColumn.getDescription())
                    .build());
        }
        //度量模块生成
        orderNum = 0;
        List<MeasuresSegment.UserMeasures> measuresBuilderList = new ArrayList<>();
        for (MetricColumnsModel measure : measureColumns) {
            String columnName = measure.getColumnModel().getName();
            //兼容导入情况组合字段
            String[] split = columnName.split("[\\*\\/\\+\\-]");
            String measureName = split[0].toLowerCase();//规避metricflow name验证

            //聚合方式特例检查
            AggregationType aggregationType = AggregationType.getByValue(measure.getAggregationType());
            Assert.notNull(aggregationType, "聚合类型未找到：" + measure.getAggregationType());

            MetricUtil.checkAggTypeMatchColumnType(aggregationType, measure.getColumnModel().getType());

            //与其他字段名称冲突，则增加特殊编号
            if (columnSet.contains(measureName)) {
                measureName = measureName + "_mea" + orderNum;
                orderNum++;
                columnSet.add(measureName);
            }
            measuresBuilderList.add(MeasuresSegment.UserMeasures.builder()
                    .name(measureName)
                    .expr(columnName)
                    .agg(aggregationType.getYamlValue())
                    .description(measure.getDescription())
                    .data_type(measure.getColumnModel().getType())
                    .build());
        }
        MetricModifyModel baseInfo = model.getBaseInfo();
        String metricType = MetricType.measure_proxy.getYamlValue();
        MetricSegment.TypeParams typeParams = MetricSegment.TypeParams.builder().build();
        //多个measures时默认生成相加metric
        if (measuresBuilderList.size() > 1) {
            metricType = MetricType.expr.getYamlValue();
            List<String> nameList = measuresBuilderList.stream().map(MeasuresSegment.Measures::getName).collect(Collectors.toList());
            typeParams.setExpr(StringUtils.join(nameList, '+'));
            typeParams.setMeasures(nameList);
        } else {
            //选中第一个度量为指标默认代理度量
            typeParams.setMeasure(measuresBuilderList.get(0).getName());
        }
        MetricSegment.UserMetric metric = MetricSegment.UserMetric.builder()
                .name(baseInfo.getEnName())
                .cn_name(baseInfo.getCnName())
                .alias(baseInfo.getEnAlias())
                .cn_alias(baseInfo.getCnAlias())
                .metric_code(baseInfo.getSerialNumber())
                .theme(metricCatalogDataManager.findById(baseInfo.getTopicDomainId()).getName())
                .business(metricCatalogDataManager.findById(baseInfo.getBusinessDomainId()).getName())
                .tech_owners(userDataManager.findUsers(baseInfo.getTechOwners()).stream().map(UserDBVO::getName).collect(Collectors.toList()))
                .business_owners(userDataManager.findUsers(baseInfo.getBusinessOwners()).stream().map(UserDBVO::getName).collect(Collectors.toList()))
                //.owners(b)
                .metric_level(MetricLevel.getByCode(baseInfo.getMetricLevel()).getFlag())
                .data_security_level(DataSecurityLevel.getByCode(baseInfo.getDataSecurityLevel()).getFlag())
                .data_type(baseInfo.getDataType())
                .description(MetricYamlContentUtil.encodeReplace(baseInfo.getDescription(), 3))
                .statisticPeriods(baseInfo.getStatisticPeriods().stream().map(StatisticPeriod::getCn).collect(Collectors.toList()))
                //.processing_logic(MetricYamlContentUtil.encodeReplace(baseInfo.getProcessLogic(), 3))
                .cluster_type(ClusterType.getByCode(baseInfo.getClusterType()).getFlag())
                .type(metricType)
                .type_params(typeParams)
                .metric_aggregation_type(MetricAggregation.findByCode(baseInfo.getMetricAggregationType()).getName())
                .metric_accumulative(WhetherStatus.findByCode(baseInfo.getMetricAccumulative()).getName())
                .metric_authentication(WhetherStatus.findByCode(baseInfo.getMetricAuthentication()).getName())
                //.constraint()
                .build();
        StringBuilder metricOverview = new StringBuilder(200);
        //展示给用户，用数据集原名
        DatasetSegment datasetSegment = yamlService.getDatasetSegment(JsonUtils.transfrom(datasetDBVO, DatasetVO.class));
        datasetSegment.setName(datasetDBVO.getName());
        datasetSegment.setDescription(MetricYamlContentUtil.encodeReplace(datasetSegment.getDescription(), 3));
        metricOverview.append(YamlUtil.toYaml(DataSourceSegment.builder().data_source(datasetSegment).build()));
        metricOverview.append(YamlUtil.toYaml(DimensionsSegment.builder().dimensions(dimensionBuilderList).build()));
        metricOverview.append(YamlUtil.toYaml(MeasuresSegment.builder().measures(measuresBuilderList).build()));
        metricOverview.append(YamlUtil.toYaml(MetricSegment.builder().metric(metric).build()));
        return MetricYamlContentUtil.decodeYamlContent(metricOverview.toString());
    }


    private Boolean isUniqueSerialNumber(MetricInsertModel metricInsertModel, String serialNumber) {
        boolean create = metricInsertModel.getId() == null;
        List<MetricDBVO> list = metricDataManager.findBySerialNumber(serialNumber);
        if (create) {
            return list.size() == 0;
        } else {
            return list.size() == 0 || (list.size() == 1 && Objects.equals(list.get(0).getId(), metricInsertModel.getId()));
        }
    }

    private Boolean isUniqueName(MetricInsertModel metricInsertModel, String enName, String cnName) {
        boolean create = metricInsertModel.getId() == null;
        List<MetricDBVO> list = metricDataManager.findByEnNameOrCnName(enName, cnName);
        if (create) {
            return list.size() == 0;
        } else {
            return list.size() == 0 || (list.size() == 1 && Objects.equals(list.get(0).getId(), metricInsertModel.getId()));
        }
    }

    @Override
    public List<DropDownListModel> getAppMetricsDropDownList(int appId) {
        return cacheService.getAppMetricsDownList(appId).stream().map(m -> JsonUtils.transfrom(m, DropDownListModel.class)).collect(Collectors.toList());
    }

    @Override
    public String getMetricSerialNumber(int topDomainId, int businessDomainId) {
        MetricCatalogDBVO topDomain = metricCatalogDataManager.findById(topDomainId);
        MetricCatalogDBVO businessDomain = metricCatalogDataManager.findById(businessDomainId);
        Assert.notNull(topDomain, "主题域不存在");
        Assert.notNull(businessDomain, "业务域不存在");
        Assert.isTrue(StringUtils.isNotBlank(topDomain.getBusinessCode()), "主题域未配置业务代码");
        Assert.isTrue(StringUtils.isNotBlank(businessDomain.getBusinessCode()), "业务域未配置业务代码");
        String maxSerialNumberByTopicAndBusiness = metricDataManager.getMaxSerialNumberByTopicAndBusiness(topDomain.getId(), businessDomain.getId());
        int defaultNumber = 0;
        if (maxSerialNumberByTopicAndBusiness != null && maxSerialNumberByTopicAndBusiness.length() > 4) {
            //检查是否是按标准规则拼接的
            String prefix = topDomain.getBusinessCode() + businessDomain.getBusinessCode();
            if (maxSerialNumberByTopicAndBusiness.startsWith(prefix)) {
                String numberStr = maxSerialNumberByTopicAndBusiness.replace(prefix, "");
                try {
                    defaultNumber = Integer.parseInt(numberStr);
                } catch (Exception e) {
                    log.error("主题域{}业务域{}下编码{}异常", topDomain.getName(), businessDomain.getName(), maxSerialNumberByTopicAndBusiness);
                }
            }
        }
        //重复的话，重试5次
        for (int i = 1; i < 6; i++) {
            String newSerialNumber = topDomain.getBusinessCode() + businessDomain.getBusinessCode() + String.valueOf(10000 + defaultNumber + i).substring(1);
            List<MetricDBVO> list = metricDataManager.findBySerialNumber(newSerialNumber);
            if (CollectionUtils.isEmpty(list)) {
                return newSerialNumber;
            }
        }
        throw new IllegalArgumentException("指标自动生成唯一编码异常");
    }

    @Override
    public MetricVO findMetricByEnNameOrSerialNumber(String name, String serialNumber) {
        MetricDBVO metric = metricDataManager.findMetricByEnNameOrSerialNumber(name, serialNumber);
        return null == metric ? null : JsonUtils.transfrom(metric, MetricVO.class);
    }

    @Override
    public MetricVO findMetricByName(String name) {
        MetricDBVO metric = metricDataManager.findByEnName(name);
        return null == metric ? null : JsonUtils.transfrom(metric, MetricVO.class);
    }

    @Override
    public MetricVO findMetricBySerialNumber(String serialNumber) {
        List<MetricDBVO> metricDBVOs = metricDataManager.findBySerialNumber(serialNumber);
        MetricDBVO metricDBVO = CollectionUtils.isEmpty(metricDBVOs) ? null : metricDBVOs.get(0);
        return null == metricDBVO ? null : JsonUtils.transfrom(metricDBVO, MetricVO.class);
    }

    @Override
    public PageImpl<AppInvokeDetailModel> findAppInvokeInfoByMetricId(int metricId, Pageable pageable) {
        return PageableUtil.page2page(metricDataManager.findAppInvokeInfoByMetricId(metricId, pageable), AppInvokeDetailModel.class, null);
    }

    @Override
    public List<MetricSegmentVersionModel> getMetricSegmentAllVersion(int metricId) {
        List<SegmentDBVO> allVersionSegmentByMetricId = segmentDataManager.findAllVersionSegmentByMetricId(YamlSegmentType.Metric, metricId);
        return allVersionSegmentByMetricId.stream().map(v -> JsonUtils.transfrom(v, MetricSegmentVersionModel.class)).collect(Collectors.toList());
    }
}
