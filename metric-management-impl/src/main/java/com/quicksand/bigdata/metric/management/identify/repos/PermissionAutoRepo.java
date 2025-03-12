package com.quicksand.bigdata.metric.management.identify.repos;

import com.quicksand.bigdata.metric.management.identify.dbvos.PermissionDBVO;
import com.quicksand.bigdata.query.consts.DataStatus;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * PermissionAutoRepo
 *
 * @author page
 * @date 2020/8/18 16:10
 */
@Repository
public interface PermissionAutoRepo
        extends JpaRepository<PermissionDBVO, Integer> {

    /**
     * 根据类型&status查找权限数据
     *
     * @param type 类型
     * @return List of PermissionDBVO
     */
    List<PermissionDBVO> findByType(PermissionType type);

    Page<PermissionDBVO> findByStatusAndTypeAndParentId(DataStatus status, PermissionType type, Integer parentId, Pageable pageable);

    List<PermissionDBVO> findByStatusAndTypeAndParentIdIn(DataStatus status, PermissionType type, Collection<Integer> parentIds);

    PermissionDBVO findByStatusAndId(DataStatus dataStatus, Integer id);

}
