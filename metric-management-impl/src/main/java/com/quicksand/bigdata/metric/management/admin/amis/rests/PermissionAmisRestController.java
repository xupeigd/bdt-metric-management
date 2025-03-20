package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.google.common.collect.Lists;
import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.identify.models.PermissionModel;
import com.quicksand.bigdata.metric.management.identify.models.PermissionOverviewModel;
import com.quicksand.bigdata.metric.management.identify.models.RolePermissionModel;
import com.quicksand.bigdata.metric.management.identify.models.RolePermissionModifyModel;
import com.quicksand.bigdata.metric.management.identify.services.PermissionService;
import com.quicksand.bigdata.metric.management.identify.services.RoleService;
import com.quicksand.bigdata.metric.management.identify.utils.PermissionModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Tag(name = "权限管理", description = "提供权限的CRUD操作接口以及角色权限管理")
public class PermissionAmisRestController {

    @Resource
    PermissionService permissionService;
    @Resource
    RoleService roleService;

    /**
     * 查询所有权限
     *
     * @return List<PermissionOverviewModel>
     */
    @Operation(description = "获取所有权限列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！")
    })
    @PreAuthorize(" hasRole('ADMIN') || hasAuthority('OP_PERMISSION_LIST')")
    @GetMapping(Vars.PATH_ROOT + "/amis/permissions")
    public FrameworkResponse<List<PermissionOverviewModel>, Void> listPermissions() {
        return FrameworkResponse.frameworkResponse(
                PermissionModelAdapter.coverPermissionChains(permissionService.queryAllPermissions(null)),
                null, 0, "success");
    }

    /**
     * 查询权限详情
     *
     * @param permissionId 权限Id
     * @return PermissionOverviewModel
     */
    @GetMapping(Vars.PATH_ROOT + "/amis/permissions/{permissionId}")
    public FrameworkResponse<PermissionOverviewModel, Void> queryPermission(
            @PathVariable("permissionId") Integer permissionId) {
        List<PermissionVO> permissionVOs = permissionService.queryPermissions(Lists.newArrayList(permissionId));
        if (CollectionUtils.isEmpty(permissionVOs)) {
            return FrameworkResponse.frameworkResponse(null, null, 1, "permission not exist .");
        } else {
            return FrameworkResponse.frameworkResponse(
                    PermissionModelAdapter.coverToOverviewModel(permissionVOs.get(0)),
                    null, 0, "success");
        }
    }

    /**
     * 新增权限
     *
     * @param permissionModel
     * @return PermissionOverviewModel
     */
    @Transactional
    @PostMapping(Vars.PATH_ROOT + "/amis/permissions")
    public FrameworkResponse<PermissionOverviewModel, Void> createPermission(
            @RequestBody PermissionModel permissionModel) {
        permissionModel.setType(PermissionType.OPERATION);
        PermissionVO permissionVO = permissionService.savePermission(permissionModel);
        return FrameworkResponse.frameworkResponse(PermissionModelAdapter.coverToOverviewModel(permissionVO), null, 0,
                "success");
    }

    /**
     * 修改权限
     *
     * @param permissionId
     * @param permissionModel
     * @return PermissionOverviewModel
     */
    @Transactional
    @PutMapping(Vars.PATH_ROOT + "/amis/permissions/{permissionId}")
    public FrameworkResponse<PermissionOverviewModel, Void> modifyPermission(
            @PathVariable("permissionId") Integer permissionId,
            @RequestBody PermissionModel permissionModel) {
        permissionModel.setId(permissionId);
        PermissionVO permissionVO = permissionService.savePermission(permissionModel);
        return FrameworkResponse.frameworkResponse(PermissionModelAdapter.coverToOverviewModel(permissionVO), null, 0,
                "success");
    }

    /**
     * 删除权限
     *
     * @param permissionIds 权限ids
     * @return
     */
    @Transactional
    @DeleteMapping(Vars.PATH_ROOT + "/amis/permissions/{permissionIds}")
    public FrameworkResponse<List<Integer>, Void> deletePermissions(
            @PathVariable("permissionIds") List<Integer> permissionIds) {
        permissionService.deletePermissions(permissionIds);
        return FrameworkResponse.frameworkResponse(permissionIds, null, 0, "success");
    }

    /**
     * 查询角色拥有的权限
     *
     * @param roleId 角色Id
     * @return List<PermissionOverviewModel>
     */
    @GetMapping(Vars.PATH_ROOT + "/amis/permissions/roles/{roleId}")
    public FrameworkResponse<RolePermissionModel, Void> listPermissionsByRole(@PathVariable("roleId") Integer roleId,
                                                                              @RequestParam(name = "type", required = false, defaultValue = "0") Integer type) {
        RoleVO role = roleService.findRole(roleId);
        if (null == role) {
            return FrameworkResponse.frameworkResponse(null, null, 1, "role not exist .");
        }
        List<PermissionVO> permissions = Objects.equals(0, type) ? role.getPermissions() : role.getRevokePermissions();
        RolePermissionModel permissionPack = RolePermissionModel.builder()
                .id(role.getId())
                .name(role.getName())
                .code(role.getCode())
                .permissions(CollectionUtils.isEmpty(permissions)
                        ? Collections.emptyList()
                        : permissions.stream()
                        .map(PermissionModelAdapter::coverToOverviewModel)
                        .collect(Collectors.toList()))
                .build();
        return FrameworkResponse.frameworkResponse(permissionPack, null, 0, "success");
    }

    /**
     * 修改角色拥有的权限
     *
     * @param roleId 角色Id
     * @return List<PermissionOverviewModel>
     */
    @Transactional
    @PutMapping(Vars.PATH_ROOT + "/amis/permissions/roles/{roleId}")
    public FrameworkResponse<RolePermissionModifyModel, Void> modifyPermissionsByRole(
            @PathVariable("roleId") Integer roleId,
            @RequestBody RolePermissionModifyModel model) {
        RoleVO role = roleService.findRole(roleId);
        if (null == role) {
            return FrameworkResponse.frameworkResponse(model, null, 1, "role not exist .");
        }
        model.setId(roleId);
        permissionService.modifyRolePermissions(model);
        return FrameworkResponse.frameworkResponse(model, null, 0, "success");
    }

}
