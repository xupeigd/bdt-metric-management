package com.quicksand.bigdata.metric.management.identify.dms;

import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
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
public interface UserDataManager {

    /**
     * 根据userId查找用户数据
     *
     * @param userId 用户Id
     * @return instance of UserDBVO
     */
    UserDBVO findUser(int userId);

    /**
     * 分页查询用户数据
     *
     * @param pageable 分页参数
     * @return Page of UserDBVO
     */
    Page<UserDBVO> queryUserWithPage(Pageable pageable);

    /**
     * 分页查询用户数据
     *
     * @param keyword  关键字
     * @param pageable 分页参数
     * @return Page of UserDBVO
     */
    Page<UserDBVO> queryUserByKeywordWithPage(String keyword, Pageable pageable);

    /**
     * 分页查询用户数据
     *
     * @param roleId   角色Id
     * @param pageable 分页参数
     * @return Page of UserDBVO
     */
    Page<UserDBVO> queryUsersByRoleIdWithPage(Integer roleId, Pageable pageable);

    /**
     * 分页查询用户数据
     *
     * @param keyword  关键字
     * @param roleId   角色Id
     * @param pageable 分页参数
     * @return Page of UserDBVO
     */
    Page<UserDBVO> queryUsersByKeywordAndRoleIdWithPage(String keyword, Integer roleId, Pageable pageable);

    /**
     * 根据用户名查找用户
     *
     * @param name 用户名
     * @return instance of UserDBVO
     */
    UserDBVO findUserByName(String name);

    /**
     * 根据邮箱名查找用户
     *
     * @param email 邮箱名
     * @return instance of UserDBVO
     */
    UserDBVO findUserByEmail(String email);

    /**
     * 保存用户数据（新建/更新）
     *
     * @param userDBVO 用户数据
     * @return instance of UserDBVO
     */
    UserDBVO saveUser(UserDBVO userDBVO);

    List<UserDBVO> findUsers(Collection<Integer> userIds);

    List<UserDBVO> findUsersNameLike(String nameKeyword);

    /**
     * 按照特定的属性检索数据
     *
     * @param propertyName 属性名称
     * @param value        值(任意类型的值都以Str形式进行检索)
     * @return list of Users / empty list
     */
    List<UserDBVO> findUsersBySpecialProperty(String propertyName, String value);

    @Transactional
    void saveUsers(Collection<UserDBVO> users);
}
