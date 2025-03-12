package com.quicksand.bigdata.metric.management.identify.services;

import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.vars.util.PageImpl;

import javax.transaction.Transactional;
import java.util.List;

/**
 * RoleService
 *
 * @author page
 * @date 2020/8/18 16:38
 */
public interface RoleService {

    /**
     * 获取系统所有的角色
     *
     * @return List of RoleVO
     */
    List<RoleVO> queryAllRoles();

    /**
     * 根据roleId查询Role
     *
     * @param roleId 角色Id
     * @return instance of RoleVO /null
     */
    RoleVO findRole(int roleId);

    List<RoleVO> findRoles(List<Integer> roleIds);


    /**
     * 分页查询角色信息
     *
     * @param pageNo   当前页面
     * @param pageSize 每页显示数量
     * @return page of
     */
    PageImpl<RoleVO> queryRoleByPage(int pageNo, int pageSize);

    /**
     * 修改角色名称
     *
     * @param id   roleId
     * @param name 名称
     * @return instance of RoleVO
     */
    @Transactional
    RoleVO modifyName(int id, String name);

    /**
     * 删除角色
     *
     * @param roleIds roleId
     */
    @Transactional
    void deleteRoles(List<Integer> roleIds);

    @Transactional
    RoleVO createRole(String name, String code);

}
