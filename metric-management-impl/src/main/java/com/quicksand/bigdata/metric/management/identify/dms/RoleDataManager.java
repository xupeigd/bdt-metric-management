package com.quicksand.bigdata.metric.management.identify.dms;

import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;
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
public interface RoleDataManager {

    List<RoleDBVO> queryAllRoles();

    RoleDBVO findRole(int roleId);

    @Transactional
    RoleDBVO saveRole(RoleDBVO privateRole);

    List<RoleDBVO> findRoles(Collection<Integer> queryIds);

    Page<RoleDBVO> queryRoleByPage(Pageable pageable);

    @Transactional
    void saveRoles(Collection<RoleDBVO> roles);

}
