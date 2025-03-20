package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.identify.models.RoleOverviewModel;
import com.quicksand.bigdata.metric.management.identify.services.RoleService;
import com.quicksand.bigdata.metric.management.identify.utils.RoleModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "角色管理", description = "提供角色的CRUD操作接口")
public class RoleAmisRestController {

    @Resource
    RoleService roleService;

    @Operation(description = "角色列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/roles")
    public FrameworkResponse<PageImpl<RoleOverviewModel>, Void> listRoles(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "perPage", defaultValue = "20") int pageSize) {
        PageImpl<RoleVO> page = roleService.queryRoleByPage(pageNo, pageSize);
        PageImpl<RoleOverviewModel> pageModel = PageImpl.build(page.getPageNo(), page.getPageSize(),
                page.getTotalPage(), page.getTotal(),
                page.getItems().stream()
                        .map(v -> RoleOverviewModel.builder()
                                .id(v.getId())
                                .code(v.getCode())
                                .name(v.getName())
                                .type(v.getType())
                                .createTime(v.getCreateTime())
                                .updateTime(v.getUpdateTime())
                                .build())
                        .collect(Collectors.toList()));
        return FrameworkResponse.frameworkResponse(pageModel, null, 0, "success");
    }

    @Operation(description = "获取角色详情")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/roles/{roleId}")
    public FrameworkResponse<RoleOverviewModel, Void> queryRole(@PathVariable("roleId") Integer roleId) {
        RoleVO vo = roleService.findRole(roleId);
        if (null == vo) {
            return FrameworkResponse.extend(Response.response(HttpStatus.NOT_FOUND));
        }
        return FrameworkResponse.frameworkResponse(RoleModelAdapter.cover2OverviewModel(vo), null, 0,
                "success");
    }

    @Operation(description = "修改角色")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @PutMapping(Vars.PATH_ROOT + "/amis/roles/{id}")
    public FrameworkResponse<RoleOverviewModel, Void> modifyRole(@PathVariable("id") Integer id,
                                                                 @RequestBody RoleOverviewModel model) {
        return FrameworkResponse.frameworkResponse(
                RoleModelAdapter.cover2OverviewModel(roleService.modifyName(id, model.getName())),
                null, 0, "success");
    }

    @Operation(description = "删除角色")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @DeleteMapping(Vars.PATH_ROOT + "/amis/roles/{roleIds}")
    public FrameworkResponse<List<Integer>, Void> deleteRoles(@PathVariable("roleIds") List<Integer> roleIds) {
        roleService.deleteRoles(roleIds);
        return FrameworkResponse.frameworkResponse(roleIds, null, 0, "success");
    }

    @Operation(description = "创建角色")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @PostMapping(Vars.PATH_ROOT + "/amis/roles")
    public FrameworkResponse<RoleOverviewModel, Void> createRole(@RequestBody RoleOverviewModel model) {
        return FrameworkResponse.frameworkResponse(
                RoleModelAdapter.cover2OverviewModel(
                        roleService.createRole(model.getName(), model.getCode())),
                null, 0, "success");
    }

}
