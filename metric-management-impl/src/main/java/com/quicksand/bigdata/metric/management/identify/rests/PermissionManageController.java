package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.PermissionOverviewModel;
import com.quicksand.bigdata.metric.management.identify.models.UserPermissionModifyModel;
import com.quicksand.bigdata.metric.management.identify.services.PermissionService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.PermissionModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PermissionManageController
 *
 * @author page
 * @date 2020/8/18 16:36
 */
@Slf4j
@RestController
@Tag(name = "权限判断apis")
public class PermissionManageController
        implements PermissionManageRestService {

    @Resource
    UserService userService;
    @Resource
    PermissionService permissionService;

    @PreAuthorize("hasRole('ADMIN') || hasAuthority('OP_USER_LIST_PERMISSION_MANAGET')")
    @Override
    public Response<List<PermissionOverviewModel>> modifyPermissionsByUser(@PathVariable("userId") int userId,
                                                                           @RequestBody UserPermissionModifyModel model) {
        UserVO userVO = userService.queryUser(userId);
        if (null == userVO) {
            return Response.response(HttpStatus.NOT_FOUND);
        }
        if (!CollectionUtils.isEmpty(model.getPermissions())) {
            //检查是否有不存在的权限
            List<PermissionVO> targetPermissions = permissionService.queryPermissions(model.getPermissions());
            if (model.getPermissions().size() != targetPermissions.size()) {
                log.info("modifyPermissionsByUser fail ： some permission not exist ! userId:{}`permissions:{}`", userId, JsonUtils.toString(model.getPermissions()));
                return Response.response(HttpStatus.BAD_REQUEST);
            }
        }
        List<PermissionVO> curPermissions = permissionService.modifyUserPermissions(userVO,
                CollectionUtils.isEmpty(model.getPermissions()) ? Collections.emptyList() : model.getPermissions());
        log.info("grant permission success ! userId:{}`permissions:{}", userId, JsonUtils.toString(model.getPermissions()));
        return Response.ok(curPermissions
                .stream()
                .map(PermissionModelAdapter::coverToOverviewModel)
                .collect(Collectors.toList()));
    }

}
