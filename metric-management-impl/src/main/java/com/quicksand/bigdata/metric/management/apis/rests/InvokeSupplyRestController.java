package com.quicksand.bigdata.metric.management.apis.rests;

import com.google.common.collect.Lists;
import com.quicksand.bigdata.metric.management.advices.AppAuthRefreshEvent;
import com.quicksand.bigdata.metric.management.apis.models.*;
import com.quicksand.bigdata.metric.management.apis.rest.InvokeSupplyRestManageService;
import com.quicksand.bigdata.metric.management.apis.rest.InvokeSupplyRestService;
import com.quicksand.bigdata.metric.management.apis.services.AppService;
import com.quicksand.bigdata.metric.management.apis.services.InvokeApplyService;
import com.quicksand.bigdata.metric.management.apis.utils.AppModelAdapter;
import com.quicksand.bigdata.metric.management.apis.vos.AppVO;
import com.quicksand.bigdata.metric.management.apis.vos.InvokeApplyRecordVO;
import com.quicksand.bigdata.metric.management.apis.vos.RelationOfAppAndMetricVO;
import com.quicksand.bigdata.metric.management.consts.ApproveState;
import com.quicksand.bigdata.metric.management.consts.CompareSymbol;
import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.identify.utils.UserModelAdapter;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import com.quicksand.bigdata.metric.management.metric.repos.MetricAutoRepo;
import com.quicksand.bigdata.metric.management.metric.vos.MetricDimensionVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricMeasureVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.utils.PageableUtil;
import com.quicksand.bigdata.vars.concurrents.TraceFuture;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.quicksand.bigdata.metric.management.identify.utils.AuthUtil.hasAuthority;

/**
 * InvokeSupplyRestController
 *
 * @author page
 * @date 2022/10/11
 */
