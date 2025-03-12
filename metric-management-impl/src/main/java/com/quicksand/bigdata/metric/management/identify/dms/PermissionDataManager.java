package com.quicksand.bigdata.metric.management.identify.dms;

import com.quicksand.bigdata.metric.management.identify.dbvos.PermissionDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.RelationOfRoleAndPermissionDBVO;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * UserDataManager
 *
 * @author page
 * @date 2020/8/18 16:19
 */
public interface PermissionDataManager {

    /**
     * 根据权限Ids与类型，查询权限数据
     *
     * @param permissionIds 权限Ids
     * @param type          类型 @see PermissionType
     * @return List of PermissionDBVO
     */
    List<PermissionDBVO> queryPermissions(Collection<Integer> permissionIds, PermissionType type);

    /**
     * 查找全部的权限
     *
     * @return List of PermissionDBVO
     */
    List<PermissionDBVO> queryAllPermissions(PermissionType type);

    /**
     * 根据权限Ids，查询权限数据
     *
     * @param permissionIds 权限Ids
     * @return List of PermissionDBVO
     */
    List<PermissionDBVO> queryPermissions(Collection<Integer> permissionIds);

    List<RelationOfRoleAndPermissionDBVO> findRelations(int roleId);

    List<RelationOfRoleAndPermissionDBVO> saveRelations(Collection<RelationOfRoleAndPermissionDBVO> relations);

    Page<PermissionDBVO> listRootPermissions(int type, Pageable pageable);

    List<PermissionDBVO> queryPermissionsByParentId(Collection<Integer> parentIds, int type);

    PermissionDBVO queryPermissionById(Integer id);

    @Transactional
    PermissionDBVO savePermission(PermissionDBVO dbvo);

    @Transactional
    void savePermissions(List<PermissionDBVO> dbvos);

}
