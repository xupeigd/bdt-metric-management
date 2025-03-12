package com.quicksand.bigdata.metric.management.identify.dms.impls;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.RoleDataManager;
import com.quicksand.bigdata.metric.management.identify.repos.RoleAutoRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RoleDataManagerImpl
 *
 * @author page
 * @date 2020/8/18 16:21
 */
@Component
public class RoleDataManagerImpl
        implements RoleDataManager {

    @Resource
    RoleAutoRepo roleAutoRepo;

    @Override
    public List<RoleDBVO> queryAllRoles() {
        return roleAutoRepo.findAll()
                .stream()
                .filter(k -> DataStatus.ENABLE.equals(k.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public RoleDBVO findRole(int roleId) {
        Optional<RoleDBVO> optional = roleAutoRepo.findById(roleId);
        if (!optional.isPresent()) {
            return null;
        }
        RoleDBVO roleDBVO = optional.get();
        return DataStatus.ENABLE.equals(roleDBVO.getStatus()) ? roleDBVO : null;
    }

    @Transactional
    @Override
    public RoleDBVO saveRole(RoleDBVO privateRole) {
        return roleAutoRepo.save(privateRole);
    }

    @Override
    public List<RoleDBVO> findRoles(Collection<Integer> queryIds) {
        return roleAutoRepo.findAllById(queryIds);
    }

    @Override
    public Page<RoleDBVO> queryRoleByPage(Pageable pageable) {
        return roleAutoRepo.findAll(pageable);
    }

    @Transactional
    @Override
    public void saveRoles(Collection<RoleDBVO> roles) {
        roleAutoRepo.saveAll(roles);
    }

}
