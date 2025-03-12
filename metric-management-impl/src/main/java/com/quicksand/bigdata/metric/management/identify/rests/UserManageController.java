package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.RoleType;
import com.quicksand.bigdata.metric.management.consts.UserStatus;
import com.quicksand.bigdata.metric.management.identify.models.UserModifyModel;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import com.quicksand.bigdata.metric.management.identify.models.UserStatusModifyModel;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyPasswordEncoder;
import com.quicksand.bigdata.metric.management.identify.services.RoleService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.UserModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * UserManageController
 *
 * @author page
 * @date 2020/8/18 16:27
 */
@RestController
@Tag(name = "用户管理Apis")
public class UserManageController
        implements UserManageRestService {

    private static final String EMAIL_SUFFIX = "@quicksand.com";

    @Resource
    UserService userService;
    @Resource
    RoleService roleService;
    @Resource
    IdentifyPasswordEncoder identifyPasswordEncoder;

    @Transactional
    @Override
    @PreAuthorize("hasAuthority('OP_USER_LIST_UPINSERT') ")
    public Response<UserOverviewModel> createUser(@RequestBody @Validated({Insert.class}) UserModifyModel model) {
        //判断邮箱是否quicksand.com结尾
        if (!model.getEmail().endsWith(EMAIL_SUFFIX)) {
            return Response.response(HttpStatus.BAD_REQUEST, "目前仅支持quicksand.com的邮箱注册！");
        }
        UserVO userVO = userService.findByName(model.getName());
        if (null != userVO) {
            return Response.response(HttpStatus.BAD_REQUEST, "相同名称的用户已存在!");
        }
        List<RoleVO> roles = roleService.findRoles(model.getRoles().stream()
                .map(UserModifyModel.RoleSelected::getId)
                .collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(roles)) {
            return Response.response(HttpStatus.BAD_REQUEST, "角色不存在/不可用！");
        }
        //不允许选择非法的role
        if (roles.stream().anyMatch(v -> Objects.equals(RoleType.PERSON, v.getType()))) {
            return Response.response(HttpStatus.BAD_REQUEST, "角色不可用！");
        }
        //创建用户
        return Response.response(
                UserModelAdapter.cover2OverviewModel(userService.createUser(model.getName(), model.getEmail(), "", model.getPassword(), roles)),
                HttpStatus.OK);
    }

    @Transactional
    @Override
    @PreAuthorize("hasAuthority('OP_USER_LIST_UPINSERT') || @varsSecurityUtilService.userMatched(#userId,authentication)")
    public Response<UserOverviewModel> modifyUserData(@PathVariable("userId") int userId,
                                                      @RequestBody UserModifyModel model) {
        //todo 由于validator不支持递进式校验，因而此处需要使用递进式校验校验model ugly
        UserVO userVO = userService.queryUser(userId);
        if (null == userVO) {
            return Response.response(HttpStatus.NOT_FOUND, "用户不存在！");
        }
        List<RoleVO> roles = roleService.findRoles(model.getRoles().stream()
                .map(UserModifyModel.RoleSelected::getId)
                .collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(roles)) {
            return Response.response(HttpStatus.BAD_REQUEST, "角色不存在/不可用！");
        }
        //不允许选择非法的role
        if (roles.stream().anyMatch(v -> Objects.equals(RoleType.PERSON, v.getType()))) {
            return Response.response(HttpStatus.BAD_REQUEST, "角色不可用！");
        }
        //参数转置
        if (StringUtils.hasText(model.getEmail())) {
            //判断邮箱是否quicksand.com结尾
            if (!model.getEmail().endsWith(EMAIL_SUFFIX)) {
                return Response.response(HttpStatus.BAD_REQUEST, "目前仅支持quicksand.com的邮箱");
            }
            userVO.setEmail(model.getEmail());
        }
        if (StringUtils.hasText(model.getName())) {
            if (2 > model.getName().length() || 16 < model.getName().length()) {
                return Response.response(HttpStatus.BAD_REQUEST, "用户名长度必须在2～16位之间");
            }
            userVO.setName(model.getName());
        }
        if (StringUtils.hasText(model.getPassword())) {
            if (4 > model.getPassword().length() || 16 < model.getPassword().length()) {
                return Response.response(HttpStatus.BAD_REQUEST, "密码长度必须在4～16之间！");
            }
            userVO.setPassword(identifyPasswordEncoder.encode(model.getPassword().trim()));
        }
        //有且仅有一个角色，本次修改再次赋予
        List<RoleVO> orginalRoles = userVO.getRoles().stream()
                .filter(v -> Objects.equals(RoleType.PERSON, v.getType()))
                .collect(Collectors.toList());
        orginalRoles.addAll(roles);
        return Response.response(
                UserModelAdapter.cover2OverviewModel(userService.modifyUserVO(userVO, orginalRoles)), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('OP_OPERATION_USER_LIST_UPINSERT')")
    @Transactional
    @Override
    public Response<UserOverviewModel> changeUserStatus(@PathVariable("userId") int userId,
                                                        @Validated @RequestBody UserStatusModifyModel model) {
        UserStatus status = UserStatus.findByCode(model.getStatus());
        if (null == status) {
            return Response.response(HttpStatus.BAD_REQUEST);
        }
        UserVO userVO = userService.queryUser(userId);
        if (null == userVO) {
            return Response.response(HttpStatus.NOT_FOUND, "用户不存在！");
        }
        userVO.setStatus(status);
        return Response.response(
                UserModelAdapter.cover2OverviewModel(userService.modifyUserVO(userVO, userVO.getRoles())), HttpStatus.OK);

    }

}
