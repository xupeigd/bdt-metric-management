package com.quicksand.bigdata.metric.management.identify.repos;

import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * UserAutoRepo
 *
 * @author page
 * @date 2020/8/18 15:25
 */
@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@Repository
public interface UserAutoRepo
        extends JpaRepository<UserDBVO, Integer> {

    /**
     * 根据数据状态分页查询
     *
     * @param keyword  关键字
     * @param pageable 分页参数
     * @return Page of UserDBVO
     */
    Page<UserDBVO> findByNameLike(String keyword, Pageable pageable);

    @Query(
            value = " select b.* from t_identify_rel_user_role a left join t_identify_users b on a.user_id=b.id " +
                    "where b.status <> 0 and a.role_id=:roleId and a.status=1 ",
            countQuery = " select count(b.id) from t_identify_rel_user_role a left join t_identify_users b on a.user_id=b.id " +
                    "where  b.status <> 0 and a.role_id=:roleId and a.status = 1 ",
            nativeQuery = true)
    Page<UserDBVO> findByRoleId(Integer roleId, Pageable pageable);

    @Query(
            value = " select b.* from t_identify_rel_user_role a left join t_identify_users b on a.user_id=b.id " +
                    "where b.status <> 0 and a.role_id <> :roleId and a.status=1 ",
            countQuery = " select count(b.id) from t_identify_rel_user_role a left join t_identify_users b on a.user_id=b.id " +
                    "where  b.status <> 0 and a.role_id <> :roleId and a.status = 1 ",
            nativeQuery = true)
    Page<UserDBVO> findByRoleIdNot(Integer roleId, Pageable pageable);

    @Query(
            value = " select b.* from t_identify_rel_user_role a left join t_identify_users b on a.user_id=b.id " +
                    "where b.status <> 0 and b.name like :keyword and a.role_id=:roleId and a.status = 1 ",
            countQuery = " select count(b.id) from t_identify_rel_user_role a left join t_identify_users b on a.user_id=b.id " +
                    "where b.status <> 0 and b.name like :keyword and a.role_id=:roleId and a.status = 1 ",
            nativeQuery = true)
    Page<UserDBVO> findByKeywordAndRoleId(String keyword, Integer roleId, Pageable pageable);

    @Query(
            value = " select b.* from t_identify_rel_user_role a left join t_identify_users b on a.user_id=b.id " +
                    "where b.status <> 0 and b.name like :keyword and a.role_id <> :roleId and a.status = 1 ",
            countQuery = " select count(b.id) from t_identify_rel_user_role a left join t_identify_users b on a.user_id=b.id " +
                    "where b.status <> 0 and b.name like :keyword and a.role_id <> :roleId and a.status = 1 ",
            nativeQuery = true)
    Page<UserDBVO> findByKeywordAndRoleIdNot(String keyword, Integer roleId, Pageable pageable);

    /**
     * 根据用户名查找用户数据（不区分状态）
     *
     * @param name 用户名称
     * @return insatnce of UserDBVO / null
     */
    UserDBVO findByName(String name);

    /**
     * 根据邮箱名称查找用户数据（不区分状态）
     *
     * @param email 邮箱名称
     * @return insatnce of UserDBVO / null
     */
    UserDBVO findByEmail(String email);


}
