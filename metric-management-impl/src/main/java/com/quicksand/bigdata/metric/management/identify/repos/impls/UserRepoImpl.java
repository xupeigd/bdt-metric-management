package com.quicksand.bigdata.metric.management.identify.repos.impls;

import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.repos.UserRepo;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * UserRepoImpl
 *
 * @author page
 * @date 2022/8/30
 */
@Repository
public class UserRepoImpl
        implements UserRepo {

    @Resource
    EntityManager entityManager;

    @Override
    public List<UserDBVO> findUserByOtherPk(String col, String value) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDBVO> usersQuery = criteriaBuilder.createQuery(UserDBVO.class);
        usersQuery.where(criteriaBuilder.equal(usersQuery.from(UserDBVO.class).get(col), value));
        return entityManager.createQuery(usersQuery).getResultList();
    }
}
