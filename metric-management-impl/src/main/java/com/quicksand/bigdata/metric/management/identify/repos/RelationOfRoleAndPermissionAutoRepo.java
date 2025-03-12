package com.quicksand.bigdata.metric.management.identify.repos;

import com.quicksand.bigdata.metric.management.consts.PermissionGrantType;
import com.quicksand.bigdata.metric.management.identify.dbvos.RelationOfRoleAndPermissionDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RelationOfUserAndRoleAutoRepo
 *
 * @author page
 * @date 2020/8/18 16:16
 */
@Repository
public interface RelationOfRoleAndPermissionAutoRepo
        extends JpaRepository<RelationOfRoleAndPermissionDBVO, Integer> {

    /**
     * 按照roleId查找权限信息
     *
     * @param roleId 角色Id
     * @return list of RelationOfRoleAndPermissionDBVO
     */
    List<RelationOfRoleAndPermissionDBVO> findAllByRoleId(int roleId);

    List<RelationOfRoleAndPermissionDBVO> findByTypeAndRoleId(PermissionGrantType type, int roleId);

}
