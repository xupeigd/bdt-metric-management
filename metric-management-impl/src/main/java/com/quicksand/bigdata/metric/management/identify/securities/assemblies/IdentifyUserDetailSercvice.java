package com.quicksand.bigdata.metric.management.identify.securities.assemblies;

import com.quicksand.bigdata.metric.management.identify.securities.vos.IdentifyUserDetails;
import com.quicksand.bigdata.metric.management.identify.services.PermissionService;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import com.quicksand.bigdata.vars.security.vos.SecurityGrantedAuthority;
import com.quicksand.bigdata.vars.security.vos.SecurityPermissionVO;
import com.quicksand.bigdata.vars.security.vos.SecurityRoleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IdentifyUserDetailSercvice
 *
 * @author page
 * @date 2020/8/24 11:19
 */
public interface IdentifyUserDetailSercvice
        extends UserDetailsService {

    static SecurityRoleVO cover2Role(RoleVO roleVO) {
        SecurityRoleVO srv = new SecurityRoleVO();
        srv.setId(roleVO.getId());
        srv.setName(roleVO.getName());
        srv.setCode(roleVO.getCode());
        return srv;
    }

    static SecurityGrantedAuthority cover2GrantPermission(PermissionVO permissionVO) {
        SecurityPermissionVO spv = new SecurityPermissionVO();
        spv.setId(permissionVO.getId());
        spv.setCode(permissionVO.getCode());
        spv.setType(PermissionType.valueOf(permissionVO.getType().getName()));
        spv.setName(permissionVO.getName());
        SecurityGrantedAuthority securityGrantedAuthority = new SecurityGrantedAuthority();
        securityGrantedAuthority.setSecurityPermissionVO(spv);
        return securityGrantedAuthority;
    }

    static SecurityGrantedAuthority cover2GrantPermission(RoleVO roleVO) {
        SecurityPermissionVO spv = new SecurityPermissionVO();
        spv.setId(roleVO.getId());
        spv.setCode(roleVO.getCode().toUpperCase());
        spv.setType(PermissionType.ROLE);
        spv.setName(roleVO.getName());
        SecurityGrantedAuthority securityGrantedAuthority = new SecurityGrantedAuthority();
        securityGrantedAuthority.setSecurityPermissionVO(spv);
        return securityGrantedAuthority;
    }

    static IdentifyUserDetails cover2UserDetails(UserVO userVO) {
        IdentifyUserDetails iud = new IdentifyUserDetails();
        iud.setId(userVO.getId());
        iud.setPassword(userVO.getPassword());
        iud.setName(userVO.getName());
        if (!CollectionUtils.isEmpty(userVO.getRoles())) {
            List<SecurityRoleVO> srs = userVO.getRoles().stream()
                    .map(IdentifyUserDetailSercvice::cover2Role)
                    .collect(Collectors.toList());
            iud.setRoles(srs);
        }
        List<PermissionVO> permissionVOS = PermissionService.resolveUserPermission(userVO, false);
        //扩殖Permission
        List<PermissionVO> addPermissions = permissionVOS.stream()
                .filter(k -> PermissionType.MENU.equals(k.getType()))
                .map(k -> {
                    PermissionVO cp = new PermissionVO();
                    BeanUtils.copyProperties(k, cp);
                    cp.setType(PermissionType.OPERATION);
                    return cp;
                })
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(addPermissions)) {
            permissionVOS.addAll(addPermissions);
        }
        //扩殖role类型的permission
        List<SecurityGrantedAuthority> authorities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userVO.getRoles())) {
            authorities.addAll(CollectionUtils.isEmpty(userVO.getRoles())
                    ? Collections.emptyList()
                    : userVO.getRoles().stream()
                    .map(IdentifyUserDetailSercvice::cover2GrantPermission)
                    .collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(permissionVOS)) {
            authorities.addAll(permissionVOS.stream()
                    .map(IdentifyUserDetailSercvice::cover2GrantPermission)
                    .collect(Collectors.toList()));
        }
        iud.setAuthorities(authorities);
        //设置签发时间与校验状态
        iud.setConsensus(false);
        iud.setValidation(false);
        iud.setCreateMills(System.currentTimeMillis());
        iud.setValidation(true);
        return iud;
    }

}
