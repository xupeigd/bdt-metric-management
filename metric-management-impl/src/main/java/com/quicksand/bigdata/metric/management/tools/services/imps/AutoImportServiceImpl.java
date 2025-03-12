package com.quicksand.bigdata.metric.management.tools.services.imps;

import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.datasource.dbvos.*;
import com.quicksand.bigdata.metric.management.datasource.dms.ClusterInfoDataManager;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetColumnModel;
import com.quicksand.bigdata.metric.management.datasource.models.TableColumnModel;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.engine.EngineService;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricCatalogDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricCatalogDataManager;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.*;
import com.quicksand.bigdata.metric.management.metric.services.MetricCatalogService;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.tools.services.AutoImportService;
import com.quicksand.bigdata.metric.management.tools.vos.FailedModel;
import com.quicksand.bigdata.metric.management.tools.vos.ImportResult;
import com.quicksand.bigdata.metric.management.tools.vos.MetricImportDBVO;
import com.quicksand.bigdata.metric.management.tools.vos.MetricImportModel;
import com.quicksand.bigdata.metric.management.utils.MetricYamlContentUtil;
import com.quicksand.bigdata.metric.management.yaml.services.YamlService;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricMergeSegment;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AutoImportServiceImpl
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/31 09:40
 * @description
 */
@Slf4j
@Service
public class AutoImportServiceImpl implements AutoImportService {
    @Resource
    ClusterInfoDataManager clusterInfoDataManager;

    @Resource
    MetricDataManager metricDataManager;

    @Resource
    MetricCatalogDataManager metricCatalogDataManager;

    @Resource
    EngineService engineService;

    @Resource
    DatasetDataManager datasetDataManager;

    @Resource
    OperationLogService operationLogService;

    @Resource
    UserDataManager userDataManager;

    @Resource
    UserService userService;

    @Resource
    YamlService yamlService;

    @Resource
    MetricCatalogService metricCatalogService;

    @Resource
    MetricService metricService;

    private static Integer getDataSecurityNum(String userSecurity) {
        String num = userSecurity.replaceAll("\\D", "");
        return Integer.parseInt(num);
    }

