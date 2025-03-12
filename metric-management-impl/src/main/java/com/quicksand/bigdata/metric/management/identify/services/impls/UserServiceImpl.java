package com.quicksand.bigdata.metric.management.identify.services.impls;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.GrantType;
import com.quicksand.bigdata.metric.management.consts.RoleType;
import com.quicksand.bigdata.metric.management.consts.UserStatus;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.RelationOfUserAndRoleDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.RoleDataManager;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.models.UserModifyModel;
import com.quicksand.bigdata.metric.management.identify.repos.RelationOfUserAndRoleAutoRepo;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyPasswordEncoder;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.identify.utils.OperationLogUtils;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UserServiceImpl
 *
 * @author page
 * @date 2020/8/18 16:37
 */
@Slf4j
@Service
public class UserServiceImpl
        implements UserService {

    @Resource
    UserDataManager userDataManager;
    @Resource
    RoleDataManager roleDataManager;
    @Resource
    RelationOfUserAndRoleAutoRepo relationOfUserAndRoleAutoRepo;
    @Resource
    IdentifyPasswordEncoder identifyPasswordEncoder;
    @Resource
    OperationLogService operationLogService;

    @Override
    public UserVO queryUser(int userId) {
        UserDBVO user = userDataManager.findUser(userId);
        if (null == user) {
            return null;
        }
        return JsonUtils.transfrom(user, UserVO.class);
    }

    private PageImpl<UserVO> cover2VO(Page<UserDBVO> dbvoPage, int curPage, int pageSize) {
        PageImpl<UserVO> pvos = new PageImpl<>();
        if (null != dbvoPage) {
            List<UserVO> userVOs = dbvoPage.getContent()
                    .stream()
                    .map(k -> JsonUtils.transfrom(k, UserVO.class))
                    .collect(Collectors.toList());
            pvos.setItems(userVOs);
            pvos.setPageSize(pageSize);
            pvos.setPageNo(curPage);
            pvos.setTotal(dbvoPage.getTotalElements());
            pvos.setTotalPage(dbvoPage.getTotalPages());
        }
        return pvos;
    }

    @Override
    public PageImpl<UserVO> queryUserByPage(int pageNo, int pageSize) {
        return cover2VO(userDataManager.queryUserWithPage(PageRequest.of(pageNo - 1, pageSize)), pageNo, pageSize);
    }

    @Override
    public PageImpl<UserVO> queryUserByPage(int pageNo, int pageSize, String keyword, Integer roleId) {
        Page<UserDBVO> page = null;
        if (StringUtils.hasText(keyword)
                && (null != roleId && 0 < roleId)) {
            //既有关键词，也有角色Id
            page = userDataManager.queryUsersByKeywordAndRoleIdWithPage(keyword, roleId, PageRequest.of(pageNo - 1, pageSize));
        } else if (StringUtils.hasText(keyword)) {
            //只有关键字
            page = userDataManager.queryUserByKeywordWithPage(keyword, PageRequest.of(pageNo - 1, pageSize));
        } else {
            //只有角色id
            page = userDataManager.queryUsersByRoleIdWithPage(roleId, PageRequest.of(pageNo - 1, pageSize));
        }
        return cover2VO(page, pageNo, pageSize);
    }

    @Override
    public UserVO findByName(String name) {
        UserDBVO userDBVO = userDataManager.findUserByName(name);
        if (null == userDBVO) {
            return null;
        }
        return JsonUtils.transfrom(userDBVO, UserVO.class);
    }

    @Transactional
    @Override
    public UserVO createUser(String name, String email, String mobile, String password, List<RoleVO> roles) {
        UserSecurityDetails authUser = AuthUtil.getUserDetail();
        int operationUserId = null == authUser ? 0 : authUser.getId();
        Date curDate = new Date();
        UserDBVO userDBVO = UserDBVO.builder()
                .name(name)
                .email(email)
                .mobile(mobile)
                .password(identifyPasswordEncoder.encode(password))
                .roles(CollectionUtils.isEmpty(roles)
                        ? new ArrayList<>()
                        : roles.stream()
                        .map(v -> JsonUtils.transfrom(v, RoleDBVO.class))
                        .collect(Collectors.toList())
                )
                .status(UserStatus.ACTIVE)
                .createUserId(operationUserId)
                .updateUserId(operationUserId)
                .createTime(curDate)
                .updateTime(curDate)
                .build();
        userDBVO = userDataManager.saveUser(userDBVO);
        //创建私有角色
        RoleDBVO privateRole = RoleDBVO.builder()
                .name(userDBVO.getName())
                .code(String.format("PR:%d", userDBVO.getId()))
                .type(RoleType.PERSON)
                .permissions(Collections.emptyList())
                .revokePermissions(Collections.emptyList())
                .createUserId(operationUserId)
                .updateUserId(operationUserId)
                .createTime(curDate)
                .updateTime(curDate)
                .status(DataStatus.ENABLE)
                .build();
        // userDBVO.getRoles().add(privateRole);
        privateRole = roleDataManager.saveRole(privateRole);
        //挂载rel
        relationOfUserAndRoleAutoRepo.save(RelationOfUserAndRoleDBVO.builder()
                .user(userDBVO)
                .role(privateRole)
                .grantType(GrantType.FIXED)
                .grantStartTime(new Date(0))
                .grantEndTime(new Date(0))
                .status(DataStatus.ENABLE)
                .createUserId(operationUserId)
                .updateUserId(operationUserId)
                .createTime(curDate)
                .updateTime(curDate)
                .build());
        log.info("create User success! user:{}`", JsonUtils.toJsonString(userDBVO));
        //todo 增加前后对比，增强审计日志的可读性
        //记录日志(OPerationLog)
        OperationLogDBVO operationLogDBVO = OperationLogUtils.buildCreateUserLog(SecurityContextHolder.getContext().getAuthentication(),
                name, email, password, roles);
        operationLogService.log(operationLogDBVO);
        return JsonUtils.transfrom(userDBVO, UserVO.class);
    }

    @Transactional
    @Override
    public UserVO modifyUserVO(UserVO userVO, List<RoleVO> roles) {
        UserDBVO userDBVO = JsonUtils.transfrom(userVO, UserDBVO.class);
        UserSecurityDetails authUser = AuthUtil.getUserDetail();
        int authUserId = null == authUser ? 0 : authUser.getId();
        Date operationTime = new Date();
        //是否清理角色
        boolean cleanRoles = CollectionUtils.isEmpty(roles);
        List<RelationOfUserAndRoleDBVO> orginalRels = relationOfUserAndRoleAutoRepo.findAllByUserId(userVO.getId());
        //移除所有的角色关系
        if (!CollectionUtils.isEmpty(orginalRels)) {
            orginalRels.forEach(v -> {
                v.setStatus(DataStatus.DISABLE);
                v.setUpdateUserId(authUserId);
                v.setUpdateTime(operationTime);
            });
        }
        if (!cleanRoles) {
            Map<Integer, RelationOfUserAndRoleDBVO> roleId2Rel = orginalRels.stream()
                    .collect(Collectors.toMap(k -> k.getRole().getId(), v -> v));
            for (RoleVO role : roles) {
                if (!roleId2Rel.containsKey(role.getId())) {
                    orginalRels.add(RelationOfUserAndRoleDBVO.builder()
                            .user(userDBVO)
                            .role(RoleDBVO.builder().id(role.getId()).build())
                            .status(DataStatus.ENABLE)
                            .grantType(GrantType.FIXED)
                            .grantStartTime(new Date(0L))
                            .grantEndTime(new Date(0L))
                            .updateTime(operationTime)
                            .createTime(operationTime)
                            .updateUserId(authUserId)
                            .createUserId(authUserId)
                            .build());
                } else {
                    RelationOfUserAndRoleDBVO rel = roleId2Rel.get(role.getId());
                    rel.setStatus(DataStatus.ENABLE);
                }
            }
        }
        relationOfUserAndRoleAutoRepo.saveAll(orginalRels);
        UserVO savedUserVO = JsonUtils.transfrom(userDataManager.saveUser(userDBVO), UserVO.class);
        log.info("modifyUserVO User success! user:{}`cleanRoles:{}`", JsonUtils.toJsonString(savedUserVO), cleanRoles);
        //todo 增加前后对比，增强审计日志的可读性
        //记录操作日志
        OperationLogDBVO operationLogDBVO = OperationLogUtils.buildUserModifyLog(SecurityContextHolder.getContext().getAuthentication(), savedUserVO);
        operationLogService.log(operationLogDBVO);
        return savedUserVO;
    }

    @Override
    public Set<UserVO> queryUsers(Collection<Integer> userIds) {
        List<UserDBVO> userDBVOS = userDataManager.findUsers(userIds);
        return userDBVOS.stream()
                .map(e -> JsonUtils.transfrom(e, UserVO.class))
                .collect(Collectors.toSet());
    }

    @Override
    public List<UserVO> findUsersNameLike(String nameKeyword) {
        return userDataManager.findUsersNameLike(nameKeyword).stream()
                .map(v -> JsonUtils.transfrom(v, UserVO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void removeUsers(List<Integer> ids) {
        List<UserDBVO> users = userDataManager.findUsers(ids);
        if (!CollectionUtils.isEmpty(users)) {
            for (UserDBVO user : users) {
                user.setStatus(UserStatus.DELETE);
                user.setUpdateTime(new Date());
                user.setUpdateUserId(Objects.requireNonNull(AuthUtil.getUserDetail()).getId());
            }
            userDataManager.saveUsers(users);
        }
    }

    @Transactional
    @Override
    public UserVO modifyUser(UserModifyModel model) {
        UserDBVO user = userDataManager.findUser(model.getId());
        if (CollectionUtils.isEmpty(model.getRoles())) {
            if (!CollectionUtils.isEmpty(user.getRoles())) {
                user.setRoles(Collections.emptyList());
            }
        } else {
            // 两边不一
            List<RoleDBVO> roles = roleDataManager.findRoles(model.getRoles().stream()
                    .map(UserModifyModel.RoleSelected::getId)
                    .collect(Collectors.toList()));
            if (!CollectionUtils.isEmpty(roles)) {
                user.setRoles(roles);
            } else {
                user.setRoles(Collections.emptyList());
            }
        }
        if (StringUtils.hasText(model.getPassword())) {
            user.setPassword(identifyPasswordEncoder.encode(model.getPassword()));
        }
        if (StringUtils.hasText(model.getMobile())) {
            user.setMobile(model.getMobile());
        }
        user.setName(model.getName());
        if (StringUtils.hasText(model.getEmail())) {
            user.setEmail(model.getEmail());
        }
        user.setUpdateUserId(Objects.requireNonNull(AuthUtil.getUserDetail()).getId());
        user.setUpdateTime(new Date());
        user.setStatus(UserStatus.findByCode(model.getUserStatus()));
        userDataManager.saveUser(user);
        return JsonUtils.transfrom(user, UserVO.class);
    }

}
