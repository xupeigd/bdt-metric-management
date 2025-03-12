package com.quicksand.bigdata.metric.management.identify.utils;

import com.quicksand.bigdata.metric.management.identify.models.*;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.jwt.JwtToken;
import com.quicksand.bigdata.vars.security.vos.UserInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserModelAdater
 *
 * @author page
 * @date 2020/8/18 17:39
 */
public final class UserModelAdapter {

    public static UserMixDetailModel cover2MixModel(@Nullable UserVO userVO, List<RoleVO> userRoles, Collection<PermissionVO> menuPermissions, Collection<PermissionVO> operationPermissions) {
        UserMixDetailModel mixModel = new UserMixDetailModel();
        if (null != userVO) {
            mixModel.setUserInfo(cover2OverviewModel(userVO));
        }
        if (!CollectionUtils.isEmpty(userRoles)) {
            userRoles.forEach(k -> {
                RoleOverviewModel rom = new RoleOverviewModel();
                rom.setId(k.getId());
                rom.setName(k.getName());
                rom.setCode(k.getCode());
                rom.setType(k.getType());
                mixModel.getRoles().add(rom);
            });
        }
        //拼装权限的父子状态
        List<PermissionOverviewModel> menuTops = PermissionModelAdapter.coverPermissionChains(menuPermissions);
        List<PermissionGatherModel> gatherModels = PermissionModelAdapter.coverPermissionGatherModel(operationPermissions);
        mixModel.setPagePermissions(menuTops);
        mixModel.setOperationPermissions(gatherModels);
        return mixModel;
    }

    public static UserDetailsModel cover2DetailModel(@NotNull UserVO userVO) {
        UserDetailsModel udm = new UserDetailsModel();
        udm.setId(userVO.getId());
        udm.setName(userVO.getName());
        udm.setMobile(userVO.getMobile());
        udm.setEmail(userVO.getEmail());
        udm.setUserStatus(userVO.getStatus().getCode());
        udm.setCreateTime(userVO.getCreateTime());
        udm.setUpdateTime(userVO.getUpdateTime());
        if (!CollectionUtils.isEmpty(userVO.getRoles())) {
//            udm.setRoleIds(userVO.getRoles().stream()
//                    .map(RoleVO::getId)
//                    .collect(Collectors.toList()));
            udm.setRoles(userVO.getRoles().stream()
                    .map(RoleModelAdapter::cover2OverviewModel)
                    .collect(Collectors.toList()));
        }
        return udm;
    }

    public static UserOverviewModel cover2OverviewModel(@Nullable UserVO userVO) {
        if (null == userVO) {
            return null;
        }
        UserOverviewModel uom = new UserOverviewModel();
        uom.setName(userVO.getName());
        uom.setId(userVO.getId());
        if (!CollectionUtils.isEmpty(userVO.getRoles())) {
            uom.setRoleId(userVO.getRoles().get(0).getId());
            uom.setRoleIds(userVO.getRoles().stream().map(RoleVO::getId).collect(Collectors.toList()));
        }
        uom.setStatus(userVO.getStatus());
        return uom;
    }

    public static UserInfo cover2UserINfo(@Nullable UserVO userVO) {
        if (null == userVO) {
            return null;
        }
        UserInfo uom = UserInfo.builder()
                .name(userVO.getName())
                .id(userVO.getId())
                .build();
        if (!CollectionUtils.isEmpty(userVO.getRoles())) {
            uom.setRoleId(userVO.getRoles().get(0).getId());
        }
        uom.setStatus(userVO.getStatus().getCode());
        return uom;
    }

    public static UserAuthModel cover2AuthModel(UserVO userVO, JwtToken<UserInfo> jwtToken) {
        UserAuthModel authModel = new UserAuthModel();
        authModel.setId(userVO.getId());
        authModel.setName(userVO.getName());
        authModel.setJwtToken(jwtToken);
        return authModel;
    }
}