    private static List<StatisticPeriod> getStatisticPeriod(String userParams) {
        List<StatisticPeriod> list = new ArrayList<>();
        for (String period : userParams.split("[,，、]")) {
            if (StatisticPeriod.findByCn(period) != null) {
                list.add(StatisticPeriod.findByCn(period));
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return Collections.singletonList(StatisticPeriod.Year);
        }
        return list;
    }

    @Override
    public ImportResult ImportMetricDataByList(List<MetricImportModel> importModels) {
        List<FailedModel> failedModelList = new ArrayList<>();
        ImportResult importResult = ImportResult.builder()
                .failedCount(0)
                .successCount(0)
                .failedList(failedModelList)
                .build();
        //按集群分组
        Map<String, List<MetricImportModel>> importGroup = importModels.stream().peek(p -> {
                    //未提供集群信息，默认为db_bigdata
                    if (StringUtils.isBlank(p.getClusterName())) {
                        p.setClusterName("db_bigdata");
                    }
                })
                .collect(Collectors.groupingBy(MetricImportModel::getClusterName));
        Map<String, List<ClusterInfoDBVO>> clusterMap = clusterInfoDataManager.findAllClusterInfos().stream().collect(Collectors.groupingBy(ClusterInfoDBVO::getName));

        //集群信息确认
        for (String clusterName : importGroup.keySet()) {
            if (!clusterMap.containsKey(clusterName)) {
                FailedModel failedModel = FailedModel.builder()
                        .rowNumber(0)
                        .cnName(clusterName)
                        .tableName(clusterName)
                        .reason(String.format("集群%s不存在忽略相关指标导入", clusterName))
                        .build();
                failedModelList.add(failedModel);
                //移除不匹配集群相关导入指标
                importGroup.remove(clusterName);
            }
        }
        //分集群导入
        for (String clusterName : importGroup.keySet()) {
            //集群信息
            List<ClusterInfoDBVO> clusterInfoDBVOList = clusterMap.get(clusterName);
            ClusterInfoDBVO clusterInfoDBVO = clusterInfoDBVOList.get(0);
            List<String> tables;
            try {
                tables = engineService.queryTables(clusterInfoDBVO);
            } catch (Exception e) {
                failedModelList.add(FailedModel.builder()
                        .rowNumber(0)
                        .cnName(clusterName)
                        .tableName(clusterName)
                        .reason(String.format("集群%s获取可用表服务异常忽略导入该集群指标", clusterName))
                        .build());
                continue;
            }
            if (CollectionUtils.isEmpty(tables)) {
                failedModelList.add(FailedModel.builder()
                        .rowNumber(0)
                        .cnName(clusterName)
                        .tableName(clusterName)
                        .reason(String.format("集群%s获取可用表信息为空，忽略导入该集群指标", clusterName))
                        .build());
                continue;
            }
            Set<String> tableSet = new HashSet<>(tables);
            //集群下导入列表
            List<MetricImportModel> metricImportModels = importGroup.get(clusterName);
            //循环插入指标
            for (MetricImportModel metricImportModel : metricImportModels) {
                try {
                    MetricImportDBVO metricImportDBVO = MetricImportDBVO.builder()
                            .metricImportModel(metricImportModel)
                            .clusterInfoDBVO(clusterInfoDBVO)
                            .build();

//                    //指标编码正则检查
//                    if (!Pattern.matches("^[A-z\\d]{4,32}$", metricImportModel.getSerialNumber())) {
//                        result.getLines().add(String.format("第%d行指标编码%s格式不正确，忽略导入", metricImportModel.getRowNum(), metricImportModel.getSerialNumber()));
//                        continue;
//                    }
//                    //指标编码唯一性检查
//                    List<MetricDBVO> list = metricDataManager.findBySerialNumber(metricImportModel.getSerialNumber());
//                    if (list.size() > 0) {
//                        result.getLines().add(String.format("第%d行指标编码%s已存在，忽略导入", metricImportModel.getRowNum(), metricImportModel.getSerialNumber()));
//                        continue;
//                    }
                    //指标所用字段(度量字段和维度字段空值检查

                    MetricLevel metricLevel = MetricLevel.getByFlag(metricImportModel.getMetricLevel());
                    if (metricLevel == null) {
                        addErrorList(failedModelList, metricImportModel, String.format("指标类型%s未在T1、T2、T3范围内，忽略导入", metricImportModel.getMetricLevel()));
                        continue;
                    }

                    DataSecurityLevel dataSecurityLevel = DataSecurityLevel.getByFlag(metricImportModel.getDataSecurityLevel());
                    if (dataSecurityLevel == null) {
                        addErrorList(failedModelList, metricImportModel, String.format("指标安全等级%s未在L1、L2、L3、L4范围内，忽略导入", metricImportModel.getDataSecurityLevel()));
                        continue;
                    }

                    if (StringUtils.isBlank(metricImportModel.getMeasures())) {
                        addErrorList(failedModelList, metricImportModel, String.format("指标%s现字段名为空，忽略导入", metricImportModel.getSerialNumber()));
                        continue;
                    }
                    DatasetDBVO datasetDBVO = datasetDataManager.findFirstByName(metricImportModel.getTableName(), clusterInfoDBVO.getId());
                    //数据集已存在
                    Set<String> columnsSet;
                    if (datasetDBVO != null) {
                        columnsSet = datasetDBVO.getColumns().stream().map(DatasetColumnDBVO::getName).collect(Collectors.toSet());
                        metricImportDBVO.setDataset(datasetDBVO);
                    } else {
                        String userTableName = metricImportModel.getTableName().trim();
                        if (userTableName.startsWith("db_bigdata.")) {
                            userTableName = userTableName.replace("db_bigdata.", "");
                            metricImportModel.setTableName(userTableName);
                        }
                        if (!tableSet.contains(metricImportModel.getTableName())) {
                            addErrorList(failedModelList, metricImportModel, String.format("所属模型（表名）%s不在指定集群中，忽略导入", metricImportModel.getTableName()));
                            continue;
                        }
                        List<TableColumnModel> tableColumnModels = engineService.queryColumInfos(clusterInfoDBVO, metricImportModel.getTableName(), null);
                        if (CollectionUtils.isEmpty(tableColumnModels)) {
                            addErrorList(failedModelList, metricImportModel, String.format("表%s的可用字段信息为空，忽略导入该表指标", metricImportModel.getTableName()));
                            continue;
                        }
                        metricImportDBVO.setTableColumnModels(tableColumnModels);
                        columnsSet = tableColumnModels.stream().map(TableColumnModel::getName).collect(Collectors.toSet());
                    }
                    Boolean columnsOk = true;
                    for (String measureColumn : metricImportModel.getMeasures().split("[\\*\\/\\+\\-]")) {
                        if (!columnsSet.contains(measureColumn.trim())) {
                            columnsOk = false;
                            addErrorList(failedModelList, metricImportModel, String.format("使用统计字段%s未在指定模型表中，忽略导入该指标", measureColumn));
                            break;
                        }
                    }
                    for (String dimColumn : metricImportModel.getDimensions().split("[,、，;；]")) {
                        if (!columnsSet.contains(dimColumn.trim())) {
                            columnsOk = false;
                            addErrorList(failedModelList, metricImportModel, String.format("维度字段%s未在指定模型表中，忽略导入该指标", dimColumn));
                            break;
                        }
                    }
                    //字段验证不通过，忽略添加
                    if (!columnsOk) {
                        continue;
                    }
                    //事务性插入相关信息
                    this.InsertDataToDB(metricImportDBVO);
                    importResult.setSuccessCount(importResult.getSuccessCount() + 1);
                } catch (IllegalArgumentException e) {
                    log.error(" ImportMetricDataByList error:{}", e);
                    addErrorList(failedModelList, metricImportModel, String.format("参数异常:%s", e.getMessage()));
                } catch (Exception e) {
                    log.error(" ImportMetricDataByList error:{}", e);
                    addErrorList(failedModelList, metricImportModel, String.format("系统导入异常:%s", e.getMessage()));
                }
            }

        }
        return importResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean InsertDataToDB(MetricImportDBVO metricImportDBVO) {
        //尝试添加用户
        log.info("获取技术负责人{}", metricImportDBVO.getMetricImportModel().getTechOwners());
        List<UserDBVO> techOwners = getUser(metricImportDBVO.getMetricImportModel().getTechOwners());
        //尝试添加用户
        log.info("获取业务负责人{}", metricImportDBVO.getMetricImportModel().getBusinessOwners());
        List<UserDBVO> businessOwners = getUser(metricImportDBVO.getMetricImportModel().getBusinessOwners());
        metricImportDBVO.setTechOwners(techOwners);
        metricImportDBVO.setBusinessOwners(businessOwners);
        //尝试创建数据集
        log.info("尝试创建数据集");
        if (metricImportDBVO.getDataset() == null) {
            DatasetDBVO dataset = createDataset(metricImportDBVO);
            metricImportDBVO.setDataset(dataset);
        }
        //创建指标主题域
        log.info("尝试创建指标主题域{}", metricImportDBVO.getMetricImportModel().getTopicDomainName());
        MetricCatalogDBVO topDomain = getMetricCatalog(metricImportDBVO.getMetricImportModel().getTopicDomainName(), 0);
        metricImportDBVO.setTopicDomain(topDomain);
        //创建指标业务域
        log.info("尝试创建指标业务域{}", metricImportDBVO.getMetricImportModel().getBusinessDomainName());
        MetricCatalogDBVO businessDomain = getMetricCatalog(metricImportDBVO.getMetricImportModel().getBusinessDomainName(), topDomain.getId());
        metricImportDBVO.setBusinessDomain(businessDomain);
        //尝试创建指标
        log.info("尝试创建指表,num={}", metricImportDBVO.getMetricImportModel().getSerialNumber());
        this.createMetric(metricImportDBVO);
        return true;
    }

    private List<UserDBVO> getUser(String userNames) {
        List<UserDBVO> userVOList = new ArrayList<>();
        for (String userName : userNames.split("[、，,/]")) {
            if (StringUtils.isNotBlank(userName)) {
                userName = userName.trim();
                Assert.isTrue(userName.contains("@quicksand.com"), "负责人信息必须取用户邮箱");
                UserDBVO userVO = userDataManager.findUserByEmail(userName);
                Assert.notNull(userVO, String.format("用户%s未录入指标系统", userName));
                userVOList.add(userVO);

            }
        }
        return userVOList;
    }

    private List<UserDBVO> getOrCreateUser(String userNames) {
        List<UserDBVO> userVOList = new ArrayList<>();
        for (String userName : userNames.split("[、，,/]")) {
            if (StringUtils.isNotBlank(userName)) {
                userName = userName.trim();
                UserDBVO userVO = userDataManager.findUserByName(userName);
                if (userVO == null) {
                    if (userName.contains("@quicksand.com")) {
                        userName = userName.replace("@quicksand.com", "");
                    }
                    UserVO newUser = userService.createUser(userName + "@quicksand.com", userName + "@quicksand.com", "+86", userName, Collections.emptyList());
                    userVOList.add(JsonUtils.transfrom(newUser, UserDBVO.class));
                } else {
                    userVOList.add(userVO);
                }
            }
        }
        return userVOList;
    }

    private MetricCatalogDBVO getOrCreateMetricCatalog(String catalogName, int parentId) {
        if (StringUtils.isNotBlank(catalogName)) {
            catalogName = catalogName.trim();
            MetricCatalogDBVO byName = metricCatalogDataManager.findByName(catalogName);
            if (null == byName) {
                MetricCatalogModifyModel model = MetricCatalogModifyModel.builder().name(catalogName).parent(parentId).status(1).build();
                return JsonUtils.transfrom(metricCatalogService.upsertCatalog(model), MetricCatalogDBVO.class);
            } else {
                return byName;
            }
        }
        return null;
    }

    private MetricCatalogDBVO getMetricCatalog(String catalogName, int parentId) {
        String name = parentId == 0 ? "主题域" : "业务域";
        if (StringUtils.isNotBlank(catalogName)) {
            catalogName = catalogName.trim();
            MetricCatalogDBVO byName = metricCatalogDataManager.findByName(catalogName);
            Assert.notNull(byName, String.format("%s%s未录入指标系统,请先在指标系统新建", name, catalogName));
            return byName;
        } else {
            throw new IllegalArgumentException(name + "信息缺失");
        }
    }

    private DatasetDBVO createDataset(MetricImportDBVO metricImportDBVO) {
        Date operationTime = new Date();
        UserSecurityDetails opUser = AuthUtil.getUserDetail();
        MetricImportModel metricImportModel = metricImportDBVO.getMetricImportModel();
        List<TableColumnModel> tableColumnModels = metricImportDBVO.getTableColumnModels();
        //创建数据集
        assert opUser != null;
        DatasetDBVO newInstance = DatasetDBVO.builder().name(metricImportModel.getTableName().trim()).tableName(metricImportModel.getTableName().trim()).cluster(metricImportDBVO.getClusterInfoDBVO()).owners(metricImportDBVO.getTechOwners()).description("导入自动创建,请完善数据集描述").updateTime(operationTime).createTime(operationTime).updateUserId(opUser.getId()).createUserId(opUser.getId()).status(DataStatus.ENABLE).mutability(MutabilityDBVO.builder().type(Mutability.Immutable).alongColumn("").updateCron("").build()).build();
        //创建主键和外键
        List<IdentifierDBVO> identifier = new ArrayList<>();
        Optional<TableColumnModel> primaryColumn = tableColumnModels.stream().filter(f -> Objects.equals(f.getColumnType(), ColumnType.Primary)).findFirst();
        primaryColumn.ifPresent(tableColumnModel -> identifier.add(IdentifierDBVO.builder().name(tableColumnModel.getName()).type(IdentifierType.Primary).expr(tableColumnModel.getName()).createTime(operationTime).createUserId(opUser.getId()).updateTime(operationTime).updateUserId(opUser.getId()).status(DataStatus.ENABLE).build()));
        List<TableColumnModel> foreignColumns = tableColumnModels.stream().filter(f -> Objects.equals(f.getColumnType(), ColumnType.Foreign)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(foreignColumns)) {
            identifier.addAll(foreignColumns.stream().map(v -> IdentifierDBVO.builder().name(v.getName()).type(IdentifierType.Foreign).expr(v.getName()).createTime(operationTime).createUserId(opUser.getId()).updateTime(operationTime).updateUserId(opUser.getId()).status(DataStatus.ENABLE).build()).collect(Collectors.toList()));
        }
        newInstance.setIdentifiers(identifier);

        //创建数据集字段
        List<DatasetColumnDBVO> datasetColumnsList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tableColumnModels)) {
            List<DatasetColumnDBVO> newColumns = tableColumnModels.stream().map(v -> DatasetColumnDBVO.builder().tableName(newInstance.getTableName()).serial(v.getSerial()).name(v.getName()).columnType(v.getColumnType()).type(v.getType()).comment(v.getComment()).createTime(operationTime).createUserId(opUser.getId()).updateTime(operationTime).updateUserId(opUser.getId()).status(DataStatus.ENABLE).build()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(newColumns)) {
                datasetColumnsList.addAll(newColumns);
            }
        }

        if (!CollectionUtils.isEmpty(datasetColumnsList)) {
            newInstance.setColumns(datasetColumnsList);
        }

        datasetDataManager.saveDataset(newInstance);
        DatasetVO ret = JsonUtils.transfrom(newInstance, DatasetVO.class);
        //生成/更新 数据集yaml片段
        yamlService.upsertSegment(ret);
        Try.run(() -> operationLogService.log(OperationLogDBVO.builder().operationTime(operationTime).address("未知").ip("0.0.0.0").userId(opUser.getId()).type(OperationLogType.DATASET_CREATE).detail("指标导入自动创建数据集").build())).onFailure(ex -> log.warn("log error! ", ex));
        metricImportDBVO.setDataset(newInstance);
        return newInstance;
    }

