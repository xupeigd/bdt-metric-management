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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class MenuAmisRestController {

    @Resource
    PermissionService permissionService;

    @Operation(description = "管理框架-菜单列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
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

    @Operation(description = "管理框架-新建菜单")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @PostMapping(Vars.PATH_ROOT + "/amis/menus")
    public FrameworkResponse<MenuModel, Void> createMenu(@RequestBody MenuModel model) {
        PermissionVO permissionVO = permissionService.saveMenu(model);
        return FrameworkResponse.frameworkResponse(MenuModelAdapter.coverToOverviewModel(permissionVO), null, 0, "success");
    }

    @Operation(description = "管理框架-查询菜单详情")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/menus/{menuId}")
    public FrameworkResponse<MenuModel, Void> modifyMenu(@PathVariable("menuId") Integer menuId) {
        List<PermissionVO> vos = permissionService.queryPermissions(Lists.newArrayList(menuId));
        if (CollectionUtils.isEmpty(vos)) {
            return FrameworkResponse.frameworkResponse(null, null, 1, "permission not exist .");
        } else {
            return FrameworkResponse.frameworkResponse(MenuModelAdapter.coverToOverviewModel(vos.get(0)),
                    null, 0, "success");
        }
    }

    @Operation(description = "管理框架-修改菜单")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @PutMapping(Vars.PATH_ROOT + "/amis/menus/{menuId}")
    public FrameworkResponse<MenuModel, Void> modifyMenu(@PathVariable("menuId") Integer menuId, @RequestBody MenuModel model) {
        model.setId(menuId);
        PermissionVO permissionVO = permissionService.saveMenu(model);
        return FrameworkResponse.frameworkResponse(MenuModelAdapter.coverToOverviewModel(permissionVO), null, 0, "success");
    }

    @Operation(description = "管理框架-删除菜单")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @DeleteMapping(Vars.PATH_ROOT + "/amis/menus/{menuIds}")
    public FrameworkResponse<List<Integer>, Void> deleteMenu(@PathVariable("menuIds") List<Integer> menuIds) {
        permissionService.deletePermissions(menuIds);
        return FrameworkResponse.frameworkResponse(menuIds, null, 0, "success");
    }

}
