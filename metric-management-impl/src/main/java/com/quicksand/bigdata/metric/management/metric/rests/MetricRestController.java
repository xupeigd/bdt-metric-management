package com.quicksand.bigdata.metric.management.metric.rests;

import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.apis.services.AppService;
import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.YamlViewModel;
import com.quicksand.bigdata.metric.management.metric.models.*;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;
import com.quicksand.bigdata.metric.management.yaml.dms.SegmentDataManager;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricMergeSegment;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MetricsController
 *
 * @author page
 * @date 2022-07-27
 */
@Slf4j
@Validated
@CrossOrigin
// @Api("指标Apis")
@Tag(name = "指标Apis")
@RestController
public class MetricRestController
        implements MetricRestService, MetricManageRestService {

    @Resource
    DatasetDataManager datasetDataManager;
    @Resource
    MetricService metricService;
    @Resource
    SegmentDataManager segmentDataManager;
    @Resource
    AppService appService;

    @Operation(description = "分页获取指标列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Override
    @PreAuthorize("hasAuthority('OP_METRICS_LIST') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    public Response<PageImpl<MetricOverviewModel>> listMetrics(@RequestBody @Validated({Insert.class}) MetricQueryModel queryModel) {
        //指标上下线条件整合
        if (!CollectionUtils.isEmpty(queryModel.getPubsubs())) {
            if (queryModel.getPubsubs().contains(PubsubStatus.Online.getCode())) {
                queryModel.setPubsub(PubsubStatus.Online.getCode());
            }
            if (queryModel.getPubsubs().contains(PubsubStatus.Offline.getCode())) {
                queryModel.setPubsub(PubsubStatus.Offline.getCode());
            }
            if (queryModel.getPubsubs().contains(PubsubStatus.Online.getCode()) && queryModel.getPubsubs().contains(PubsubStatus.Offline.getCode())) {
                queryModel.setPubsub(null);
            }
        }

        //指标实效性条件整合
        if (!CollectionUtils.isEmpty(queryModel.getClusterTypes())) {
            if (queryModel.getClusterTypes().contains(ClusterType.offline.getCode())) {
                queryModel.setClusterType(ClusterType.offline.getCode());
            }
            if (queryModel.getClusterTypes().contains(ClusterType.realtime.getCode())) {
                queryModel.setClusterType(ClusterType.realtime.getCode());
            }
            if (queryModel.getClusterTypes().contains(PubsubStatus.Online.getCode()) && queryModel.getClusterTypes().contains(ClusterType.offline.getCode())) {
                queryModel.setClusterType(null);
            }
        }
        PageRequest requestPage = PageRequest.of(queryModel.getPageNo() - 1, queryModel.getPageSize());

        //添加排序
        List<Sort.Order> orderList = new ArrayList<>();
        if (CollectionUtils.isEmpty(queryModel.getSorts())) {
            //添加默认排序
            orderList.add(Sort.Order.desc("updateTime"));
        } else {
            for (SortModel userSort : queryModel.getSorts()) {
                orderList.add(userSort.getAsc() ? Sort.Order.asc(userSort.getName()) : Sort.Order.desc(userSort.getName()));
            }
        }
        requestPage = requestPage.withSort(Sort.by(orderList));
        return Response.ok(metricService.queryAllMetrics(queryModel, requestPage));
    }

    @Operation(description = "获取指标详情")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<MetricOverviewModel> findMetric(@RequestParam(name = "name", required = false) String name,
                                                    @RequestParam(name = "serialNumber", required = false) String serialNumber) {
        if (!StringUtils.hasText(name)
                && !StringUtils.hasText(serialNumber)) {
            return Response.ok();
        }
        MetricVO metricVO = StringUtils.hasText(name) && StringUtils.hasText(serialNumber)
                ? metricService.findMetricByEnNameOrSerialNumber(name, serialNumber)
                : (StringUtils.hasText(name) ? metricService.findMetricByName(name) : metricService.findMetricBySerialNumber(serialNumber));
        return Response.ok(null == metricVO ? null : JsonUtils.transfrom(metricVO, MetricOverviewModel.class));
    }

    @Operation(description = "获取指标详情")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<MetricDetailModel> findMetricById(@PathVariable("id") @Min(value = 0L, message = "不存在的指标实体! ") @Parameter(name = "指标id") int id) {
        return Response.ok(metricService.findMetricById(id));
    }

    @Operation(description = "获取指标的模型")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_MODEL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<MetricSegmentModel> findMetricYamlSegmentById(@PathVariable("id") @Min(value = 0L, message = "不存在的指标实体! ") int id) {
        return Response.ok(metricService.findMetricYamlSegmentById(id));
    }

    @Operation(description = "创建指标")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Override
    public Response<MetricDetailModel> createMetric(@RequestBody @Validated({Insert.class}) MetricInsertModel model) {
        return metricService.upsertMetric(model);
    }

    @Operation(description = "修改指标")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_MODIFY') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Override
    public Response<MetricDetailModel> modifyMetric(@PathVariable("id") @Min(value = 0L, message = "不存在的指标实体！ ") int id,
                                                    @RequestBody @Validated({Update.class}) MetricInsertModel model) {
        model.setId(id);
        return metricService.upsertMetric(model);
    }

    @Operation(description = "删除指标")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DELETE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Override
    public Response<Void> removeMetric(@PathVariable("id") @Min(value = 0L, message = "不存在的指标实体！ ") @Parameter(name = "指标id") int id) {
        return Response.ok(metricService.markDeleteByMetricId(id));
    }

    @Operation(description = "修改指标上下线状态")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_PUS') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Transactional
    @Override
    public Response<PubsubStatus> modifyMetricPubsubStatus(@PathVariable("id") int id,
                                                           @PathVariable("pubsub") int pubsub,
                                                           @Validated @RequestBody(required = false) MetricQuotaModel metricQuota) {
        PubsubStatus newStatus = PubsubStatus.findByCode(pubsub);
        Assert.notNull(newStatus, "状态字段参数异常");
        if (null != metricQuota && 0L == metricQuota.getQuota()) {
            return Response.response(HttpStatus.BAD_REQUEST, "额度不能小于0!");
        }
        return Response.ok(metricService.modifyMetricPubsubStatus(id, newStatus, metricQuota));
    }

    @Operation(description = "检查yaml语法")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE') " +
            "|| hasAuthority('OP_METRICS_MODIFY') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Override
    public Response<MetricInsertModel> verifyYamlSegment(@RequestBody @Validated({Update.class}) MetricInsertModel model) {
        MetricMergeSegment metricMergeSegment = metricService.buildMetricMergeSegment(model, true);
        return metricMergeSegment.getVerifySuccess() ? Response.ok(model) : Response.response(HttpStatus.BAD_REQUEST, "验证未通过");
    }

    @Operation(description = "根据度量维度信息构建yaml内容")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE') " +
            "|| hasAuthority('OP_METRICS_MODIFY') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Override
    public Response<YamlViewModel> buildMetricYamlSegment(@RequestBody @Validated({Insert.class}) MetricYamlBuilderModel model) {
        DatasetDBVO dataset = datasetDataManager.findDatasetById(model.getBaseInfo().getDatasetId());
        if (null == dataset) {
            return Response.response(HttpStatus.BAD_REQUEST, "基础信息中数据集不存在! ");
        }
        Assert.isTrue(model.getMeasureColumns().stream().allMatch(a -> AggregationType.getByValue(a.getAggregationType()) != null), "指标度量字段需要提供有效的aggregationType字段");
        String content = metricService.buildYamlContentFromUserModel(model);
        YamlViewModel viewModel = YamlViewModel.builder().lines(Arrays.asList(content.split("\n"))).yamlSegment(content
        ).build();
        return Response.ok(viewModel);
    }

    @Operation(description = "获取聚合方式列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE') " +
            "|| hasAuthority('OP_METRICS_MODIFY') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Override
    public Response<AggregationType[]> getMetricAggregation() {
        return Response.ok(AggregationType.values());
    }

    @Override
    public Response<YamlViewModel> getMetricYaml(@PathVariable("metricId") @Min(value = 0L, message = "不存在的指标实体! ") int metricId) {
        MetricDetailModel metricDetailModel = metricService.findMetricById(metricId);
        if (null == metricDetailModel) {
            return Response.notfound();
        }
        List<SegmentDBVO> segments = segmentDataManager.findSegmentByMetricId(YamlSegmentType.Metric, metricId);
        if (CollectionUtils.isEmpty(segments)) {
            return Response.ok();
        }
        SegmentDBVO segmentDBVO = segments.get(0);
        YamlViewModel yamlViewModel = YamlViewModel.builder()
                .lines(Arrays.asList(segmentDBVO.getContent().split("\n")))
                .build();
        return Response.ok(yamlViewModel);
    }

    @Operation(description = "让指标与应用解绑")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Response<Void> unbindAppAndMetric(AppInvokeDetailModel appInvokeDetailModel) {
        appService.unbindAppAndMetric(appInvokeDetailModel.getAppId().intValue(), appInvokeDetailModel.getMetricId().intValue());
        return Response.ok();
    }

    @Operation(description = "获取指标被调用的应用列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<PageImpl<AppInvokeDetailModel>> listInvokeApps(@PathVariable("metricId") @Min(value = 1L, message = "不存在的指标实体! ") int metricId,
                                                                   @Parameter(name = "pageNo", description = "页码（最小1）") @Min(value = 1, message = "最小pageNo为1！")
                                                                   @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                                   @Parameter(name = "pageSize", description = "页容量（最小1）") @Min(value = 1, message = "最小pageSize为1！")
                                                                   @Max(value = 200, message = "最大pageSize不超过200")
                                                                   @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        PageRequest page = PageRequest.of(pageNo - 1, pageSize);
        return Response.ok(metricService.findAppInvokeInfoByMetricId(metricId, page));
    }

    @Operation(description = "根据主题域和业务域自动生成指标编码")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<String> getSerialNumber(int topicId, int businessId) {
        return Response.response(metricService.getMetricSerialNumber(topicId, businessId), HttpStatus.OK);
    }

    @Operation(description = "获取指标历史版本信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_DETAIL') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @Override
    public Response<List<MetricSegmentVersionModel>> findMetricVersions(int metricId) {
        MetricDetailModel metricDetailModel = metricService.findMetricById(metricId);
        if (null == metricDetailModel) {
            return Response.notfound();
        }
        return Response.response(metricService.getMetricSegmentAllVersion(metricId), HttpStatus.OK);
    }
}
