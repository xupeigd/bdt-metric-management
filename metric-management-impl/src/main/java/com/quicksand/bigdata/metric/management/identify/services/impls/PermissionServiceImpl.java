package com.quicksand.bigdata.metric.management.identify.services.impls;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.GrantType;
import com.quicksand.bigdata.metric.management.consts.PermissionGrantType;
import com.quicksand.bigdata.metric.management.consts.RoleType;
import com.quicksand.bigdata.metric.management.identify.dbvos.PermissionDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.RelationOfRoleAndPermissionDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.PermissionDataManager;
import com.quicksand.bigdata.metric.management.identify.dms.RoleDataManager;
import com.quicksand.bigdata.metric.management.identify.models.MenuModel;
import com.quicksand.bigdata.metric.management.identify.models.PermissionModel;
import com.quicksand.bigdata.metric.management.identify.models.RolePermissionModifyModel;
import com.quicksand.bigdata.metric.management.identify.repos.RelationOfRoleAndPermissionAutoRepo;
import com.quicksand.bigdata.metric.management.identify.services.PermissionService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PermissionServiceImpl
 *
 * @author page
 * @date 2020/8/18 16:39
 */
@Service
public class PermissionServiceImpl
        implements PermissionService {

    @Resource
    PermissionDataManager permissionDataManager;
    @Resource
    RoleDataManager roleDataManager;
    @Resource
    RelationOfRoleAndPermissionAutoRepo relationOfRoleAndPermissionAutoRepo;

    @Override
    public List<PermissionVO> queryAllPermissions(PermissionType type) {
        List<PermissionDBVO> permissionDBVOS = permissionDataManager.queryAllPermissions(null);
        if (CollectionUtils.isEmpty(permissionDBVOS)) {
            return Collections.emptyList();
        }
        return permissionDBVOS.stream().map(k -> {
            PermissionVO permissionVO = new PermissionVO();
            BeanUtils.copyProperties(k, permissionVO);
            return permissionVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> modifyUserPermissions(UserVO userVO, List<Integer> permissionIds) {
        RoleVO privateRole = userVO.getRoles().stream()
                .filter(v -> Objects.equals(RoleType.PERSON, v.getType()))
                .findFirst()
                .orElseGet(null);
        if (null == privateRole) {
            throw new ValidationException("私有角色不存在，无法修改权限！");
        }
        Date curDate = new Date();
        UserSecurityDetails authUser = AuthUtil.getUserDetail();
        List<RelationOfRoleAndPermissionDBVO> relations = permissionDataManager.findRelations(privateRole.getId());
        if (CollectionUtils.isEmpty(permissionIds)) {
            //意味着需要将所有的rel撤销(删除)
            if (!CollectionUtils.isEmpty(relations)) {
                for (RelationOfRoleAndPermissionDBVO relation : relations) {
                    relation.setUpdateTime(curDate);
                    relation.setUpdateUserId(null == authUser ? 0 : authUser.getId());
                    relation.setStatus(DataStatus.DISABLE);
                }
                permissionDataManager.saveRelations(relations);
            }
        } else {
            Map<Integer, RelationOfRoleAndPermissionDBVO> function2Permission = relations.stream()
                    .filter(v -> Objects.equals(GrantType.FIXED, v.getGrantType())
                            || (curDate.after(v.getGrantStartTime()) && curDate.before(v.getGrantEndTime())))
                    .peek(v -> {
                        v.setUpdateTime(curDate);
                        v.setUpdateUserId(null == authUser ? 0 : authUser.getId());
                        v.setStatus(DataStatus.DISABLE);
                    })
                    .collect(Collectors.toMap(k -> (Objects.equals(GrantType.FIXED, k.getGrantType()) ? 1 : -1) * k.getPermissionDBVO().getId(), v -> v));
            for (Integer permissionId : permissionIds) {
                RelationOfRoleAndPermissionDBVO orDefault = function2Permission.getOrDefault(permissionId, RelationOfRoleAndPermissionDBVO.builder()
                        .permissionDBVO(PermissionDBVO.builder().id(Math.abs(permissionId)).build())
                        .role(RoleDBVO.builder().id(privateRole.getId()).build())
                        .createTime(curDate)
                        .createUserId(null == authUser ? 0 : authUser.getId())
                        .type(0 > permissionId ? PermissionGrantType.REVOKE : PermissionGrantType.GRANT)
                        .grantStartTime(new Date(0))
                        .grantEndTime(new Date(0))
                        .grantType(GrantType.FIXED)
                        .build());
                orDefault.setUpdateTime(curDate);
                orDefault.setUpdateUserId(null == authUser ? 0 : authUser.getId());
                orDefault.setStatus(DataStatus.ENABLE);
                function2Permission.put(permissionId, orDefault);
            }
            permissionDataManager.saveRelations(function2Permission.values());
        }
        return queryPermissions(permissionIds);
    }

    @Override
    public List<PermissionVO> queryPermissions(List<Integer> permissionIds) {
        return (CollectionUtils.isEmpty(permissionIds) ? Collections.emptyList() : permissionDataManager.queryPermissions(permissionIds))
                .stream()
                .map(k -> JsonUtils.transfrom(k, PermissionVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PageImpl<PermissionVO> listPermissions(int type, int pageNo, int pageSize) {
        Page<PermissionDBVO> page = permissionDataManager.listRootPermissions(type, PageRequest.of(pageNo - 1, pageSize));
        List<PermissionVO> permissionVOs = null != page && !CollectionUtils.isEmpty(page.getContent())
                ? page.getContent().stream()
                .map(k -> JsonUtils.transfrom(k, PermissionVO.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                : Collections.emptyList();
        Map<Integer, PermissionVO> id2Permission = permissionVOs.stream()
                .collect(Collectors.toMap(PermissionVO::getId, v -> v));
        List<PermissionDBVO> children = permissionDataManager.queryPermissionsByParentId(id2Permission.keySet(), type);
        if (!CollectionUtils.isEmpty(children)) {
            for (PermissionDBVO permissionDBVO : children) {
                PermissionVO permissionVO = JsonUtils.transfrom(permissionDBVO, PermissionVO.class);
                PermissionVO parent = id2Permission.get(permissionVO.getParentId());
                if (null != parent) {
                    if (null != parent.getChildren()) {
                        parent.getChildren().add(permissionVO);
                    } else {
                        parent.setChildren(new ArrayList<>());
                        parent.getChildren().add(permissionVO);
                    }
                }
            }
        }
        //noinspection DataFlowIssue
        return new PageImpl<>(pageNo, pageSize, null == page ? 0L : (long) page.getTotalPages(),
                null == page ? 0 : (int) page.getTotalElements(), permissionVOs);
    }

    @Transactional
    @Override
    public PermissionVO savePermission(PermissionModel model) {
        PermissionDBVO dbvo = new PermissionDBVO();
        if (null != model.getId()) {
            dbvo = permissionDataManager.queryPermissionById(model.getId());
            if (null == dbvo) {
                throw new ValidationException("权限不存在，无法修改！");
            }
        } else {
            dbvo.setName(model.getName());
            dbvo.setCode(model.getCode());
            dbvo.setParentId(model.getParentId());
            dbvo.setCreateTime(new Date());
            dbvo.setStatus(DataStatus.ENABLE);
            dbvo.setIcon("");
            dbvo.setPath("");
            dbvo.setModule("");
            dbvo.setType(model.getType());
        }
        dbvo.setParentId(null == model.getParentId() ? 0 : model.getParentId());
        dbvo.setUpdateTime(new Date());
        dbvo.setName(model.getName());
        dbvo.setSerial(model.getSerial());
        dbvo = permissionDataManager.savePermission(dbvo);
        return JsonUtils.transfrom(dbvo, PermissionVO.class);
    }

    @Transactional
    @Override
    public void deletePermissions(List<Integer> permissionIds) {
        List<PermissionDBVO> dbvos = permissionDataManager.queryPermissions(permissionIds);
        if (!CollectionUtils.isEmpty(dbvos)) {
            for (PermissionDBVO dbvo : dbvos) {
                dbvo.setStatus(DataStatus.DISABLE);
                dbvo.setUpdateTime(new Date());
            }
            permissionDataManager.savePermissions(dbvos);
        }
    }

    @Transactional
    @Override
    public PermissionVO saveMenu(MenuModel model) {
        PermissionDBVO dbvo = new PermissionDBVO();
        if (null != model.getId()) {
            dbvo = permissionDataManager.queryPermissionById(model.getId());
            if (null == dbvo) {
                throw new ValidationException("权限不存在，无法修改！");
            }
        } else {
            dbvo.setName(model.getName());
            dbvo.setCode("");
            dbvo.setParentId(model.getParentId());
            dbvo.setCreateTime(new Date());
            dbvo.setStatus(DataStatus.ENABLE);
            dbvo.setIcon("");
            dbvo.setPath(model.getPath());
            dbvo.setModule("");
            dbvo.setCode(DigestUtils.md5DigestAsHex((model.getName() + "_" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8)));
            dbvo.setType(PermissionType.MENU);
        }
        dbvo.setParentId(null == model.getParentId() ? 0 : model.getParentId());
        dbvo.setUpdateTime(new Date());
        dbvo.setName(model.getName());
        dbvo.setSerial(model.getSerial());
        dbvo.setIcon(null == model.getIcon() ? "" : model.getIcon());
        dbvo = permissionDataManager.savePermission(dbvo);
        return JsonUtils.transfrom(dbvo, PermissionVO.class);
    }

    @Transactional
    @Override
    public void modifyRolePermissions(RolePermissionModifyModel model) {
        RoleDBVO role = roleDataManager.findRole(model.getId());
        if (null != role) {
            role.setUpdateTime(new Date());

            if (CollectionUtils.isEmpty(model.getPermissions())) {
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    // 清理关联关系
                    if (Objects.equals(0, model.getType())) {
                        role.setPermissions(Collections.emptyList());
                    } else {
                        List<RelationOfRoleAndPermissionDBVO> relPermissions = relationOfRoleAndPermissionAutoRepo.findByTypeAndRoleId(PermissionGrantType.REVOKE, model.getId());
                        if (!CollectionUtils.isEmpty(relPermissions)) {
                            relationOfRoleAndPermissionAutoRepo.deleteAll(relPermissions);
                        }
                    }
                    roleDataManager.saveRole(role);
                }
            }
            if (!CollectionUtils.isEmpty(model.getPermissions())) {
                List<PermissionDBVO> permissions = permissionDataManager.queryPermissions(model.getPermissions().stream()
                        .map(RolePermissionModifyModel.PermissionSelected::getId)
                        .collect(Collectors.toList()));
                if (Objects.equals(0, model.getType())) {
                    role.setPermissions(permissions);
                } else {
                    List<RelationOfRoleAndPermissionDBVO> relPermissions = permissions.stream()
                            .map(v -> {
                                RelationOfRoleAndPermissionDBVO rel = new RelationOfRoleAndPermissionDBVO();
                                rel.setStatus(DataStatus.ENABLE);
                                rel.setRole(role);
                                rel.setPermissionDBVO(v);
                                rel.setType(PermissionGrantType.REVOKE);
                                rel.setGrantType(GrantType.FIXED);
                                rel.setCreateTime(new Date());
                                rel.setUpdateTime(new Date());
                                rel.setCreateUserId(Objects.requireNonNull(AuthUtil.getUserDetail()).getId());
                                rel.setUpdateUserId(Objects.requireNonNull(AuthUtil.getUserDetail()).getId());
                                rel.setGrantStartTime(new Date());
                                rel.setGrantEndTime(new Date());
                                return rel;
                            })
                            .collect(Collectors.toList());
                    relationOfRoleAndPermissionAutoRepo.saveAll(relPermissions);
                }
                roleDataManager.saveRole(role);
            }
        }

    }

}
