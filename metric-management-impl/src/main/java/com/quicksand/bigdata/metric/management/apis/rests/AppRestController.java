package com.quicksand.bigdata.metric.management.apis.rests;

import com.quicksand.bigdata.metric.management.advices.AppAuthRefreshEvent;
import com.quicksand.bigdata.metric.management.apis.models.AppDetailModel;
import com.quicksand.bigdata.metric.management.apis.models.AppModel;
import com.quicksand.bigdata.metric.management.apis.models.AppModifyModel;
import com.quicksand.bigdata.metric.management.apis.models.AppToken;
import com.quicksand.bigdata.metric.management.apis.repos.RelationOfAppAndMetricAutoRepo;
import com.quicksand.bigdata.metric.management.apis.rest.AppManageRestService;
import com.quicksand.bigdata.metric.management.apis.rest.AppRestService;
import com.quicksand.bigdata.metric.management.apis.services.AppService;
import com.quicksand.bigdata.metric.management.apis.utils.AppModelAdapter;
import com.quicksand.bigdata.metric.management.apis.vos.AppVO;
import com.quicksand.bigdata.metric.management.apis.vos.AuthTokenVO;
import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.metric.management.metric.models.DropDownListModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.utils.PageableUtil;
import com.quicksand.bigdata.vars.concurrents.TraceFuture;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.service.VarsSecurityUtilService;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AppRestController
 *
 * @author page
 * @date 2022/9/28
 */
