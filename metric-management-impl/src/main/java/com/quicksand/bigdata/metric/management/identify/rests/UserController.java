package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.UserDetailsModel;
import com.quicksand.bigdata.metric.management.identify.models.UserMixDetailModel;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.services.PermissionService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.UserModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.OperationLogVO;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OxlUserController
 *
 * @author page
 * @date 2020/8/18 16:24
 */
@CrossOrigin
// @Api("用户数据Apis")
@Tag(name = "用户数据Apis")
@RestController
public class UserController
        implements UserRestService {

    @Resource
    UserService userService;
    @Resource
    OperationLogService operationLogService;

    @Operation(description = "获取用户信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PreAuthorize("hasAuthority('OP_USER_LIST') || @varsSecurityUtilService.userMatched(#userId,authentication) || @varsSecurityUtilService.isAnonymousUser(authentication)")
    @Override
    public Response<UserMixDetailModel> queryUserMixInfo(@PathVariable("userId") int userId) {
        UserVO user = userService.queryUser(userId);
        if (null == user) {
            return Response.response(HttpStatus.NOT_FOUND);
        }
        List<PermissionVO> permissionVos = PermissionService.resolveUserPermission(user, false);
        //拼装model结构
        return Response.ok(
                UserModelAdapter.cover2MixModel(user, user.getRoles(),
                        permissionVos.stream().filter(k -> PermissionType.MENU.equals(k.getType())).collect(Collectors.toList()),
                        permissionVos));
    }

    @Operation(description = "获取用户列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！")
    })
    @PreAuthorize("hasAuthority('OP_USER_LIST') || @varsSecurityUtilService.isAnonymousUser(authentication)")
    @Override
    public Response<PageImpl<UserDetailsModel>> listUserDetails(@RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                                @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                                @RequestParam(name = "keyword", required = false) String keyword,
                                                                @RequestParam(name = "roleId", required = false) Integer roleId) {
        if (1 > pageNo || 0 >= pageSize) {
            return Response.ok(PageImpl.buildEmptyPage(pageNo, pageSize));
        }
        PageImpl<UserVO> userVOPage = null;
        if (!StringUtils.hasText(keyword)
                && (null == roleId || 0 >= roleId)) {
            userVOPage = userService.queryUserByPage(pageNo, pageSize);
        } else {
            userVOPage = userService.queryUserByPage(pageNo, pageSize, keyword, roleId);
        }
        if (null == userVOPage) {
            return Response.ok(PageImpl.buildEmptyPage(pageNo, pageSize));
        }
        //将page转换为model，吐出
        PageImpl<UserDetailsModel> pageModel = PageImpl.buildEmptyPage(pageNo, pageSize);
        pageModel.setTotal(userVOPage.getTotal());
        pageModel.setTotalPage(userVOPage.getTotalPage());
        if (!CollectionUtils.isEmpty(userVOPage.getItems())) {
            List<UserDetailsModel> collect = userVOPage.getItems()
                    .stream()
                    .map(UserModelAdapter::cover2DetailModel)
                    .collect(Collectors.toList());
            Map<Integer, UserDetailsModel> id2udm = collect.stream().collect(Collectors.toMap(UserDetailsModel::getId, v -> v));
            //查询用户的最后登陆时间
            List<OperationLogVO> logVos = operationLogService.queryLastLoginByUserIds(id2udm.keySet());
            if (!CollectionUtils.isEmpty(logVos)) {
                Map<Integer, Date> userId2OperationTime = logVos.stream().collect(Collectors.toMap(OperationLogVO::getUserId, OperationLogVO::getOperationTime));
                id2udm.forEach((k, v) -> {
                    Date date = userId2OperationTime.get(v.getId());
                    if (null != date) {
                        date = new Date(date.getTime());
                        v.setLastLoginTime(date);
                    }
                });
            }
            pageModel.setItems(collect);
        }
        return Response.ok(pageModel);
    }

}