    private MetricDBVO createMetric(MetricImportDBVO metricImportDBVO) {
        Date operationTime = new Date();
        UserSecurityDetails opUser = AuthUtil.getUserDetail();
        MetricImportModel importModel = metricImportDBVO.getMetricImportModel();
        importModel.setSerialNumber(metricService.getMetricSerialNumber(metricImportDBVO.getTopicDomain().getId(), metricImportDBVO.getBusinessDomain().getId()));
        MetricYamlBuilderModel metricYamlBuilderModel = createMetricYamlBuilderModel(metricImportDBVO);
        String yamlContent = metricService.buildYamlContentFromUserModel(metricYamlBuilderModel);
        yamlContent = MetricYamlContentUtil.decodeYamlContent(yamlContent);
        MetricInsertModel insertModel = new MetricInsertModel();
        insertModel.setYamlSegment(yamlContent);
        MetricMergeSegment metricMergeSegment = metricService.buildMetricMergeSegment(insertModel, true);
        MetricDBVO tmpMetric = metricMergeSegment.getTmpMetric();
        MetricDBVO metricModel = MetricDBVO.builder()
                .cnName(importModel.getCnName().trim())
                .enName(importModel.getEnName().trim())
                .cnAlias(StringUtils.defaultIfBlank(importModel.getCnAlias(), StringUtils.EMPTY))
                .enAlias(StringUtils.defaultIfBlank(importModel.getEnAlias(), StringUtils.EMPTY))
                .businessDomain(metricImportDBVO.getBusinessDomain())
                .topicDomain(metricImportDBVO.getTopicDomain())
                .businessOwners(metricImportDBVO.getBusinessOwners())
                .techOwners(metricImportDBVO.getTechOwners())
                .dataset(metricImportDBVO.getDataset())
                .dataType(StringUtils.defaultIfBlank(importModel.getDataType().trim(), StringUtils.EMPTY))
                .description(StringUtils.defaultIfBlank(importModel.getDescription(), StringUtils.EMPTY))
                .processLogic(StringUtils.EMPTY)
                .clusterType(ClusterType.offline)
                .dataSecurityLevel(DataSecurityLevel.getByFlag(importModel.getDataSecurityLevel().trim()))
                .serialNumber(importModel.getSerialNumber().trim())
                .statisticPeriods(getStatisticPeriod(importModel.getStatisticPeriods()))
                .pubsub(PubsubStatus.Offline)
                .metricLevel(MetricLevel.getByFlag(importModel.getMetricLevel().trim()))
                .updateTime(operationTime)
                .createTime(operationTime)
                .updateUserId(null == opUser ? 0 : opUser.getId())
                .createUserId(null == opUser ? 0 : opUser.getId())
                .measure(tmpMetric.getMeasure())
                .metricExpr(tmpMetric.getMetricExpr().trim())
                .metricType(tmpMetric.getMetricType().trim())
                .defaultQuota(1000L)
                .defaultCronExpress(MetricDBVO.CRON_MONTH)
                .defaultQuotaPeriod(MetricDBVO.QUOTA_PERIOD_MONTH)
                .status(DataStatus.ENABLE)
                .build();
        metricService.appendMetricDimsAndMeasures(metricMergeSegment, metricModel);
        metricDataManager.saveMetric(metricModel);
        MetricVO metricVO = JsonUtils.transfrom(metricModel, MetricVO.class);
        metricVO.setYamlSegment(yamlContent);
        //生成/更新指标yaml片段
        yamlService.upsertSegment(metricVO, YamlSegmentType.Metric);
        //生成/更新指标sql
        MetricVO sqlMetricVo = new MetricVO();
        BeanUtils.copyProperties(metricVO, sqlMetricVo);
        sqlMetricVo.setYamlSegment(metricMergeSegment.getSqlContent());
        yamlService.upsertSegment(sqlMetricVo, YamlSegmentType.Sql);
        return metricModel;
    }

//    private DimType getDimColumnsType(String columnType) {
//        Set<String> timeColumnTypeNameSet = new HashSet<>();
//        timeColumnTypeNameSet.add("date");
//        timeColumnTypeNameSet.add("datetime");
//        if (timeColumnTypeNameSet.contains(columnType.toLowerCase(Locale.ROOT))) {
//            return DimType.time;
//        }
//        return DimType.categorical;
//    }

