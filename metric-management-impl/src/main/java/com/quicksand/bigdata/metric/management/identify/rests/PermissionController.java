package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.PermissionOverviewModel;
import com.quicksand.bigdata.metric.management.identify.services.PermissionService;
import com.quicksand.bigdata.metric.management.identify.services.RoleService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.PermissionModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PermissionController
 *
 * @Author page
 * @Date 2020/8/18 16:35
 */
@RestController
@Tag(name = "权限服务apis")
public class PermissionController
        implements PermissionRestService {

    @Resource
    UserService userService;
    @Resource
    PermissionService permissionService;
    @Resource
    RoleService roleService;

    @PreAuthorize(" hasRole('ADMIN') || hasAuthority('OP_PERMISSION_LIST')")
    @Override
    public Response<List<PermissionOverviewModel>> queryAllPermissions() {
        List<PermissionVO> permissionVOS = permissionService.queryAllPermissions(null);
        if (CollectionUtils.isEmpty(permissionVOS)) {
            return Response.ok();
        }
        return Response.ok(PermissionModelAdapter.coverPermissionChains(permissionVOS));
    }

    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OPERATION_USER_LIST') OR @varsSecurityUtilService.userMatched(#userId,authentication)")
    @Override
    public Response<List<PermissionOverviewModel>> queryPermissionsByUserId(@PathVariable("userId") int userId) {
        UserVO userVO = userService.queryUser(userId);
        if (null == userVO) {
            return Response.ok();
        }
        if (CollectionUtils.isEmpty(userVO.getRoles())) {
            //用户没有任何权限
            return Response.ok(Collections.emptyList());
        }
        //解析用户拥有的全部权限
        return Response.ok(PermissionService.resolveUserPermission(userVO, false)
                .stream().map(PermissionModelAdapter::coverToOverviewModel).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OPERATION_ROLE_LIST')")
    @Override
    public Response<List<PermissionOverviewModel>> queryPermissionsByRoleId(@PathVariable("roleId") int roleId) {
        RoleVO role = roleService.findRole(roleId);
        if (null == role) {
            return Response.ok();
        }
        //解析角色拥有的全部权限
        return Response.ok(PermissionService.resolveRolePermission(role)
                .stream().map(PermissionModelAdapter::coverToOverviewModel).collect(Collectors.toList()));
    }


}
