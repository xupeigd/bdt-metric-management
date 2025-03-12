package com.quicksand.bigdata.metric.management.identify.dms.impls;

import com.quicksand.bigdata.metric.management.consts.UserStatus;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.repos.UserAutoRepo;
import com.quicksand.bigdata.metric.management.identify.repos.UserRepo;
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
import java.util.Optional;

/**
 * UserDataManagerImpl
 *
 * @author page
 * @date 2020/8/18 16:20
 */
@Component
public class UserDataManagerImpl
        implements UserDataManager {

    @Resource
    UserRepo userRepo;
    @Resource
    UserAutoRepo userAutoRepo;

    @Override
    public UserDBVO findUser(int userId) {
        Optional<UserDBVO> optional = userAutoRepo.findById(userId);
        if (!optional.isPresent()
                || UserStatus.DELETE.equals(optional.get().getStatus())) {
            return null;
        }
        return optional.get();
    }

    @Override
    public Page<UserDBVO> queryUserWithPage(Pageable pageable) {
        return userAutoRepo.findAll(pageable);
    }

    @Override
    public Page<UserDBVO> queryUserByKeywordWithPage(String keyword, Pageable pageable) {
        return userAutoRepo.findByNameLike("%" + keyword + "%", pageable);
    }

    @Override
    public Page<UserDBVO> queryUsersByRoleIdWithPage(Integer roleId, Pageable pageable) {
        //正常情况下，一个用户与一个角色只会有一个关系
        return userAutoRepo.findByRoleId(roleId, pageable);
    }

    @Override
    public Page<UserDBVO> queryUsersByKeywordAndRoleIdWithPage(String keyword, Integer roleId, Pageable pageable) {
        return userAutoRepo.findByKeywordAndRoleId("%" + keyword + "%", roleId, pageable);
    }

    @Override
    public UserDBVO findUserByName(String name) {
        return userAutoRepo.findByName(name);
    }

    @Override
    public UserDBVO findUserByEmail(String email) {
        return userAutoRepo.findByEmail(email);
    }

    @Transactional
    @Override
    public UserDBVO saveUser(UserDBVO userDBVO) {
        return userAutoRepo.save(userDBVO);
    }

    @Override
    public List<UserDBVO> findUsers(Collection<Integer> userIds) {
        return CollectionUtils.isEmpty(userIds) ? Collections.emptyList() : userAutoRepo.findAllById(userIds);
    }

    @Override
    public List<UserDBVO> findUsersNameLike(String nameKeyword) {
        return userAutoRepo.findByNameLike("%" + nameKeyword + "%", PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id")))
                .getContent();
    }

    @Override
    public List<UserDBVO> findUsersBySpecialProperty(String propertyName, String value) {
        return userRepo.findUserByOtherPk(propertyName, value);
    }

    @Transactional
    @Override
    public void saveUsers(Collection<UserDBVO> users) {
        userAutoRepo.saveAll(users);
    }

}
