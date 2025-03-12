package com.quicksand.bigdata.metric.management.datasource.services.impls;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.IdentifierType;
import com.quicksand.bigdata.metric.management.consts.Mutability;
import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.datasource.dbvos.*;
import com.quicksand.bigdata.metric.management.datasource.dms.ClusterInfoDataManager;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.DatasetModifyModel;
import com.quicksand.bigdata.metric.management.datasource.models.TableColumnModel;
import com.quicksand.bigdata.metric.management.datasource.services.DatasetService;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.engine.EngineService;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.yaml.services.YamlService;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DatasetServiceImpl
 *
 * @author page
 * @date 2022/8/2
 */
@Slf4j
@Service
public class DatasetServiceImpl
        implements DatasetService {

    @Resource
    YamlService yamlService;
    @Resource
    EngineService engineService;
    @Resource
    UserDataManager userDataManager;
    @Resource
    DatasetDataManager datasetDataManager;
    @Resource
    ClusterInfoDataManager clusterInfoDataManager;
    @Resource
    OperationLogService operationLogService;

    @Override
    public DatasetVO upsertDataset(DatasetModifyModel model) throws Throwable {
        DatasetVO ret = null;
        boolean isCreate = null == model.getId() || 0 >= model.getId();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Date operationTime = new Date();
        //验证pk，fks
        ClusterInfoDBVO clusterInfo = clusterInfoDataManager.findClusterInfo(model.getCluster());
        Try.run(() -> {
                    Map<String, TableColumnModel> name2ColumnMap = engineService.queryColumInfos(clusterInfo, model.getTableName(), "")
                            .stream()
                            .collect(Collectors.toMap(TableColumnModel::getName, v -> v));
                    if (StringUtils.hasText(model.getPrimaryKey())
                            && !name2ColumnMap.containsKey(model.getPrimaryKey())) {
                        throw new ValidationException(String.format("设定的主键不存在！primaryKey:%s", model.getPrimaryKey()));
                    }
                    if (!CollectionUtils.isEmpty(model.getForeignKeys())) {
                        for (String foreignKey : model.getForeignKeys()) {
                            if (!name2ColumnMap.containsKey(foreignKey)) {
                                throw new ValidationException(String.format("设定的外键不存在！foreignKey:%s", foreignKey));
                            }
                        }
                    }
                })
                .onFailure(ex -> log.error(String.format("upsertDataset error ! cluster:%d,table:%s", clusterInfo.getId(), model.getTableName()), ex))
                .getOrElseThrow(x -> x);
        if (isCreate) {
            DatasetDBVO newInstance = DatasetDBVO.builder()
                    .name(model.getName())
                    .tableName(model.getTableName())
                    .cluster(clusterInfo)
                    .owners(userDataManager.findUsers(model.getOwners()))
                    .description(StringUtils.hasText(model.getDescription()) ? model.getDescription() : "")
                    .updateTime(operationTime)
                    .createTime(operationTime)
                    .updateUserId(null == userDetail ? 0 : userDetail.getId())
                    .createUserId(null == userDetail ? 0 : userDetail.getId())
                    .status(DataStatus.ENABLE)
                    .mutability(null == model.getMutability()
                            ? MutabilityDBVO.builder()
                            .type(Mutability.Immutable)
                            .alongColumn("")
                            .updateCron("")
                            .build()
                            : MutabilityDBVO.builder()
                            .type(model.getMutability().getType())
                            .alongColumn(StringUtils.hasText(model.getMutability().getAlongColumn())
                                    ? model.getMutability().getAlongColumn()
                                    : "")
                            .updateCron(StringUtils.hasText(model.getMutability().getUpdateCron())
                                    ? model.getMutability().getUpdateCron()
                                    : "")
                            .build())
                    .build();
            rePrePersistIdentifiers(newInstance, model, true);
            rePrePersistColumns(newInstance, true);
            datasetDataManager.saveDataset(newInstance);
            ret = JsonUtils.transfrom(newInstance, DatasetVO.class);
            //生成/更新yaml片段
            yamlService.upsertSegment(ret);
            Try.run(() ->
                    operationLogService.log(OperationLogDBVO.builder()
                            .operationTime(operationTime)
                            .address("未知")
                            .ip("0.0.0.0")
                            .userId(null == userDetail ? 0 : userDetail.getId())
                            .type(OperationLogType.DATASET_CREATE)
                            .detail(String.format("创建数据集，参数：%s", JsonUtils.toJsonString(model)))
                            .build())
            ).onFailure(ex -> log.warn("log error! ", ex));
        } else {
            DatasetDBVO dataset = datasetDataManager.findDatasetById(model.getId());
            if (null != dataset) {
                dataset.setName(model.getName());
                dataset.setTableName(model.getTableName());
                dataset.setCluster(clusterInfo);
                dataset.setOwners(userDataManager.findUsers(model.getOwners()));
                dataset.setDescription(StringUtils.hasText(model.getDescription()) ? model.getDescription() : "");
                dataset.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
                dataset.setUpdateTime(operationTime);
                if (null == dataset.getMutability()) {
                    dataset.setMutability(MutabilityDBVO.builder()
                            .type(Mutability.Immutable)
                            .alongColumn("")
                            .updateCron("")
                            .build());
                } else {
                    dataset.setMutability(MutabilityDBVO.builder()
                            .type(model.getMutability().getType())
                            .alongColumn(StringUtils.hasText(model.getMutability().getAlongColumn())
                                    ? model.getMutability().getAlongColumn()
                                    : "")
                            .updateCron(StringUtils.hasText(model.getMutability().getUpdateCron())
                                    ? model.getMutability().getUpdateCron()
                                    : "")
                            .build());
                }
                rePrePersistIdentifiers(dataset, model, false);
                rePrePersistColumns(dataset, false);
                datasetDataManager.saveDataset(dataset);
                ret = JsonUtils.transfrom(dataset, DatasetVO.class);
                //生成/更新yaml片段
                if (!CollectionUtils.isEmpty(ret.getIdentifiers())) {
                    ret.setIdentifiers(ret.getIdentifiers()
                            .stream()
                            .filter(v -> Objects.equals(DataStatus.ENABLE, v.getStatus()))
                            .collect(Collectors.toList())
                    );
                }
                if (!CollectionUtils.isEmpty(ret.getColumns())) {
                    ret.setColumns(ret.getColumns()
                            .stream()
                            .filter(v -> Objects.equals(DataStatus.ENABLE, v.getStatus()))
                            .collect(Collectors.toList()));
                }
                yamlService.upsertSegment(ret);
                Try.run(() ->
                        operationLogService.log(OperationLogDBVO.builder()
                                .operationTime(operationTime)
                                .address("未知")
                                .ip("0.0.0.0")
                                .userId(null == userDetail ? 0 : userDetail.getId())
                                .type(OperationLogType.DATASET_MODIFY)
                                .detail(String.format("修改数据集，参数：%s", JsonUtils.toJsonString(model)))
                                .build())
                ).onFailure(ex -> log.warn("log error! ", ex));
            }
        }
        return ret;
    }

    private List<IdentifierDBVO> rePrePersistIdentifiers(DatasetDBVO dataset, DatasetModifyModel model, boolean isNew) {
        List<IdentifierDBVO> identifier = new ArrayList<>();
        Date operationTime = new Date();
        UserSecurityDetails opUser = AuthUtil.getUserDetail();
        if (!isNew) {
            List<IdentifierDBVO> oldIdentifiers = dataset.getIdentifiers();
            if (!CollectionUtils.isEmpty(oldIdentifiers)) {
                oldIdentifiers.forEach(v -> {
                    v.setStatus(DataStatus.DISABLE);
                    v.setUpdateUserId(null == opUser ? 0 : opUser.getId());
                    v.setUpdateTime(operationTime);
                    v.setName(String.format("%s-%d", v.getName(), v.getCreateTime().getTime()));
                });
                identifier.addAll(oldIdentifiers);
            }
        }
        if (StringUtils.hasText(model.getPrimaryKey())) {
            identifier.add(IdentifierDBVO.builder()
                    .name(model.getPrimaryKey())
                    .type(IdentifierType.Primary)
                    .expr(model.getPrimaryKey())
                    .createTime(operationTime)
                    .createUserId(null == opUser ? 0 : opUser.getId())
                    .updateTime(operationTime)
                    .updateUserId(null == opUser ? 0 : opUser.getId())
                    .status(DataStatus.ENABLE)
                    .build());
        }
        if (!CollectionUtils.isEmpty(model.getForeignKeys())) {
            identifier.addAll(model.getForeignKeys()
                    .stream()
                    .map(v -> IdentifierDBVO.builder()
                            .name(v)
                            .type(IdentifierType.Foreign)
                            .expr(v)
                            .createTime(operationTime)
                            .createUserId(null == opUser ? 0 : opUser.getId())
                            .updateTime(operationTime)
                            .updateUserId(null == opUser ? 0 : opUser.getId())
                            .status(DataStatus.ENABLE)
                            .build())
                    .collect(Collectors.toList()));
        }
        dataset.setIdentifiers(identifier);
        return identifier;
    }

    /**
     * 重新持计划列信息
     *
     * @param dataset dataset实体
     * @param isNew
     */
    private void rePrePersistColumns(DatasetDBVO dataset, boolean isNew) {
        List<DatasetColumnDBVO> dbvos = new ArrayList<>();
        Date operationTime = new Date();
        UserSecurityDetails opUser = AuthUtil.getUserDetail();
        if (!isNew) {
            List<DatasetColumnDBVO> oldColumns = dataset.getColumns();
            if (!CollectionUtils.isEmpty(oldColumns)) {
                oldColumns.forEach(v -> {
                    v.setName(String.format("%s-%d", v.getName(), v.getCreateTime().getTime()));
                    v.setUpdateTime(operationTime);
                    v.setStatus(DataStatus.DISABLE);
                    v.setUpdateUserId(null == opUser ? 0 : opUser.getId());
                });
                dbvos.addAll(oldColumns);
            }
        }
        //create new
        Try.run(() -> {
                    List<TableColumnModel> columnModels = engineService.queryColumInfos(dataset.getCluster(), dataset.getTableName(), "");
                    if (!CollectionUtils.isEmpty(columnModels)) {
                        List<DatasetColumnDBVO> newColumns = columnModels.stream()
                                .map(v -> DatasetColumnDBVO.builder()
                                        .tableName(dataset.getTableName())
                                        .serial(v.getSerial())
                                        .name(v.getName())
                                        .columnType(v.getColumnType())
                                        .type(v.getType())
                                        .comment(v.getComment())
                                        .createTime(operationTime)
                                        .createUserId(null == opUser ? 0 : opUser.getId())
                                        .updateTime(operationTime)
                                        .updateUserId(null == opUser ? 0 : opUser.getId())
                                        .status(DataStatus.ENABLE)
                                        .build())
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(newColumns)) {
                            dbvos.addAll(newColumns);
                        }
                    }
                })
                .onFailure(ex -> log.error(String.format("rePrePersistColumns error! dataset:%d,table:%s", dataset.getId(), dataset.getTableName()), ex));
        if (!CollectionUtils.isEmpty(dbvos)) {
            dataset.setColumns(dbvos);
        }
    }

}