@Slf4j
@Validated
@RestController
// @Api("应用Apis")
@Tag(name = "应用Apis")
public class AppRestController
        implements AppRestService, AppManageRestService {

    @Resource
    AppService appService;
    @Resource
    UserService userService;
    @Resource
    VarsSecurityUtilService varsSecurityUtilService;
    @Resource
    OperationLogService operationLogService;
    @Resource
    MetricService metricService;
    @Resource
    ApplicationContext applicationContext;
    @Resource
    RelationOfAppAndMetricAutoRepo relationOfAppAndMetricAutoRepo;

    /**
     * 创建应用
     *
     * @return list of AppModel
     */
    @Operation(description = "创建应用")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('OP_APP_CREATE')")
    public Response<AppDetailModel> createApp(@Validated(Insert.class) @RequestBody AppModifyModel model) {
        UserSecurityDetails authUser = AuthUtil.getUserDetail();
        if (null == authUser) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        AppVO existApp = appService.findApp(model.getName());
        if (null != existApp) {
            return Response.response(HttpStatus.BAD_REQUEST, String.format("名称为%s的应用已存在!", model.getName()));
        }
        AppVO appVo = appService.createApp(model);
        if (null == appVo) {
            return Response.response(HttpStatus.INTERNAL_SERVER_ERROR, "应用创建失败！");
        } else {
            operationLogService.log(OperationLogType.APP_MODIFY, String.format("创建应用：用户[%s %d]创建应用[%s %d]",
                    authUser.getUsername(), authUser.getId(), appVo.getName(), appVo.getId()));
        }
        return Response.ok(AppModelAdapter.cover2DetailModel(appVo));
    }


    /**
     * 修改应用信息
     *
     * @return list of AppModel
     */
    @Operation(description = "修改应用信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Override
    @PreAuthorize("hasAuthority('OP_APP_MODIFY')")
    public Response<AppDetailModel> modifyApp(@Parameter(name = "id", description = "应用主键") @Min(value = 1, message = "不支持的应用主键！")
                                              @PathVariable("id") int id, @Validated(Update.class) @RequestBody AppModifyModel model) {
        UserSecurityDetails authUser = AuthUtil.getUserDetail();
        if (null == authUser) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        AppVO app = appService.findApp(id);
        if (null == app) {
            return Response.notfound();
        }
        boolean operationEnable = null != app.getOwner() && authUser.getId() == app.getOwner().getId();
        if (!operationEnable) {
            //记录尝试越权操作的业务日志
            operationLogService.log(OperationLogType.OVERSTEP_PERMISSION, String.format("越权拦截：用户[%s %d]尝试修改应用信息[%s %d]",
                    authUser.getUsername(), authUser.getId(), app.getName(), app.getId()));
            return Response.response(HttpStatus.UNAUTHORIZED);
        } else {
            //涉及到改名的
            if (!Objects.equals(app.getName(), model.getName())) {
                if (null != appService.findApp(model.getName())) {
                    return Response.response(HttpStatus.BAD_REQUEST, String.format("名称为%s的应用已存在!", model.getName()));
                }
            }
            AppVO nAppVo = appService.modifyApp(id, model);
            operationLogService.log(OperationLogType.APP_MODIFY, String.format("修改应用信息：用户[%s %d]修改应用信息[%s %d]",
                    authUser.getUsername(), authUser.getId(), app.getName(), app.getId()));
            return Response.ok(AppModelAdapter.cover2DetailModel(nAppVo));
        }
    }

    /**
     * 更换app的访问token
     *
     * @param id 应用主键
     * @return token:String
     */
    @Operation(description = "更换app的访问token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('OP_APP_MODIFY_TOKEN')")
    public Response<AppToken> exchangeAppToken(@Parameter(name = "id", description = "应用主键") @Min(value = 1, message = "不支持的应用主键！")
                                               @PathVariable("id") int id) {
        UserSecurityDetails authUser = AuthUtil.getUserDetail();
        if (null == authUser) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        AppVO app = appService.findApp(id);
        if (null == app) {
            return Response.notfound();
        }
        boolean operationEnable = AuthUtil.isAdmin()
                || (null != app.getOwner() && authUser.getId() == app.getOwner().getId());
        if (!operationEnable) {
            //记录尝试越权操作的业务日志
            operationLogService.log(OperationLogType.OVERSTEP_PERMISSION, String.format("越权拦截：用户[%s %d]尝试更换应用token[%s %d]",
                    authUser.getUsername(), authUser.getId(), app.getName(), app.getId()));
            return Response.response(HttpStatus.UNAUTHORIZED);
        } else {
            //执行删除，并记录日志
            AuthTokenVO oldToken = app.getToken();
            AuthTokenVO newToken = appService.exchangeToken(id, oldToken);
            operationLogService.log(OperationLogType.APP_MODIFY_TOKEN, String.format("更换Token：用户[%s %d]更换应用token[%s %d],启用新token[%s %d],原token[%s %d]作废！",
                    authUser.getUsername(), authUser.getId(), app.getName(), app.getId(), newToken.getToken(), newToken.getId(),
                    null == oldToken ? "" : oldToken.getToken(), null == oldToken ? -1 : oldToken.getId()));
            return Response.ok(AppToken.builder().token(newToken.getToken()).build());
        }
    }


    /**
     * 删除应用
     *
     * @param id 应用主键
     * @return void
     */
    @Operation(description = "删除应用")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success!"),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "425", description = "未实现!"),
    })
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APP_DELETE')")
    public Response<Void> removeApp(@Parameter(name = "id", description = "应用主键")
                                    @Min(value = 1, message = "不支持的应用主键！")
                                    @PathVariable("id")
                                    int id) {
        UserSecurityDetails authUser = AuthUtil.getUserDetail();
        if (null == authUser) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        AppVO app = appService.findApp(id);
        if (null == app) {
            return Response.notfound();
        }
        boolean operationEnable = AuthUtil.isAdmin()
                || (null != app.getOwner() && authUser.getId() == app.getOwner().getId());
        if (!operationEnable) {
            //记录尝试越权操作的业务日志
            operationLogService.log(OperationLogType.OVERSTEP_PERMISSION, String.format("越权拦截：用户[%s %d]尝试删除应用[%s %d]",
                    authUser.getUsername(), authUser.getId(), app.getName(), app.getId()));
            return Response.response(HttpStatus.UNAUTHORIZED);
        } else {
            //执行删除，并记录日志
            appService.removeApp(id);
            operationLogService.log(OperationLogType.APP_DELETE, String.format("删除应用：用户[%s %d]删除应用[%s %d]",
                    authUser.getUsername(), authUser.getId(), app.getName(), app.getId()));
            TraceFuture.run(() -> {
                TimeUnit.MILLISECONDS.sleep(500L);
                applicationContext.publishEvent(new AppAuthRefreshEvent(app.getId()));
            });
            return Response.ok();
        }
    }


    /**
     * 获取应用信息
     *
     * @return list of AppModel
     */
    @Operation(description = "获取应用信息列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success!"),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "425", description = "未实现!"),
    })
    @Override
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APP_LIST') || @varsSecurityUtilService.isAnonymousUser(authentication)")
    public Response<PageImpl<AppModel>> fetchApps(@Parameter(name = "pageNo", description = "页码（最小1）") @Min(value = 1, message = "最小pageNo为1！")
                                                  @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                  @Parameter(name = "pageSize", description = "页容量（最小1）") @Min(value = 1, message = "最小pageSize为1！")
                                                  @Max(value = 200, message = "最大pageSize不超过200")
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                  @Parameter(name = "nameKeyword", description = "名称关键字")
                                                  @RequestParam(name = "nameKeyword", required = false, defaultValue = "") String nameKeyword,
                                                  @Parameter(name = "ownerIds", description = "负责人ids(多个采用半角逗号分隔)")
                                                  @RequestParam(name = "ownerIds", required = false) List<Integer> ownerIds,
                                                  @Parameter(name = "appTypes", description = "应用类型ids(多个采用半角逗号分隔)")
                                                  @RequestParam(name = "appTypes", required = false) List<Integer> appTypes,
                                                  @Parameter(name = "ownerNameKeyword", description = "负责人名称关键字(可模糊搜索)")
                                                  @RequestParam(name = "ownerNameKeyword", required = false, defaultValue = "") String ownerNameKeyword) {
        boolean fetchAll = AuthUtil.isAdmin() || varsSecurityUtilService.isAnonymousUser(AuthUtil.getAuthentication());
        int userId = null == AuthUtil.getUserDetail() ? 0 : AuthUtil.getUserDetail().getId();
        if (!fetchAll
                && 0 >= userId) {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
        //前置处理
        List<Integer> filterOwnerIds = new ArrayList<>();
        if (!fetchAll) {
            filterOwnerIds.add(userId);
        } else if (!CollectionUtils.isEmpty(ownerIds)) {
            filterOwnerIds.addAll(ownerIds);
        }
        if (StringUtils.hasText(ownerNameKeyword)) {
            List<UserVO> nameLikes = userService.findUsersNameLike(ownerNameKeyword);
            if (CollectionUtils.isEmpty(nameLikes)) {
                return Response.ok(PageImpl.buildEmptyPage(pageNo, pageSize));
            } else {
                filterOwnerIds = nameLikes.stream()
                        .map(UserVO::getId)
                        .filter(filterOwnerIds::contains)
                        .collect(Collectors.toList());
            }
        }
        PageImpl<AppModel> appModelPage = PageableUtil.page2page(appService.fetchApps(nameKeyword, filterOwnerIds, appTypes, pageNo, pageSize),
                AppModel.class, AppModelAdapter::cover2Model);
        if (!CollectionUtils.isEmpty(appModelPage.getItems())) {
            List<Map<String, Long>> appId2Counts = relationOfAppAndMetricAutoRepo.findByAppIds(appModelPage.getItems().stream()
                    .map(AppModel::getId)
                    .collect(Collectors.toList()));
            if (!CollectionUtils.isEmpty(appId2Counts)) {
                Map<Integer, Integer> id2Counts = appId2Counts.stream()
                        .collect(Collectors.toMap(k -> Integer.parseInt(String.valueOf(k.get("app_id"))), v -> Integer.parseInt(String.valueOf(v.get("total")))));
                appModelPage.getItems()
                        .forEach(v -> v.setMetricsCount(null == id2Counts.get(v.getId()) ? 0 : id2Counts.get(v.getId())));
            }
        } else {
            appModelPage.getItems().forEach(v -> v.setMetricsCount(0));
        }
        return Response.ok(appModelPage);
    }

    /**
     * 获取所有应用列表
     *
     * @return list of AppModel
     */
    @Operation(description = "获取所有应用列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success!"),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "425", description = "未实现!"),
    })
    @Override
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APP_LIST') || @varsSecurityUtilService.isAnonymousUser(authentication)")
    public Response<List<AppModel>> fetchAppList() {
        boolean fetchAll = AuthUtil.isAdmin() || varsSecurityUtilService.isAnonymousUser(AuthUtil.getAuthentication());
        List<AppModel> appModels = appService.fetchAppList(fetchAll).stream()
                .map(AppModelAdapter::cover2Model)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(appModels)) {
            List<Map<String, Long>> appId2Counts = relationOfAppAndMetricAutoRepo.findByAppIds(appModels.stream()
                    .map(AppModel::getId)
                    .collect(Collectors.toList()));
            if (!CollectionUtils.isEmpty(appId2Counts)) {
                Map<Integer, Integer> id2Counts = appId2Counts.stream()
                        .collect(Collectors.toMap(k -> Integer.parseInt(String.valueOf(k.get("app_id"))), v -> Integer.parseInt(String.valueOf(v.get("total")))));
                appModels.forEach(v -> v.setMetricsCount(null == id2Counts.get(v.getId()) ? 0 : id2Counts.get(v.getId())));
            } else {
                appModels.forEach(v -> v.setMetricsCount(0));
            }
        }
        return Response.ok(appModels);
    }

    /**
     * 获取应用的详细信息
     *
     * @param appId 应用id
     * @return instance of AppDetailModel
     */
    @Operation(description = "获取应用详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success!"),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "425", description = "未实现!"),
    })
    @Override
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APP_LIST') || @varsSecurityUtilService.isAnonymousUser(authentication)")
    public Response<AppDetailModel> findAppInfo(@Parameter(name = "id", description = "应用主键") @Min(value = 1, message = "不支持的应用主键！")
                                                @PathVariable("id") int appId) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        AppVO appVO = appService.findApp(appId);
        if (null == appVO) {
            if (null == userDetail) {
                return Response.notfound();
            } else {
                return Response.ok();
            }
        }
        //验证用户是否有权限查看app
        if (AuthUtil.isAdmin()
                || varsSecurityUtilService.isAnonymousUser(AuthUtil.getAuthentication())
                || (null != appVO.getOwner() && null != userDetail && userDetail.getId() == appVO.getOwner().getId())) {
            return Response.ok(AppModelAdapter.cover2DetailModel(appVO));
        } else {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 根据appId分页获取指标数据
     *
     * @return page of MetricOverviewModel
     */
    @Operation(description = "获取应用指标信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success!"),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "425", description = "未实现!"),
    })
    @Override
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APP_LIST') || @varsSecurityUtilService.isAnonymousUser(authentication)")
    public Response<PageImpl<MetricOverviewModel>> findAppMetrics(@Parameter(name = "id", description = "应用主键")
                                                                  @Min(value = 1, message = "不支持的应用主键！")
                                                                  @PathVariable("id") int id,
                                                                  @Parameter(name = "pageNo", description = "页码（最小1）") @Min(value = 1, message = "最小pageNo为1！")
                                                                  @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                                  @Parameter(name = "pageSize", description = "页容量（最小1）") @Min(value = 1, message = "最小pageSize为1！")
                                                                  @Max(value = 200, message = "最大pageSize不超过200")
                                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                                  @Parameter(name = "effective", description = "收否生效 1生效 0失效") @Min(value = 0, message = "")
                                                                  @Max(value = 1, message = "")
                                                                  @RequestParam(name = "effective", required = false, defaultValue = "1") Integer effective,
                                                                  @Parameter(name = "metricKeyword", description = "指标名称(可模糊搜索)")
                                                                  @RequestParam(name = "metricKeyword", required = false, defaultValue = "") String metricKeyword) {
        AppVO appVO = appService.findApp(id);
        if (null == appVO) {
            if (null == AuthUtil.getUserDetail()) {
                return Response.notfound();
            } else {
                return Response.ok();
            }
        }
        //验证用户是否有权限查看app
        if (AuthUtil.isAdmin()
                || varsSecurityUtilService.isAnonymousUser(AuthUtil.getAuthentication())
                || (null != appVO.getOwner() && null != AuthUtil.getUserDetail() && AuthUtil.getUserDetail().getId() == appVO.getOwner().getId())) {
            return Response.ok(PageableUtil.page2page(appService.findMetrics(id, metricKeyword, effective, pageNo, pageSize),
                    MetricOverviewModel.class, v -> JsonUtils.transfrom(v, MetricOverviewModel.class)));
        } else {
            return Response.response(HttpStatus.UNAUTHORIZED);
        }

    }

    /**
     * 获取应用下拉列表
     *
     * @return List<DropDownListModel>
     */
    @Operation(description = "获取应用下拉列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success!"),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "425", description = "未实现!"),
    })
    @Override
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APP_LIST') || @varsSecurityUtilService.isAnonymousUser(authentication)")
    public Response<List<DropDownListModel>> getAppsDropDownList() {
        boolean fetchAll = AuthUtil.isAdmin() || varsSecurityUtilService.isAnonymousUser(AuthUtil.getAuthentication());
        return Response.ok(appService.findUserApps(fetchAll));
    }

    /**
     * 获取应用可选指标下拉列表
     *
     * @return List<DropDownListModel>
     */
    @Operation(description = "获取应用可选指标下拉列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success!"),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found"),
            @ApiResponse(responseCode = "425", description = "未实现!"),
    })
    @Override
    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_APP_LIST') || @varsSecurityUtilService.isAnonymousUser(authentication)")
    public Response<List<DropDownListModel>> getAppMetricsDropDownList(@PathVariable("appId") @Min(value = 1L, message = "不存在的应用! ") int appId) {
        AppVO app = appService.findApp(appId);
        if (null == app) {
            return Response.notfound();
        }
        return Response.ok(metricService.getAppMetricsDropDownList(appId));
    }

}
