package com.quicksand.bigdata.metric.management.identify.services;

import com.quicksand.bigdata.metric.management.consts.RoleType;
import com.quicksand.bigdata.metric.management.identify.models.MenuModel;
import com.quicksand.bigdata.metric.management.identify.models.PermissionModel;
import com.quicksand.bigdata.metric.management.identify.models.RolePermissionModifyModel;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import com.quicksand.bigdata.vars.util.PageImpl;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PermissionService
 *
 * @author page
 * @date 2020/8/18 16:39
 */
public interface PermissionService {

    /**
     * 解析用户拥有的权限
     *
     * @param userVO             用户数据
     * @param withoutPrivateRole 是否不包含私有角色
     * @return List of PermissionVO
     */
    static List<PermissionVO> resolveUserPermission(UserVO userVO, boolean withoutPrivateRole) {
        List<PermissionVO> permissions = new ArrayList<>();
        List<PermissionVO> revokePermissions = new ArrayList<>();
        //解析角色附带的权限
        if (!CollectionUtils.isEmpty(userVO.getRoles())) {
            for (RoleVO role : userVO.getRoles()) {
                if (withoutPrivateRole && Objects.equals(RoleType.PERSON, role.getType())) {
                    continue;
                }
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    List<PermissionVO> finalPermissions = permissions;
                    role.getPermissions().forEach(v -> finalPermissions.add(finalPermissions.contains(v) ? null : v));
                }
                if (!CollectionUtils.isEmpty(role.getRevokePermissions())) {
                    List<PermissionVO> finalRevokePermissions = revokePermissions;
                    role.getRevokePermissions().forEach(v -> finalRevokePermissions.add(finalRevokePermissions.contains(v) ? null : v));
                }
            }
            permissions = permissions.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            revokePermissions = revokePermissions.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(revokePermissions)) {
                permissions.removeAll(revokePermissions);
            }
        }
        return permissions;
    }

    static List<PermissionVO> resolveRolePermission(RoleVO role) {
        List<PermissionVO> permissions = new ArrayList<>();
        List<PermissionVO> revokePermissions = new ArrayList<>();
        //解析角色附带的权限
        if (!CollectionUtils.isEmpty(role.getPermissions())) {
            permissions = CollectionUtils.isEmpty(role.getPermissions())
                    ? new ArrayList<>() : new ArrayList<>(role.getPermissions());
            revokePermissions = CollectionUtils.isEmpty(role.getRevokePermissions())
                    ? new ArrayList<>() : new ArrayList<>(role.getRevokePermissions());
            if (!CollectionUtils.isEmpty(revokePermissions)) {
                permissions.removeAll(revokePermissions);
            }
        }
        return permissions;
    }

    /**
     * 查找全系统支持的权限数据
     *
     * @return List of PermissionVO
     */
    List<PermissionVO> queryAllPermissions(PermissionType type);

    /**
     * 修改用户的权限数据
     * （全量修改，eg：传入一个空数组，意味着用户所有的特殊授予/撤销权限都被撤销）
     *
     * @param userVO        修改的用户对象
     * @param permissionIds 授予的权限Id
     * @return List of PermissionVO
     */
    List<PermissionVO> modifyUserPermissions(UserVO userVO, List<Integer> permissionIds);

    /**
     * 根据permissionId查找permissions
     *
     * @param permissionIds ids
     * @return List of PermissionVO
     */
    List<PermissionVO> queryPermissions(List<Integer> permissionIds);

    /**
     * @param type     @see PermissionType
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageImpl<PermissionVO> listPermissions(int type, int pageNo, int pageSize);

    /**
     * 保存权限
     *
     * @param permissionModel
     * @return
     */
    @Transactional
    PermissionVO savePermission(PermissionModel permissionModel);

    /**
     * 删除权限
     *
     * @param permissionIds 权限ids
     */
    @Transactional
    void deletePermissions(List<Integer> permissionIds);

    /**
     * 保存菜单
     *
     * @param model instance of MenuModel
     * @return
     */
    @Transactional
    PermissionVO saveMenu(MenuModel model);

    /**
     * 修改角色的权限设置
     * @param model instance of RolePermissionModel
     */
    @Transactional
    void modifyRolePermissions(RolePermissionModifyModel model);

}
