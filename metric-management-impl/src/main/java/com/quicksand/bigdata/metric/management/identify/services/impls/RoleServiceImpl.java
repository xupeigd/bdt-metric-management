package com.quicksand.bigdata.metric.management.identify.services.impls;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.RoleType;
import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.RoleDataManager;
import com.quicksand.bigdata.metric.management.identify.services.RoleService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RoleServiceImpl
 *
 * @author page
 * @date 2020/8/18 16:38
 */
@Slf4j
@Service
public class RoleServiceImpl
        implements RoleService {

    @Resource
    RoleDataManager roleDataManager;

    private RoleVO dbvo2Vo(RoleDBVO dbvo) {
        return JsonUtils.transfrom(dbvo, RoleVO.class);
    }

    @Override
    public List<RoleVO> queryAllRoles() {
        return roleDataManager.queryAllRoles()
                .stream()
                .map(this::dbvo2Vo)
                .collect(Collectors.toList());
    }

    @Override
    public RoleVO findRole(int roleId) {
        RoleDBVO roleDBVO = roleDataManager.findRole(roleId);
        if (null == roleDBVO) {
            return null;
        }
        return JsonUtils.transfrom(roleDBVO, RoleVO.class);
    }

    @Override
    public List<RoleVO> findRoles(List<Integer> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        Set<Integer> queryIds = new HashSet<>(roleIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        List<RoleDBVO> roles = roleDataManager.findRoles(queryIds);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(this::dbvo2Vo)
                .collect(Collectors.toList());
    }

    @Override
    public PageImpl<RoleVO> queryRoleByPage(int pageNo, int pageSize) {
        return cover2VO(roleDataManager.queryRoleByPage(PageRequest.of(pageNo - 1, pageSize)), pageNo, pageSize);
    }

    @Transactional
    @Override
    public RoleVO modifyName(int id, String name) {
        RoleDBVO role = roleDataManager.findRole(id);
        role.setName(name);
        role.setUpdateTime(new Date());
        return JsonUtils.transfrom(roleDataManager.saveRole(role), RoleVO.class);
    }

    @Transactional
    @Override
    public void deleteRoles(List<Integer> roleIds) {
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<RoleDBVO> roles = roleDataManager.findRoles(roleIds);
            if (!CollectionUtils.isEmpty(roles)) {
                for (RoleDBVO role : roles) {
                    role.setStatus(DataStatus.DISABLE);
                    role.setUpdateTime(new Date());
                }
                roleDataManager.saveRoles(roles);
            }
        }
    }

    @Transactional
    @Override
    public RoleVO createRole(String name, String code) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        assert userDetail != null;
        RoleDBVO dbvo = new RoleDBVO();
        dbvo.setName(name);
        dbvo.setCode(code);
        dbvo.setUpdateTime(new Date());
        dbvo.setCreateTime(new Date());
        dbvo.setStatus(DataStatus.ENABLE);
        dbvo.setType(RoleType.PUBLIC);
        dbvo.setCreateUserId(userDetail.getId());
        dbvo.setUpdateUserId(userDetail.getId());
        return JsonUtils.transfrom(roleDataManager.saveRole(dbvo), RoleVO.class);
    }

    private PageImpl<RoleVO> cover2VO(Page<RoleDBVO> dbvoPage, int curPage, int pageSize) {
        PageImpl<RoleVO> pvos = new PageImpl<>();
        if (null != dbvoPage) {
            List<RoleVO> userVOs = dbvoPage.getContent()
                    .stream()
                    .map(k -> JsonUtils.transfrom(k, RoleVO.class))
                    .collect(Collectors.toList());
            pvos.setItems(userVOs);
            pvos.setPageSize(pageSize);
            pvos.setPageNo(curPage);
            pvos.setTotal(dbvoPage.getTotalElements());
            pvos.setTotalPage(dbvoPage.getTotalPages());
        }
        return pvos;
    }

}
