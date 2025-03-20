package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.google.common.collect.Lists;
import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.identify.models.MenuModel;
import com.quicksand.bigdata.metric.management.identify.services.PermissionService;
import com.quicksand.bigdata.metric.management.identify.utils.MenuModelAdapter;
import com.quicksand.bigdata.metric.management.identify.utils.PermissionModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Tag(name = "菜单管理", description = "系统菜单的增删改查和相关操作接口")
public class MenuAmisRestController {

    @Resource
    PermissionService permissionService;

    @Operation(summary = "获取菜单列表", description = "获取系统中所有菜单信息列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/menus")
    public FrameworkResponse<List<MenuModel>, Void> listMenus() {
        List<PermissionVO> vos = permissionService.queryAllPermissions(PermissionType.MENU);
        List<PermissionVO> menus = CollectionUtils.isEmpty(vos)
                ? Collections.emptyList()
                : vos.stream()
                .filter(v -> Objects.equals(PermissionType.MENU, v.getType()))
                .collect(Collectors.toList());
        List<MenuModel> models = PermissionModelAdapter.coverPermissionChains(menus)
                .stream()
                .map(v -> JsonUtils.transfrom(v, MenuModel.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return FrameworkResponse.frameworkResponse(models, null, 0, "success");
    }

    @Operation(summary = "创建菜单", description = "创建新的系统菜单")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = MenuModel.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @Transactional
    @PostMapping(Vars.PATH_ROOT + "/amis/menus")
    public FrameworkResponse<MenuModel, Void> createMenu(
            @Parameter(description = "菜单模型", required = true) @RequestBody MenuModel model) {
        PermissionVO permissionVO = permissionService.saveMenu(model);
        return FrameworkResponse.frameworkResponse(MenuModelAdapter.coverToOverviewModel(permissionVO), null, 0,
                "success");
    }

    @Operation(summary = "查询菜单详情", description = "根据菜单ID查询单个菜单的详细信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = MenuModel.class))),
            @ApiResponse(responseCode = "404", description = "菜单不存在"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/menus/{menuId}")
    public FrameworkResponse<MenuModel, Void> modifyMenu(
            @Parameter(description = "菜单ID", required = true) @PathVariable("menuId") Integer menuId) {
        List<PermissionVO> vos = permissionService.queryPermissions(Lists.newArrayList(menuId));
        if (CollectionUtils.isEmpty(vos)) {
            return FrameworkResponse.frameworkResponse(null, null, 1, "permission not exist .");
        } else {
            return FrameworkResponse.frameworkResponse(MenuModelAdapter.coverToOverviewModel(vos.get(0)),
                    null, 0, "success");
        }
    }

    @Operation(summary = "修改菜单", description = "根据菜单ID更新菜单信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = MenuModel.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "菜单不存在"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @Transactional
    @PutMapping(Vars.PATH_ROOT + "/amis/menus/{menuId}")
    public FrameworkResponse<MenuModel, Void> modifyMenu(
            @Parameter(description = "菜单ID", required = true) @PathVariable("menuId") Integer menuId,
            @Parameter(description = "菜单模型", required = true) @RequestBody MenuModel model) {
        model.setId(menuId);
        PermissionVO permissionVO = permissionService.saveMenu(model);
        return FrameworkResponse.frameworkResponse(MenuModelAdapter.coverToOverviewModel(permissionVO), null, 0,
                "success");
    }

    @Operation(summary = "删除菜单", description = "根据菜单ID列表批量删除菜单信息")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "未授权的访问")
    })
    @Transactional
    @DeleteMapping(Vars.PATH_ROOT + "/amis/menus/{menuIds}")
    public FrameworkResponse<List<Integer>, Void> deleteMenu(
            @Parameter(description = "菜单ID列表", required = true) @PathVariable("menuIds") List<Integer> menuIds) {
        permissionService.deletePermissions(menuIds);
        return FrameworkResponse.frameworkResponse(menuIds, null, 0, "success");
    }

}
