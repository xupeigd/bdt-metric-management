package com.quicksand.bigdata.metric.management.identify.services;

import com.quicksand.bigdata.metric.management.identify.models.UserModifyModel;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.util.PageImpl;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * UserService
 *
 * @author page
 * @date 2020/8/18 16:37
 */
public interface UserService {

    UserVO queryUser(int userId);

    PageImpl<UserVO> queryUserByPage(int pageNo, int pageSize);

    PageImpl<UserVO> queryUserByPage(int pageNo, int pageSize, String keyword, Integer roleId);

    UserVO findByName(String name);

    /**
     * 创建用户
     *
     * @param name     用户名
     * @param email    邮箱
     * @param mobile
     * @param password 密码
     * @param roles    授权的角色
     * @return instance of UserVO
     */
    UserVO createUser(String name, String email, String mobile, String password, List<RoleVO> roles);

    /**
     * 修改用户数据
     *
     * @param userVO 用户数据
     * @param roles  角色数据 空数组表示清空 null表示不修改角色
     * @return instance of UserVO
     */
    UserVO modifyUserVO(UserVO userVO, List<RoleVO> roles);

    /**
     * 根据userIds查找用户数据
     *
     * @param userIds 用户Ids
     * @return Set of UserVO
     */
    Set<UserVO> queryUsers(Collection<Integer> userIds);

    List<UserVO> findUsersNameLike(String nameKeyword);

    @Transactional
    void removeUsers(List<Integer> ids);

    @Transactional
    UserVO modifyUser(UserModifyModel model);

}