    private MetricYamlBuilderModel createMetricYamlBuilderModel(MetricImportDBVO metricImportDBVO) {
        MetricImportModel importModel = metricImportDBVO.getMetricImportModel();
        Map<String, List<DatasetColumnDBVO>> columnsMap = metricImportDBVO.getDataset().getColumns().stream().collect(Collectors.groupingBy(DatasetColumnDBVO::getName));

        //度量添加
        DatasetColumnModel measureColumnModel = new DatasetColumnModel();
        measureColumnModel.setName(importModel.getMeasures());
        measureColumnModel.setType(importModel.getDataType());
        String measureDesc = StringUtils.EMPTY;
        //度量为单个字段时，度量取字段描述
        List<DatasetColumnDBVO> datasetColumnDBVOS1 = columnsMap.get(measureColumnModel.getName());
        if (CollectionUtils.isEmpty(datasetColumnDBVOS1)) {
            measureDesc = datasetColumnDBVOS1.get(0).getComment();
        }
        measureDesc = StringUtils.isBlank(measureDesc) ? "描述" : measureDesc;
        MetricColumnsModel measure = MetricColumnsModel.builder().columnModel(measureColumnModel).description(measureDesc).aggregationType(AggregationType.SUM.getYamlValue()).build();
        //维度添加
        List<MetricColumnsModel> dimColumns = new ArrayList<>();
        for (String dimColumnName : importModel.getDimensions().split("[,、，;；]")) {
            dimColumnName = dimColumnName.trim();
            List<DatasetColumnDBVO> datasetColumnDBVOS = columnsMap.get(dimColumnName);
            DatasetColumnDBVO datasetColumnDBVO = datasetColumnDBVOS.get(0);
            DatasetColumnModel datasetColumnModel = new DatasetColumnModel();
            datasetColumnModel.setName(datasetColumnDBVO.getName());
            datasetColumnModel.setType(datasetColumnDBVO.getType());
            //datasetColumnModel.setDataset();
            String dimDesc = StringUtils.isBlank(datasetColumnDBVO.getComment()) ? "描述" : datasetColumnDBVO.getComment();
            MetricColumnsModel metricColumnsModel = MetricColumnsModel.builder().columnModel(datasetColumnModel).description(dimDesc).build();
            dimColumns.add(metricColumnsModel);
        }
        //基本信息补充
        MetricModifyModel baseInfo = new MetricModifyModel();
        baseInfo.setCnName(importModel.getCnName());
        baseInfo.setEnName(importModel.getEnName());
        baseInfo.setSerialNumber(importModel.getSerialNumber());
        baseInfo.setCnAlias(importModel.getCnAlias());
        baseInfo.setEnAlias(importModel.getEnAlias());
        baseInfo.setTechOwners(metricImportDBVO.getTechOwners().stream().map(UserDBVO::getId).collect(Collectors.toList()));
        baseInfo.setBusinessOwners(metricImportDBVO.getBusinessOwners().stream().map(UserDBVO::getId).collect(Collectors.toList()));
        baseInfo.setDescription(MetricYamlContentUtil.encodeReplace(importModel.getDescription(), 3));
//        baseInfo.setProcessLogic(MetricYamlContentUtil.encodeReplace(importModel.getProcessLogic(), 3));
        baseInfo.setMetricLevel(MetricLevel.getByFlag(importModel.getMetricLevel()).getCode());
        baseInfo.setDataSecurityLevel(DataSecurityLevel.getByFlag(importModel.getDataSecurityLevel()).getCode());
        baseInfo.setDataType(importModel.getDataType());
        baseInfo.setStatisticPeriods(getStatisticPeriod(importModel.getStatisticPeriods()));
        baseInfo.setTopicDomainId(metricImportDBVO.getTopicDomain().getId());
        baseInfo.setBusinessDomainId(metricImportDBVO.getBusinessDomain().getId());
        baseInfo.setDatasetId(metricImportDBVO.getDataset().getId());
        baseInfo.setClusterType(getClusterTypeCode(importModel.getClusterType()));
        return MetricYamlBuilderModel.builder()
                .baseInfo(baseInfo)
                .measureColumns(Collections.singletonList(measure)).dimColumns(dimColumns).build();
    }

    private int getClusterTypeCode(String clusterTypeStr) {
        return ClusterType.getByFlag(clusterTypeStr) == null ? ClusterType.offline.getCode() : ClusterType.getByFlag(clusterTypeStr).getCode();
    }

    private void addErrorList(List<FailedModel> failedModelList, MetricImportModel metricImportModel, String reason) {
        failedModelList.add(FailedModel.builder()
                .rowNumber(metricImportModel.getRowNum())
                .cnName(metricImportModel.getCnName())
                .tableName(metricImportModel.getTableName())
                .reason(reason)
                .build());
    }

}
