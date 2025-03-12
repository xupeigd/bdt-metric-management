package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.identify.models.UserDetailsModel;
import com.quicksand.bigdata.metric.management.identify.models.UserModifyModel;
import com.quicksand.bigdata.metric.management.identify.rests.UserRestService;
import com.quicksand.bigdata.metric.management.identify.services.RoleService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.UserModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class UserAmisRestController
 *
 * @Author: page
 * @Date: 2025/3/4
 * @Description:
 */
@RestController
public class UserAmisRestController {


    @Resource
    UserService userService;
    @Resource
    RoleService roleService;
    @Resource
    UserRestService userRestService;

    @Operation(description = "管理框架-用户列表")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/users")
    public FrameworkResponse<PageImpl<UserDetailsModel>, Void> listUses(@RequestParam(name = "page", defaultValue = "1") int pageNo,
                                                                        @RequestParam(name = "perPage", defaultValue = "20") int pageSize,
                                                                        @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
        Response<PageImpl<UserDetailsModel>> pageRestResponse = userRestService.listUserDetails(pageNo, pageSize, keyword, null);
        return FrameworkResponse.extend(pageRestResponse);
    }

    @Operation(description = "管理框架-修改用户数据")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @PostMapping(Vars.PATH_ROOT + "/amis/users")
    public FrameworkResponse<UserDetailsModel, Void> createUserInfo(@RequestBody UserModifyModel model) {
        List<RoleVO> roleVOS = !CollectionUtils.isEmpty(model.getRoles())
                ? roleService.findRoles(model.getRoles().stream()
                .map(UserModifyModel.RoleSelected::getId)
                .collect(Collectors.toList()))
                : Collections.emptyList();
        UserVO user = userService.createUser(model.getName(), model.getEmail(), model.getMobile(), model.getPassword(), roleVOS);
        return FrameworkResponse.frameworkResponse(UserModelAdapter.cover2DetailModel(user), null, 0, "success");
    }

    @Operation(description = "管理框架-查看用户详情")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @GetMapping(Vars.PATH_ROOT + "/amis/users/{userId}")
    public FrameworkResponse<UserDetailsModel, Void> queryUserInfo(@PathVariable(name = "userId") Integer userId) {
        UserVO userVO = userService.queryUser(userId);
        if (null == userVO) {
            return FrameworkResponse.extend(Response.response(HttpStatus.NOT_FOUND));
        }
        return FrameworkResponse.frameworkResponse(UserModelAdapter.cover2DetailModel(userVO), null, 0, "success");
    }

    @Operation(description = "管理框架-修改用户数据")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @PutMapping(Vars.PATH_ROOT + "/amis/users/{userId}")
    public FrameworkResponse<UserDetailsModel, Void> ModifyUserInfo(@PathVariable(name = "userId") Integer userId,
                                                                    @RequestBody UserModifyModel model) {
        UserVO userVO = userService.queryUser(userId);
        if (null == userVO) {
            return FrameworkResponse.extend(Response.response(HttpStatus.NOT_FOUND));
        }
        model.setId(userId);
        userVO = userService.modifyUser(model);
        return FrameworkResponse.frameworkResponse(UserModelAdapter.cover2DetailModel(userVO), null, 0, "success");
    }

    @Operation(description = "删除用户数据")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! ")
    })
    @Transactional
    @DeleteMapping(Vars.PATH_ROOT + "/amis/users/{userIds}")
    public FrameworkResponse<List<Integer>, Void> deleteRoles(@PathVariable("userIds") List<Integer> userIds) {
        userService.removeUsers(userIds);
        return FrameworkResponse.frameworkResponse(userIds, null, 0, "success");
    }

}
