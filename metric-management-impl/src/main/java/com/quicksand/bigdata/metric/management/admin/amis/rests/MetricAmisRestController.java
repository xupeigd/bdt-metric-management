package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.google.common.collect.Lists;
import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.consts.AggregationType;
import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.consts.StatisticPeriod;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.YamlViewModel;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.*;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 指标AMIS接口控制器
 *
 * @Author: page
 * @Date: 2025/3/16
 * @Description: 提供指标的CRUD操作和相关查询功能
 */
@RequestMapping(Vars.PATH_ROOT + "/amis/metrics")
@RestController
@Tag(name = "指标管理", description = "指标的增删改查和相关操作接口")
public class MetricAmisRestController {

    @Resource
    MetricDataManager metricDataManager;
    @Resource
    MetricService metricService;
    @Resource
    DatasetDataManager datasetDataManager;
    @Value("${metricflow.enable}")
    boolean metricflowEnable;

    @Operation(summary = "获取指标列表", description = "分页获取所有指标信息列表，支持按业务域过滤")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = PageImpl.class))),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('OP_METRICS_LIST') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    public FrameworkResponse<PageImpl<MetricOverviewModel>, Void> listMetrics(
            @Parameter(description = "业务域ID，默认为0表示不过滤") @RequestParam(name = "businessCatalog", required = false, defaultValue = "0") Integer businessId,
            @Parameter(description = "页码，默认为1") @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @Parameter(description = "每页记录数，默认为20") @RequestParam(name = "perPage", defaultValue = "20") int pageSize) {
        MetricQueryModel metricQueryModel = new MetricQueryModel();
        if (null != businessId && 0 != businessId) {
            metricQueryModel.setBusinessIds(Lists.newArrayList(businessId));
        }
        PageImpl<MetricOverviewModel> overviewModelPage = metricService.queryAllMetrics(metricQueryModel,
                PageRequest.of(pageNo - 1, pageSize));
        return FrameworkResponse.frameworkResponse(overviewModelPage, null, 0, "");
    }

    @Builder
    @Data
    public static final class ContentValue<T> {
        T content;
        T value;
    }

    @Operation(summary = "获取指标SQL预览", description = "根据指标ID获取其对应的SQL查询语句")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = Content.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "指标不存在")
    })
    @GetMapping("/{metricId}/previewSql")
    public FrameworkResponse<ContentValue<String>, Void> getMetricPreviewSql(
            @Parameter(description = "指标ID", required = true) @Min(value = 1L, message = "不存在的指标！") @Max(value = Integer.MAX_VALUE, message = "不存在的指标！") @PathVariable("metricId") int metricId) {
        MetricDBVO metric = metricDataManager.findByMetricId(metricId);
        if (null == metric) {
            return FrameworkResponse.frameworkResponse(1, "指标不存在或已被删除！");
        }
        String transformSql = metricService.getMetricQuerySql(metricId);
        // 获取sql
        if (!StringUtils.hasText(transformSql)) {
            if (Objects.equals(false, metricflowEnable)) {
                transformSql = String.format("select 1+1 as %s", metric.getEnName());
            } else {
                // 不重新获取
                return FrameworkResponse.frameworkResponse(1, "指标状态异常：编译失败，不存在或已被删除！");
            }
        }
        return FrameworkResponse.frameworkResponse(new ContentValue<>(transformSql, null), null, 0, "sucess!");
    }

    @Operation(summary = "修改指标发布状态", description = "根据指标ID更新其发布状态，可选择性地设置配额")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = Content.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "指标不存在"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @Transactional
    @PutMapping("/{id}/{pubsub}")
    public FrameworkResponse<ContentValue<PubsubStatus>, Void> modifyMetricPubsubStatus(
            @Parameter(description = "指标ID", required = true) @PathVariable("id") int id,
            @Parameter(description = "发布状态码", required = true) @PathVariable("pubsub") int pubsub,
            @Parameter(description = "指标配额模型") @Validated @RequestBody(required = false) MetricQuotaModel metricQuota) {
        PubsubStatus newStatus = PubsubStatus.findByCode(pubsub);
        if (null == newStatus) {
            return FrameworkResponse.frameworkResponse(1, "参数错误！");
        }
        if (null != metricQuota && 0L == metricQuota.getQuota()) {
            return FrameworkResponse.frameworkResponse(1, "额度不能小于0!");
        }
        PubsubStatus pubsubStatus = metricService.modifyMetricPubsubStatus(id, newStatus, metricQuota);
        return FrameworkResponse.frameworkResponse(new ContentValue<>(pubsubStatus, null), null, 0, "success");
    }

    /**
     * 根据主题域和业务域自动生成指标编码
     *
     * @return 指标编码字符串
     */
    @Operation(summary = "获取指标序列号", description = "根据主题域和业务域自动生成指标编码")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = Content.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @GetMapping("/serialNumber")
    public FrameworkResponse<ContentValue<String>, Void> getSerialNumber(
            @Parameter(description = "主题域ID", required = false) @Min(value = 1L, message = "不存在的主题域! ") @RequestParam(name = "topicId", required = false) Integer topicId,
            @Parameter(description = "业务域ID", required = false) @Min(value = 1L, message = "不存在的业务域! ") @RequestParam(name = "businessId", required = false) Integer businessId) {
        String metricSerialNumber = null != topicId && null != businessId
                ? metricService.getMetricSerialNumber(topicId, businessId)
                : "";
        return FrameworkResponse.frameworkResponse(new ContentValue<>(null, metricSerialNumber), null, 0, "success ! ");
    }

    @Operation(summary = "构建指标YAML片段", description = "根据提供的模型构建指标的YAML配置片段")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = YamlViewModel.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "数据集不存在"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @PostMapping("/build")
    public FrameworkResponse<YamlViewModel, Void> buildMetricYamlSegment(
            @Parameter(description = "指标YAML构建模型", required = true) @RequestBody @Validated({
                    Insert.class}) MetricYamlBuilderModel model) {
        DatasetDBVO dataset = datasetDataManager.findDatasetById(model.getBaseInfo().getDatasetId());
        if (null == dataset) {
            return FrameworkResponse.frameworkResponse(1, "基础信息中数据集不存在! ");
        }
        Assert.isTrue(model.getMeasureColumns().stream()
                        .allMatch(a -> AggregationType.getByValue(a.getAggregationType()) != null),
                "指标度量字段需要提供有效的aggregationType字段");
        String content = metricService.buildYamlContentFromUserModel(model);
        YamlViewModel viewModel = YamlViewModel.builder()
                .lines(Arrays.asList(content.split("\n")))
                .value(content).build();
        return FrameworkResponse.frameworkResponse(viewModel, null, 0, "success !");
    }

    @Operation(summary = "创建指标", description = "创建新的指标记录")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = MetricDetailModel.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "未授权的访问"),
            @ApiResponse(responseCode = "425", description = "未实现!"),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) ")
    @Transactional
    @PostMapping
    public FrameworkResponse<MetricDetailModel, Void> createMetric(
            @Parameter(description = "指标创建模型", required = true) @RequestBody @Validated({
                    Insert.class}) MetricInsertModel model) {
        return FrameworkResponse.extend(metricService.upsertMetric(model));
    }

    @Operation(summary = "查询指标详情", description = "根据指标ID查询单个指标的详细信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = Content.class, subTypes = {
                    MetricDetailModel.class}))),
            @ApiResponse(responseCode = "404", description = "指标不存在"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @GetMapping("/{id}")
    public FrameworkResponse<MetricDetailModel, Void> queryMetric(
            @Parameter(description = "指标ID", required = true) @PathVariable("id") int id) {
        MetricDetailModel metric = metricService.findMetricById(id);
        if (!CollectionUtils.isEmpty(metric.getStatisticPeriods())) {
            metric.setStatisticPeriodValues(metric.getStatisticPeriods().stream()
                    .map(StatisticPeriod::getCode)
                    .collect(Collectors.toList()));
        }
        return FrameworkResponse.frameworkResponse(metric, null, 0, "success !");
    }

}