@Validated
@Slf4j
@RestController
@Tag(name = "应用调用申请服务")
public class InvokeSupplyRestController
        implements InvokeSupplyRestService, InvokeSupplyRestManageService {

    @Resource
    AppService appService;
    @Resource
    InvokeApplyService invokeApplyService;
    @Resource
    MetricAutoRepo metricAutoRepo;
    @Resource
    OperationLogService operationLogService;
    @Resource
    ApplicationContext applicationContext;

    @Value("${vars.metric.platform.url}")
    String apiPlatformUrl;

    /**
     * 调用申请-审核操作
     * 只有指标业务负责人可以审核
     *
     * @return instance of InvokeApplyDetailModel
     */
    @SneakyThrows
    @Operation(description = "调用申请-审核操作")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Transactional
    @PreAuthorize("hasAuthority('OP_APP_INVOKE_APPROVE')")
    @Override
    public Response<InvokeApplyDetailModel> approve(@Parameter(name = "id", description = "申请记录id")
                                                    @Min(value = 1L, message = "非法的申请Id")
                                                    @PathVariable("id") int id,
                                                    @Parameter(name = "审核参数")
                                                    @NotNull
                                                    @RequestBody InvokeApplyApproveModel model) {
        InvokeApplyRecordVO record = invokeApplyService.findApplyRecord(id);
        if (null == record) {
            return Response.response(HttpStatus.NOT_FOUND, "申请记录不存在！");
        }
        if (!Objects.equals(ApproveState.DEFAULT, record.getApprovedState())) {
            return Response.response(HttpStatus.BAD_REQUEST, String.format("申请记录已处理！id：%d", id));
        }
        if (null == record.getApp()) {
            return Response.response(HttpStatus.NOT_FOUND, "申请的应用不存在！");
        }
        InvokeApplyRecordVO approve = invokeApplyService.approve(id, ApproveState.cover(model.getState()), StringUtils.hasText(model.getApproveComment()) ? model.getApproveComment() : "");
        boolean approved = Objects.equals(ApproveState.APPROVED, ApproveState.cover(model.getState()));
        operationLogService.log(approved ? OperationLogType.INVOKE_APPLY_APPROVED : OperationLogType.INVOKE_APPLY_REJECT,
                String.format(approved ? "调用申请-核准: 应用[%s %d]申请调用指标[%s]" : "调用申请-拒绝: 应用[%s %d]申请调用指标[%s]",
                        record.getApp().getName(), record.getApp().getId(),
                        StringUtils.collectionToCommaDelimitedString(record.getMetrics().stream()
                                .filter(Objects::nonNull)
                                .map(v -> v.getEnName() + " " + v.getId())
                                .collect(Collectors.toList()))));
        TraceFuture.run(() -> {
            TimeUnit.MILLISECONDS.sleep(500L);
            applicationContext.publishEvent(new AppAuthRefreshEvent(record.getApp().getId()));
        });
        return Response.ok(cover2Model(approve));
    }

    /**
     * 分页查询市场指标
     * （已上线的指标）
     *
     * @return page of InvokeApplyDetailModel
     */
    @Operation(description = "分页查询市场指标")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APP_INVOKE_LIST')")
    @Override
    public Response<PageImpl<MetricOverviewModel>> listMetrics4Market(@Parameter(name = "pageNo", description = "页码(最小1),可选")
                                                                      @Min(value = 1, message = "最小pageNo为1！")
                                                                      @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                                      @Parameter(name = "pageSize", description = "页容量(最小1),可选")
                                                                      @Min(value = 1, message = "最小pageSize为1！") @Max(value = 200, message = "最大pageSize不超过200")
                                                                      @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                                      @Parameter(name = "keyword", description = "指标名称/英文名/别名模糊搜索关键字")
                                                                      @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                                                                      @Parameter(name = "techOwnerIds", description = "技术负责人Ids，可选")
                                                                      @RequestParam(name = "techOwnerIds", required = false) List<Integer> techOwnerIds,
                                                                      @Parameter(name = "businessOwnerIds", description = "业务负责人Ids，可选")
                                                                      @RequestParam(name = "businessOwnerIds", required = false) List<Integer> businessOwnerIds,
                                                                      @Parameter(name = "appId", description = "应用Id,用于检查是否有调用权限")
                                                                      @RequestParam(name = "appId", required = false) Integer appId) {
        //todo
        org.springframework.data.domain.Page<MetricVO> metrics;
        String filterKeyword = StringUtils.hasText(keyword) ? "%" + keyword + "%" : "%";
        if (CollectionUtils.isEmpty(techOwnerIds) && CollectionUtils.isEmpty(businessOwnerIds)) {
            metrics = metricAutoRepo.findPubMetricsByKeyword(filterKeyword, PageRequest.of(pageNo - 1, pageSize))
                    .map(v -> JsonUtils.transfrom(v, MetricVO.class));
        } else if (!CollectionUtils.isEmpty(techOwnerIds) && !CollectionUtils.isEmpty(businessOwnerIds)) {
            metrics = metricAutoRepo.findPubMetricsByKeywordAndOwnerIds(filterKeyword, businessOwnerIds, techOwnerIds, PageRequest.of(pageNo - 1, pageSize))
                    .map(v -> JsonUtils.transfrom(v, MetricVO.class));
        } else if (CollectionUtils.isEmpty(techOwnerIds)) {
            metrics = metricAutoRepo.findPubMetricsByKeywordAndbusinessOwnerIds(filterKeyword, businessOwnerIds, PageRequest.of(pageNo - 1, pageSize))
                    .map(v -> JsonUtils.transfrom(v, MetricVO.class));
        } else {
            metrics = metricAutoRepo.findPubMetricsByKeywordAndtechOwnerIds(filterKeyword, techOwnerIds, PageRequest.of(pageNo - 1, pageSize))
                    .map(v -> JsonUtils.transfrom(v, MetricVO.class));
        }
        List<Integer> curPageMetricIds = metrics.getContent().stream()
                .map(MetricVO::getId)
                .collect(Collectors.toList());
        if (null != appId && 0 < appId && !CollectionUtils.isEmpty(curPageMetricIds)) {
            List<Integer> effectiveMetricIds = invokeApplyService.findAllEffectiveRelasByAppId(appId)
                    .stream()
                    .map(RelationOfAppAndMetricVO::getId)
                    .collect(Collectors.toList());
            curPageMetricIds.retainAll(effectiveMetricIds);
        } else {
            curPageMetricIds.clear();
        }
        return Response.ok(PageableUtil.page2page(metrics.map(v -> JsonUtils.transfrom(v, MetricVO.class)), MetricOverviewModel.class,
                v -> {
                    MetricOverviewModel m = JsonUtils.transfrom(v, MetricOverviewModel.class);
                    m.setInvokeEnable(curPageMetricIds.contains(m.getId()) ? 1 : 0);
                    return m;
                }));
    }

    /**
     * 分页查询调用申请
     * 只有指标业务负责人有审批权限
     *
     * @return page of InvokeApplyDetailModel
     */
    @Operation(description = "分页查询审批列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APPROVAL_LIST')")
    @Override
    public Response<PageImpl<InvokeApplyDetailModel>> listApplys(@Parameter(name = "pageNo", description = "页码(最小1),可选")
                                                                 @Min(value = 1, message = "最小pageNo为1！")
                                                                 @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                                 @Parameter(name = "pageSize", description = "页容量(最小1),可选")
                                                                 @Min(value = 1, message = "最小pageSize为1！") @Max(value = 200, message = "最大pageSize不超过200")
                                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                                 @Parameter(name = "metricKeyword", description = "指标名称")
                                                                 @RequestParam(name = "metricKeyword", required = false) String metricKeyword,
                                                                 @Parameter(name = "appIds", description = "应用Id 可选,多选")
                                                                 @RequestParam(name = "appIds", required = false) List<Integer> appIds,
                                                                 @Parameter(name = "applyUsers", description = "提交人用户Id，可选,多选")
                                                                 @RequestParam(name = "applyUsers", required = false) List<Integer> applyUsers,
                                                                 @Parameter(name = "states", description = "审批状态 0未审核 1核准 2拒绝 可选,多选")
                                                                 @RequestParam(name = "states", required = false) List<Integer> states) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        if (null == userDetail) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        PageRequest requestPage = PageRequest.of(pageNo - 1, pageSize);
        requestPage = requestPage.withSort(Sort.Direction.DESC, "updateTime");
        return Response.ok(PageableUtil.page2page(
                invokeApplyService.fetchApproveLists(appIds, applyUsers, states, metricKeyword, requestPage),
                InvokeApplyDetailModel.class, this::cover2Model));
    }

    /**
     * 我提交的审批
     *
     * @return page of InvokeApplyDetailModel
     */
    @Operation(description = "我提交的审批")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('ME_MY_APPROVAL')")
    @Override
    public Response<PageImpl<InvokeApplyDetailModel>> myApplys(@Parameter(name = "pageNo", description = "页码(最小1),可选")
                                                               @Min(value = 1, message = "最小pageNo为1！")
                                                               @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                               @Parameter(name = "pageSize", description = "页容量(最小1),可选")
                                                               @Min(value = 1, message = "最小pageSize为1！") @Max(value = 200, message = "最大pageSize不超过200")
                                                               @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                               @Parameter(name = "metricKeyword", description = "指标名称")
                                                               @RequestParam(name = "metricKeyword", required = false) String metricKeyword,
                                                               @Parameter(name = "appIds", description = "应用Id 可选,多选")
                                                               @RequestParam(name = "appIds", required = false) List<Integer> appIds,
                                                               @Parameter(name = "states", description = "审批状态 0未审核 1核准 2拒绝 可选,多选")
                                                               @RequestParam(name = "states", required = false) List<Integer> states) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        if (null == userDetail) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        List<Integer> filterUserIds = Lists.newArrayList(userDetail.getId());
        List<ApproveState> fiterStates;
        if (CollectionUtils.isEmpty(states)) {
            fiterStates = Lists.newArrayList(ApproveState.DEFAULT, ApproveState.APPROVED, ApproveState.REJECT);
        } else {
            fiterStates = states.stream().map(ApproveState::cover).collect(Collectors.toList());
        }
        return Response.ok(PageableUtil.page2page(
                invokeApplyService.fetchApplys(appIds, filterUserIds, fiterStates, metricKeyword, pageNo, pageSize),
                InvokeApplyDetailModel.class, this::cover2Model));
    }

    /**
     * 获取申请详情
     *
     * @return instance of InvokeApplyDetailModel
     */
    @Operation(description = "获取申请详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_APP_INVOKE_LIST') || hasAuthority('OP_APPROVAL_DETAIL')")
    @Override
    public Response<InvokeApplyDetailModel> findApply(@Parameter(name = "id", description = "申请Id")
                                                      @Min(value = 1, message = "最小pageNo为1！")
                                                      @PathVariable("id") int id) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        if (null == userDetail) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        InvokeApplyRecordVO record = invokeApplyService.findApplyRecord(id);
        if (null == record) {
            return Response.response(HttpStatus.NOT_FOUND, String.format("申请[%d]不存在！", id));
        }
        if (!hasAuthority("OP_APP_INVOKE_APPROVE")
                && userDetail.getId() != record.getCreateUser().getId()
                && userDetail.getId() != record.getApp().getOwner().getId()) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        return Response.ok(cover2Model(record));
    }

    /**
     * 新建指标调用申请
     *
     * @return instance of InvokeApplyDetailModel
     */
    @Operation(description = "新建调用申请")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Transactional
    @PreAuthorize("hasAuthority('OP_APP_INVOKE_APPLY')")
    @Override
    public Response<List<InvokeApplyDetailModel>> applyInvoke(@Validated @RequestBody InvokeApplyModel model) {
        AppVO app = appService.findApp(model.getAppId());
        if (null == app) {
            return Response.response(HttpStatus.NOT_FOUND, String.format("应用[%d]不存在！", model.getAppId()));
        }
        List<MetricDBVO> hitMetrics = metricAutoRepo.findAllById(model.getMetricIds());
        if (CollectionUtils.isEmpty(hitMetrics)
                || model.getMetricIds().size() != hitMetrics.size()) {
            return Response.response(HttpStatus.BAD_REQUEST, "选中指标部分/全部不存在！");
        }
        List<InvokeApplyRecordVO> applyList = invokeApplyService.createApply(model);
        operationLogService.log(OperationLogType.INVOKE_APPLY, String.format("调用申请: 应用[%s %d]申请调用指标[%s]", app.getName(), app.getId(),
                StringUtils.collectionToCommaDelimitedString(hitMetrics.stream().map(v -> v.getEnName() + " " + v.getId()).collect(Collectors.toList()))));
        return Response.ok(applyList.stream().map(v -> cover2Model(v)).collect(Collectors.toList()));
    }

    @Operation(description = "获取接口的调用信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Transactional
    @PreAuthorize("hasAuthority('OP_APP_INVOKE_LIST')")
    @Override
    public Response<MetricInvokeInfosModel> invokeInfos(@Parameter(name = "id", description = "指标Id")
                                                        @Min(value = 1, message = "不存在的指标！")
                                                        @PathVariable("id") int id) {
        MetricVO metric = metricAutoRepo.findById(id)
                .map(v -> JsonUtils.transfrom(v, MetricVO.class))
                .orElse(null);
        if (null == metric) {
            return Response.response(HttpStatus.NOT_FOUND, String.format("指标[%d]不存在!", id));
        }
        if (!Objects.equals(PubsubStatus.Online, metric.getPubsub())) {
            return Response.response(HttpStatus.NOT_ACCEPTABLE, String.format("指标[%d]未上线，无法提供调用！", id));
        }
        //构造调用接口的数据
        return Response.ok(buildInvokeInfos(metric));
    }

    private MetricInvokeInfosModel buildInvokeInfos(MetricVO metricVO) {
        List<String> orderNames = metricVO.getMeasures()
                .stream()
                .map(MetricMeasureVO::getName)
                .collect(Collectors.toList());
        orderNames.addAll(metricVO.getDimensions()
                .stream()
                .map(MetricDimensionVO::getName)
                .collect(Collectors.toList()));

        MetricInvokeInfoModel cviInterface = MetricInvokeInfoModel.builder()
                // .interfaceUrl(String.format("%s/stit/metrics/cvi/%d", apiPlatformUrl, metricVO.getId()))
                .interfaceUrl(String.format("%s/stit/metrics/cvi", apiPlatformUrl))
                .name("查询全部可用参数接口(GET)")
                .method("GET")
                .requestParameters(Lists.newArrayList(
                        InvokeParamModel.builder()
                                .name("keyword")
                                .type("String")
                                .description("指标英文名称/指标编号")
                                .isRequired(true)
                                .candidateValues("")
                                .build(),
                        InvokeParamModel.builder()
                                .name("dimensions")
                                .type("String")
                                .description("纬度名称，支持多选，半角逗号分隔")
                                .isRequired(true)
                                .candidateValues("")
                                .build()
                ))
                .build();

        ExplainAttributesModel exampleJsonModel = ExplainAttributesModel.builder()
                // .metricIds(Lists.newArrayList(metricVO.getId()))
                .metrics(Collections.singletonList(metricVO.getSerialNumber()))
                .dimensions(metricVO.getDimensions().stream().map(MetricDimensionVO::getName).collect(Collectors.toList()))
                .condition(ConditionModel.builder()
                        .name(metricVO.getDimensions().get(0).getName())
                        .symbol(CompareSymbol.NOT_EQUALS.getCode())
                        .hitValues(Lists.newArrayList(""))
                        .build())
                .sorts(Lists.newArrayList(SortModel.builder().asc(true).name(metricVO.getDimensions().get(0).getName()).build()))
                .build();

        MetricInvokeInfoModel qviInterface = MetricInvokeInfoModel.builder()
                .name("获得数据接口(POST)")
                .method("POST")
                // .interfaceUrl(String.format("%s/stit/metrics/rqi/%d", apiPlatformUrl, metricVO.getId()))
                .interfaceUrl(String.format("%s/stit/metrics/rqi", apiPlatformUrl))
                .headersParameters(Lists.newArrayList(InvokeParamModel.builder()
                        .name("Content-Type")
                        .type("String")
                        .description("请求Content类型")
                        .isRequired(true)
                        .candidateValues("application/json")
                        .build()))
                .exampleJson(JsonUtils.toJson(exampleJsonModel))
                .requestParameters(Lists.newArrayList(
                        // InvokeParamModel.builder()
                        //         .name("metricIds")
                        //         .type("List<Integer>")
                        //         .description("指标Id(可选，与metrics二选一)")
                        //         .isRequired(false)
                        //         .candidateValues("")
                        //         .build(),
                        InvokeParamModel.builder()
                                .name("metrics")
                                .type("List<String>")
                                .description("英文名称/编号")
                                .isRequired(true)
                                .candidateValues("")
                                .build(),
                        InvokeParamModel.builder()
                                .name("dimensions")
                                .type("List<String>")
                                .description("纬度名称，支持多选")
                                .isRequired(true)
                                .candidateValues("")
                                .build(),
                        InvokeParamModel.builder()
                                .name("condition")
                                .type("Condition")
                                .description("纬度筛选条件，详见示例")
                                .isRequired(false)
                                .candidateValues("")
                                .build(),
                        InvokeParamModel.builder()
                                .name("sorts")
                                .type("List<Sort>")
                                .description("排序项目，详见示例")
                                .isRequired(false)
                                .candidateValues("")
                                .build()
                ))
                .build();

        return MetricInvokeInfosModel.builder()
                .headersParameters(Lists.newArrayList(
                        InvokeParamModel.builder()
                                .name("METRICS-APP")
                                .type("String")
                                .description("调用应用的token")
                                .isRequired(true)
                                .candidateValues("")
                                .build()
                ))
                .candidateValueInterface(cviInterface)
                .resultQueryInterface(qviInterface)
                .interfacesInfo(Lists.newArrayList(cviInterface, qviInterface))
                .build();
    }


    /**
     * cover2Model
     *
     * @param vo InvokeApplyRecordVO
     * @return InvokeApplyDetailModel
     */
    private InvokeApplyDetailModel cover2Model(InvokeApplyRecordVO vo) {
        return InvokeApplyDetailModel.builder()
                .id(vo.getId())
                .app(AppModelAdapter.cover2Model(vo.getApp()))
                .metrics(vo.getMetrics().stream()
                        .filter(Objects::nonNull)
                        .map(v -> JsonUtils.transfrom(v, MetricOverviewModel.class))
                        .collect(Collectors.toList()))
                .createUser(null == vo.getCreateUser() ? null : UserModelAdapter.cover2OverviewModel(vo.getCreateUser()))
                .description(StringUtils.hasText(vo.getDescription()) ? vo.getDescription() : "")
                .approvedComment(StringUtils.hasText(vo.getApprovedComment()) ? vo.getApprovedComment() : "")
                .approvedUserId(vo.getApprovedUserId())
                .approvedState(vo.getApprovedState())
                .approvedType(vo.getApprovedType())
                .createTime(vo.getCreateTime())
                .updateUserId(vo.getUpdateUserId())
                .updateTime(vo.getUpdateTime())
                .qpd(vo.getQpd())
                .qps(vo.getQps())
                .tp99(vo.getTp99())
                .build();
    }

}
