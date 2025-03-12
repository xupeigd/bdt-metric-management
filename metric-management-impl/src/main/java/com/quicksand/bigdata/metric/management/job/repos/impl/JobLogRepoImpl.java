package com.quicksand.bigdata.metric.management.job.repos.impl;

import com.quicksand.bigdata.metric.management.job.core.model.JobLog;
import com.quicksand.bigdata.metric.management.job.repos.JobLogRepo;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JobLogRepoImpl
 *
 * @author page
 * @date 2023/2/2
 */
@Repository
public class JobLogRepoImpl
        implements JobLogRepo {

    @Resource
    EntityManager entityManager;

    @Override
    public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobLog> query = criteriaBuilder.createQuery(JobLog.class);
        Root<JobLog> root = query.from(JobLog.class);
        List<Predicate> predicates = new ArrayList<>();

        if (0 < jobGroup) {
            predicates.add(criteriaBuilder.equal(root.get("jobGroup"), jobGroup));
        }
        if (0 < jobId) {
            predicates.add(criteriaBuilder.equal(root.get("jobId"), jobId));
        }
        if (null != clearBeforeTime) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("triggerTime"), clearBeforeTime));
        }
        @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
        Predicate allPredicate = predicates.isEmpty() ? null : 1 < predicates.size() ? criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])) : predicates.get(0);
        allPredicate = null != allPredicate ? allPredicate : criteriaBuilder.gt(root.get("id"), 0);
        List<JobLog> resultList = entityManager.createQuery(query.select(root).where(allPredicate))
                .setFirstResult(Math.max(0, clearBeforeNum))
                .setMaxResults(pagesize)
                .getResultList();
        return CollectionUtils.isEmpty(resultList) ? Collections.emptyList() : resultList.stream().map(JobLog::getId).collect(Collectors.toList());
    }

    @Override
    public Page<JobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobLog> query = criteriaBuilder.createQuery(JobLog.class);
        Root<JobLog> queryRoot = query.from(JobLog.class);
        List<Predicate> queryPredicates = new ArrayList<>();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<JobLog> countRoot = countQuery.from(JobLog.class);
        List<Predicate> countPredicates = new ArrayList<>();

        if (0 == jobId && 0 < jobGroup) {
            queryPredicates.add(criteriaBuilder.equal(queryRoot.get("jobGroup"), jobGroup));
            countPredicates.add(criteriaBuilder.equal(countRoot.get("jobGroup"), jobGroup));
        }
        if (0 < jobId) {
            queryPredicates.add(criteriaBuilder.equal(queryRoot.get("jobId"), jobId));
            countPredicates.add(criteriaBuilder.equal(countRoot.get("jobId"), jobId));
        }
        if (null != triggerTimeStart) {
            queryPredicates.add(criteriaBuilder.greaterThanOrEqualTo(queryRoot.get("triggerTime"), triggerTimeStart));
            countPredicates.add(criteriaBuilder.greaterThanOrEqualTo(countRoot.get("triggerTime"), triggerTimeStart));
        }
        if (null != triggerTimeEnd) {
            queryPredicates.add(criteriaBuilder.lessThanOrEqualTo(queryRoot.get("triggerTime"), triggerTimeEnd));
            countPredicates.add(criteriaBuilder.lessThanOrEqualTo(countRoot.get("triggerTime"), triggerTimeEnd));
        }
        if (1 == logStatus) {
            queryPredicates.add(criteriaBuilder.equal(queryRoot.get("handleCode"), 200));
            countPredicates.add(criteriaBuilder.equal(countRoot.get("handleCode"), 200));
        }
        if (2 == logStatus) {
            queryPredicates.add(criteriaBuilder.or(criteriaBuilder.not(criteriaBuilder.in(queryRoot.get("triggerCode")).value(0).value(200)),
                    criteriaBuilder.not(criteriaBuilder.in(queryRoot.get("handleCode")).value(0).value(200))));
            countPredicates.add(criteriaBuilder.or(criteriaBuilder.not(criteriaBuilder.in(countRoot.get("triggerCode")).value(0).value(200)),
                    criteriaBuilder.not(criteriaBuilder.in(countRoot.get("handleCode")).value(0).value(200))));
        }
        if (3 == logStatus) {
            queryPredicates.add(criteriaBuilder.equal(queryRoot.get("triggerCode"), 200));
            queryPredicates.add(criteriaBuilder.equal(queryRoot.get("handleCode"), 0));
            countPredicates.add(criteriaBuilder.equal(countRoot.get("triggerCode"), 200));
            countPredicates.add(criteriaBuilder.equal(countRoot.get("handleCode"), 0));
        }
        @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
        Predicate allPredicate = 1 < queryPredicates.size() ? criteriaBuilder.and(queryPredicates.toArray(new Predicate[queryPredicates.size()])) : queryPredicates.get(0);
        @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
        Predicate countPredicate = 1 < countPredicates.size() ? criteriaBuilder.and(countPredicates.toArray(new Predicate[countPredicates.size()])) : countPredicates.get(0);

        Long count = entityManager.createQuery(countQuery.select(criteriaBuilder.count(countRoot)).where(countPredicate))
                .getSingleResult();

        List<JobLog> resultList = 0L >= count ? Collections.emptyList() : entityManager.createQuery(query.select(queryRoot)
                        .where(allPredicate).orderBy(new OrderImpl(countRoot.get("triggerTime"), false)))
                .setFirstResult(offset)
                .setMaxResults(pagesize)
                .getResultList();
        return new PageImpl<>(resultList, PageRequest.of(offset / pagesize, pagesize), count);
    }

}
