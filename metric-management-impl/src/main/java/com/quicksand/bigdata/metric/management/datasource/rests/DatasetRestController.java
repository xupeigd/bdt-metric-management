package com.quicksand.bigdata.metric.management.datasource.rests;

import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetColumnDBVO;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dbvos.IdentifierDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.ClusterInfoDataManager;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.*;
import com.quicksand.bigdata.metric.management.datasource.services.DatasetService;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.MetricQueryModel;
import com.quicksand.bigdata.metric.management.utils.DatasetUtil;
import com.quicksand.bigdata.metric.management.utils.PageableUtil;
import com.quicksand.bigdata.metric.management.utils.YamlUtil;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;
import com.quicksand.bigdata.metric.management.yaml.dms.SegmentDataManager;
import com.quicksand.bigdata.metric.management.yaml.services.YamlService;
import com.quicksand.bigdata.metric.management.yaml.vos.DataSourceSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.DatasetSegment;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DatasetController
 *
 * @author page
 * @date 2022/7/27
 */
@Slf4j
@Validated
@CrossOrigin
// @Api("数据集Apis")
@Tag(name = "数据集Apis")
@RestController
public class DatasetRestController
        implements DatasetRestService, DatasetManageRestService {

    @Resource
    YamlService yamlService;
    @Resource
    DatasetService datasetService;
    @Resource
    UserDataManager userDataManager;
    @Resource
    SegmentDataManager segmentDataManager;
    @Resource
    DatasetDataManager datasetDataManager;
    @Resource
    OperationLogService operationLogService;
    @Resource
    ClusterInfoDataManager clusterInfoDataManager;

    @Resource
    MetricDataManager metricDataManager;

    /**
     * @param pageNo           页码 基于1
     * @param pageSize         页容量 默认 20
     * @param nameKeyword      名称关键字
     * @param clusterIds       集群id
     * @param ownerIds         负责人id
     * @param ownerNameKeyword 负责人名称关键字
     * @return page of DatasetOverviewModel
     */
    @Operation(description = "获取数据集列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！")
    })
    @PreAuthorize("hasAuthority('OP_DATASET_LIST') " +
            "|| hasAuthority('OP_DATASET_SEARCH')  " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Transactional
    @Override
    public Response<PageImpl<DatasetOverviewModel>> queryDatasets(@Min(1) @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                                  @Min(1) @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                                  @RequestParam(name = "nameKeyword", required = false, defaultValue = "")
                                                                  @Parameter(name = "nameKeyword", description = "名称关键字") String nameKeyword,
                                                                  @RequestParam(name = "clusterIds", required = false)
                                                                  @Parameter(name = "clusterIds", description = "集群Ids(多个采用半角逗号分隔)") List<Integer> clusterIds,
                                                                  @RequestParam(name = "clusterNameKeyword", required = false, defaultValue = "")
                                                                  @Parameter(name = "clusterNameKeyword", description = "集群名称") String clusterNameKeyword,
                                                                  @RequestParam(name = "ownerIds", required = false)
                                                                  @Parameter(name = "ownerIds", description = "负责人ids(多个采用半角逗号分隔)") List<Integer> ownerIds,
                                                                  @RequestParam(name = "ownerNameKeyword", required = false, defaultValue = "")
                                                                  @Parameter(name = "ownerNameKeyword", description = "负责人名称关键字(可模糊搜索)") String ownerNameKeyword) {
        List<Integer> reqClusterIds = null == clusterIds ? Collections.emptyList() : clusterIds;
        //前置验证
        List<Integer> deClusterIds = new ArrayList<>();
        if (StringUtils.hasText(clusterNameKeyword)) {
            List<Integer> hitClusterIds = clusterInfoDataManager.findClustersNameLike(clusterNameKeyword)
                    .stream()
                    .map(ClusterInfoDBVO::getId)
                    .filter(id -> reqClusterIds.isEmpty() || reqClusterIds.contains(id))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(hitClusterIds)) {
                return Response.ok(PageImpl.buildEmptyPage(pageNo, pageSize));
            }
            deClusterIds.addAll(hitClusterIds);
        } else if (!reqClusterIds.isEmpty()) {
            deClusterIds.addAll(reqClusterIds);
        }
        List<Integer> reqOwnerIds = null == ownerIds ? Collections.emptyList() : ownerIds;
        List<Integer> deOwnerIds = new ArrayList<>();
        if (StringUtils.hasText(ownerNameKeyword)) {
            List<Integer> hitUserIds = userDataManager.findUsersNameLike(ownerNameKeyword)
                    .stream()
                    .map(UserDBVO::getId)
                    .filter(id -> reqOwnerIds.isEmpty() || reqOwnerIds.contains(id))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(hitUserIds)) {
                return Response.ok(PageImpl.buildEmptyPage(pageNo, pageSize));
            }
            deOwnerIds.addAll(hitUserIds);
        } else if (!reqOwnerIds.isEmpty()) {
            deOwnerIds.addAll(reqOwnerIds);
        }

        return Response.ok(
                PageableUtil.page2page(
                        datasetDataManager.findAllDatasets(nameKeyword, deClusterIds, deOwnerIds, PageRequest.of(pageNo - 1, pageSize, Sort.by("update_time").descending())),
                        DatasetOverviewModel.class, r -> {
                            DatasetOverviewModel transfrom = JsonUtils.transfrom(r, DatasetOverviewModel.class);
                            if (!CollectionUtils.isEmpty(r.getOwners())) {
                                transfrom.setOwners(
                                        r.getOwners().stream()
                                                .map(v -> Try.of(() -> JsonUtils.transfrom(v, UserOverviewModel.class)).getOrNull())
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList()));
                            }
                            return transfrom;
                        })
        );
    }

    @Operation(description = "获取单个dataset数据")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！")
    })
    @Override
    public Response<DatasetModel> queryDataset(@PathVariable("datasetId") int datasetId) {
        DatasetDBVO dataset = datasetDataManager.findDatasetById(datasetId);
        if (null == dataset) {
            return Response.notfound();
        }
        return Response.ok(JsonUtils.transfrom(dataset, DatasetModel.class, r -> {
            List<String> includedColumns = DatasetUtil.resolvingFields(dataset);
            r.setIncludedColumns(StringUtils.collectionToCommaDelimitedString(includedColumns));
            if (null != dataset.getCluster()) {
                r.setClusterInfo(JsonUtils.transfrom(dataset.getCluster(), ClusterInfoModel.class));
            }
            if (!CollectionUtils.isEmpty(dataset.getOwners())) {
                r.setOwners(
                        dataset.getOwners().stream()
                                .map(v -> Try.of(() -> JsonUtils.transfrom(v, UserOverviewModel.class)).getOrNull())
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                if (!CollectionUtils.isEmpty(dataset.getIdentifiers())) {
                    List<String> primaryKeys = dataset.getIdentifiers()
                            .stream()
                            .filter(v -> Objects.equals(IdentifierType.Primary, v.getType()))
                            .map(IdentifierDBVO::getName)
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(primaryKeys)) {
                        r.setPrimaryKey(primaryKeys.get(0));
                    }
                    r.setForeignKeys(dataset.getIdentifiers()
                            .stream()
                            .filter(v -> Objects.equals(IdentifierType.Foreign, v.getType()))
                            .map(IdentifierDBVO::getName)
                            .collect(Collectors.toList()));
                }
            }
            return r;
        }));
    }

    @Operation(description = "获取数据集字段信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_DATASET_COLUMNS')  " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Override
    public Response<List<DatasetColumnModel>> queryColumnsTypes(@PathVariable("datasetId") int datasetId,
                                                                @RequestParam(name = "nameKeyword", required = false, defaultValue = "")
                                                                @Parameter(name = "字段名称关键字，可选") String nameKeyword,
                                                                @RequestParam(name = "dataTypes", required = false, defaultValue = "")
                                                                @Parameter(name = "数据类型(可选，多个使用半角逗号分隔)") String dataTypes) {
        DatasetDBVO dataset = datasetDataManager.findDatasetById(datasetId);
        if (null == dataset) {
            return Response.response(HttpStatus.NOT_FOUND, "无效的的数据集! ");
        }
        List<String> reqTypes = StringUtils.hasText(dataTypes)
                ? new ArrayList<>(Arrays.stream(dataTypes.split(","))
                .filter(StringUtils::hasText)
                .map(String::toUpperCase)
                .collect(Collectors.toSet()))
                : new ArrayList<>();
        List<DatasetColumnDBVO> columns = dataset.getColumns();
        if (!CollectionUtils.isEmpty(columns)) {
            columns = columns.stream()
                    .filter(v -> !StringUtils.hasText(nameKeyword) || v.getName().contains(nameKeyword))
                    .filter(v -> reqTypes.isEmpty() || reqTypes.stream().anyMatch(v.getType().toUpperCase()::contains))
                    .collect(Collectors.toList());
        }
        DatasetOverviewModel transfrom = JsonUtils.transfrom(dataset, DatasetOverviewModel.class);
        return Response.ok(columns.stream()
                .map(v -> JsonUtils.transfrom(v, DatasetColumnModel.class))
                .peek(v -> v.setDataset(transfrom))
                .collect(Collectors.toList()));
    }

    @Operation(description = "获取数据集字段类型信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_DATASET_COLUMNS')  " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Override
    public Response<List<ColumnTypeModel>> queryColumnsTypes(@PathVariable("datasetId") int datasetId) {
        DatasetDBVO dataset = datasetDataManager.findDatasetById(datasetId);
        if (null == dataset) {
            return Response.notfound();
        }
        if (CollectionUtils.isEmpty(dataset.getColumns())) {
            return Response.ok();
        }
        Set<String> details = new HashSet<>();
        return Response.ok(dataset.getColumns()
                .stream()
                .map(v -> ColumnTypeModel.builder()
                        .type(v.getType().contains("(") ? v.getType().substring(0, v.getType().indexOf('(')).toUpperCase() : v.getType().toUpperCase())
                        .details(v.getType())
                        .build())
                .filter(v -> details.add(v.getDetails()))
                .collect(Collectors.toList()));
    }

    @Operation(description = "查询数据集的yamlSegment")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_DATASET_DETAIL')  " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Override
    public Response<YamlViewModel> queryDatasetYamlModel(@PathVariable("datasetId") @Min(value = 1L, message = "无效的的数据集")
                                                         @Max(value = Integer.MAX_VALUE, message = "无效的的数据集") int datasetId) {
        DatasetDBVO dataset = datasetDataManager.findDatasetById(datasetId);
        if (null == dataset) {
            return Response.response(HttpStatus.NOT_FOUND, "无效的的数据集! ");
        }
        List<SegmentDBVO> segments = segmentDataManager.findSegmentByDatasetId(datasetId);
        if (CollectionUtils.isEmpty(segments)) {
            return Response.response(HttpStatus.SERVICE_UNAVAILABLE, "服务异常! ");
        }
        SegmentDBVO segmentDBVO = segments.get(0);
        Try<DatasetSegment> datasetSegmentTry = YamlUtil.yamlToObject(segmentDBVO.getContent(), DatasetSegment.class);
        if (datasetSegmentTry.isFailure()) {
            return Response.response(HttpStatus.SERVICE_UNAVAILABLE, "服务异常! ");
        }
        DatasetSegment datasetSegment = datasetSegmentTry.get();
        datasetSegment.setName(dataset.getName());
        String content = YamlUtil.toYaml(DataSourceSegment.builder().data_source(datasetSegment).build());
        YamlViewModel yamlViewModel = YamlViewModel.builder()
                .yamlSegment(content)
                .lines(Arrays.asList(content.split("\n")))
                .build();
        return Response.ok(yamlViewModel);
    }

    @SneakyThrows
    @Operation(description = "创建数据集")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_DATASET_CREATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Transactional
    @Override
    public Response<DatasetOverviewModel> createDataset(@RequestBody @Validated({Insert.class}) DatasetModifyModel model) {
        if (null == clusterInfoDataManager.findClusterInfo(model.getCluster())) {
            return Response.response(HttpStatus.BAD_REQUEST, "集群参数错误! ");
        }
//        if (!CollectionUtils.isEmpty(model.getOwners())
//                && CollectionUtils.isEmpty(userDataManager.findUsers(model.getOwners()))) {
//            return Response.response(HttpStatus.BAD_REQUEST, "负责人参数错误! ");
//        }
        if (null != datasetDataManager.findDatasetByName(model.getName())) {
            return Response.response(HttpStatus.BAD_REQUEST, "存在重名数据集! ");
        }
        return Response.ok(JsonUtils.transfrom(datasetService.upsertDataset(model), DatasetOverviewModel.class));
    }

    @SneakyThrows
    @Operation(description = "修改数据集")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "400", description = "参数错误! "),
    })
    @PreAuthorize("hasAuthority('OP_DATASET_MODIFY') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Transactional
    @Override
    public Response<DatasetOverviewModel> modifyDataset(@PathVariable("datasetId") @Min(value = 0L, message = "不存在的数据集")
                                                        @Max(value = Integer.MAX_VALUE, message = "不存在的数据集") int datasetId,
                                                        @RequestBody @Validated({Update.class}) DatasetModifyModel model) {
        if (!Objects.equals(datasetId, model.getId())) {
            return Response.response(HttpStatus.BAD_REQUEST, "参数错误! ");
        }
        if (null == clusterInfoDataManager.findClusterInfo(model.getCluster())) {
            return Response.response(HttpStatus.BAD_REQUEST, "集群参数错误! ");
        }
//        if (!CollectionUtils.isEmpty(model.getOwners())
//                && CollectionUtils.isEmpty(userDataManager.findUsers(model.getOwners()))) {
//            return Response.response(HttpStatus.BAD_REQUEST, "负责人参数错误! ");
//        }
        DatasetDBVO dataset = datasetDataManager.findDatasetById(model.getId());
        if (null == dataset) {
            return Response.response(HttpStatus.NOT_FOUND);
        }
        DatasetDBVO sameName = datasetDataManager.findDatasetByName(model.getName());
        if (!Objects.equals(dataset.getName(), model.getName())
                && null != sameName
                && !Objects.equals(dataset.getId(), sameName.getId())) {
            return Response.response(HttpStatus.BAD_REQUEST, "存在重名数据集! ");
        }
        return Response.ok(JsonUtils.transfrom(datasetService.upsertDataset(model), DatasetOverviewModel.class));
    }

    @Operation(description = "删除数据集")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_DATASET_DELETE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Transactional
    @Override
    public Response<Void> deleteDataset(@PathVariable("datasetId") @Min(value = 0L, message = "不存在的数据集")
                                        @Max(value = Integer.MAX_VALUE, message = "不存在的数据集") int datasetId) {
        Date operationTime = new Date();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        DatasetDBVO dataset = datasetDataManager.findDatasetById(datasetId);
        if (null == dataset) {
            return Response.response(HttpStatus.NOT_FOUND);
        }
        //如果存在有效指标引用不能删除
        MetricQueryModel queryModel = MetricQueryModel.builder().datasetIds(Collections.singletonList(dataset.getId())).build();
        Pageable pageable = PageRequest.of(0, 20);
        Page<MetricDBVO> metricDBVOS = metricDataManager.queryAllMetricsByConditions(queryModel, pageable);
        if (!CollectionUtils.isEmpty(metricDBVOS.getContent())) {
            return Response.response(HttpStatus.BAD_REQUEST, "数据集下存在引用指标，无法删除");
        }
//        Assert.isTrue(CollectionUtils.isEmpty(metricDBVOS.getContent()), "数据集下存在引用指标，无法删除");
        //消除重名
        dataset.setName(String.format("%s-%d", dataset.getName(), datasetId));
        dataset.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
        dataset.setUpdateTime(operationTime);
        dataset.setStatus(DataStatus.DISABLE);
        if (!CollectionUtils.isEmpty(dataset.getIdentifiers())) {
            dataset.getIdentifiers().forEach(v -> {
                v.setName(String.format("%s-%d", v.getName(), v.getCreateTime().getTime()));
                v.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
                v.setUpdateTime(operationTime);
                v.setStatus(DataStatus.DISABLE);
            });
        }
        if (!CollectionUtils.isEmpty(dataset.getColumns())) {
            dataset.getColumns().forEach(v -> {
                v.setName(String.format("%s-%d", v.getName(), v.getCreateTime().getTime()));
                v.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
                v.setUpdateTime(operationTime);
                v.setStatus(DataStatus.DISABLE);
            });
        }
        datasetDataManager.updateDataset(dataset);
        yamlService.removeSegmentByDatasetId(datasetId);
        Try.run(() ->
                operationLogService.log(OperationLogDBVO.builder()
                        .operationTime(operationTime)
                        .address("未知")
                        .ip("0.0.0.0")
                        .userId(null == userDetail ? 0 : userDetail.getId())
                        .type(OperationLogType.DATASET_DELETE)
                        .detail(String.format("删除数据集，id：%d", datasetId))
                        .build())
        ).onFailure(ex -> log.warn("log error! ", ex));
        return Response.ok();
    }


}
