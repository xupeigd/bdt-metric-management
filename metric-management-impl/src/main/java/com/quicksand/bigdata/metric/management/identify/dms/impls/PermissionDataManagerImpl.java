package com.quicksand.bigdata.metric.management.identify.dms.impls;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.quicksand.bigdata.metric.management.identify.dbvos.PermissionDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.RelationOfRoleAndPermissionDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.PermissionDataManager;
import com.quicksand.bigdata.metric.management.identify.repos.PermissionAutoRepo;
import com.quicksand.bigdata.metric.management.identify.repos.RelationOfRoleAndPermissionAutoRepo;
import com.quicksand.bigdata.query.consts.DataStatus;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * PermissionDataManagerImpl
 *
 * @author page
 * @date 2020/8/18 16:21
 */
@Slf4j
@Component
public class PermissionDataManagerImpl
        implements PermissionDataManager {

    @Resource
    PermissionAutoRepo permissionAutoRepo;
    @Resource
    RelationOfRoleAndPermissionAutoRepo relationOfRoleAndPermissionAutoRepo;

    LoadingCache<String, List<PermissionDBVO>> permissions = CacheBuilder.newBuilder()
            .maximumSize(30)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<PermissionDBVO>>() {
                @SuppressWarnings("NullableProblems")
                @Override
                public List<PermissionDBVO> load(String key) {
                    return queryPermissions(PermissionType.findByCode(Integer.parseInt(key)));
                }
            });

    private List<PermissionDBVO> queryPermissions(PermissionType type) {
        return permissionAutoRepo.findByType(type);
    }

    @Override
    public List<PermissionDBVO> queryPermissions(Collection<Integer> permissionIds, PermissionType type) {
        List<PermissionDBVO> permissionDBVOs = null;
        try {
            permissionDBVOs = permissions.get(String.valueOf(type.getCode()));
        } catch (ExecutionException e) {
            log.error("queryPermissions error! type:{}`", type, e);
        }
        if (CollectionUtils.isEmpty(permissionDBVOs)) {
            return Collections.emptyList();
        }
        return permissionDBVOs.stream()
                .filter(k -> permissionIds.contains(k.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDBVO> queryAllPermissions(PermissionType type) {
        return null == type ? permissionAutoRepo.findAll() : permissionAutoRepo.findByType(type);
    }

    @Override
    public List<PermissionDBVO> queryPermissions(Collection<Integer> permissionIds) {
        return queryAllPermissions(null)
                .stream()
                .filter(k -> permissionIds.contains(k.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RelationOfRoleAndPermissionDBVO> findRelations(int roleId) {
        return relationOfRoleAndPermissionAutoRepo.findAllByRoleId(roleId);
    }

    @Override
    public List<RelationOfRoleAndPermissionDBVO> saveRelations(Collection<RelationOfRoleAndPermissionDBVO> relations) {
        return relationOfRoleAndPermissionAutoRepo.saveAll(relations);
    }

    @Override
    public Page<PermissionDBVO> listRootPermissions(int type, Pageable pageable) {
        PageRequest pr = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "serial"));
        return permissionAutoRepo.findByStatusAndTypeAndParentId(DataStatus.ENABLE, PermissionType.findByCode(type), 0, pr);
    }

    @Override
    public List<PermissionDBVO> queryPermissionsByParentId(Collection<Integer> parentIds, int type) {
        return permissionAutoRepo.findByStatusAndTypeAndParentIdIn(DataStatus.ENABLE, PermissionType.findByCode(type), parentIds);
    }

    @Override
    public PermissionDBVO queryPermissionById(Integer id) {
        return permissionAutoRepo.findByStatusAndId(DataStatus.ENABLE, id);
    }

    @Transactional
    @Override
    public PermissionDBVO savePermission(PermissionDBVO dbvo) {
        return permissionAutoRepo.save(dbvo);
    }

    @Override
    public void savePermissions(List<PermissionDBVO> dbvos) {
        permissionAutoRepo.saveAll(dbvos);
    }

}
