package com.quicksand.bigdata.metric.management.job.repos.impl;

import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import com.quicksand.bigdata.metric.management.job.repos.JobInfoAutoRepo;
import com.quicksand.bigdata.metric.management.job.repos.JobInfoRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JobInfoRepo
 *
 * @author page
 * @date 2023/2/1
 */
@Repository
public class JobInfoRepoImpl
        implements JobInfoRepo {

    @Resource
    EntityManager entityManager;
    @Resource
    JobInfoAutoRepo jobInfoAutoRepo;

    @Override
    public Page<JobInfo> queryJobInfos(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobInfo> query = criteriaBuilder.createQuery(JobInfo.class);
        Root<JobInfo> queryRoot = query.from(JobInfo.class);
        List<Predicate> queryPredicates = new ArrayList<>();

        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<JobInfo> countRoot = countQuery.from(JobInfo.class);
        List<Predicate> countPredicates = new ArrayList<>();

        if (0 < triggerStatus) {
            queryPredicates.add(criteriaBuilder.equal(queryRoot.get("triggerStatus"), triggerStatus));
            countPredicates.add(criteriaBuilder.equal(countRoot.get("triggerStatus"), triggerStatus));
        }
        if (0 < jobGroup) {
            queryPredicates.add(criteriaBuilder.equal(queryRoot.get("jobGroup"), jobGroup));
            countPredicates.add(criteriaBuilder.equal(countRoot.get("jobGroup"), jobGroup));
        }
        if (StringUtils.hasText(jobDesc)) {
            queryPredicates.add(criteriaBuilder.like(queryRoot.get("jobDesc"), "%" + jobDesc + "%"));
            countPredicates.add(criteriaBuilder.like(countRoot.get("jobDesc"), "%" + jobDesc + "%"));
        }
        if (StringUtils.hasText(executorHandler)) {
            queryPredicates.add(criteriaBuilder.like(queryRoot.get("executorHandler"), "%" + executorHandler + "%"));
            countPredicates.add(criteriaBuilder.like(countRoot.get("executorHandler"), "%" + executorHandler + "%"));
        }
        if (StringUtils.hasText(author)) {
            queryPredicates.add(criteriaBuilder.like(queryRoot.get("author"), "%" + author + "%"));
            countPredicates.add(criteriaBuilder.like(countRoot.get("author"), "%" + author + "%"));
        }
        if (CollectionUtils.isEmpty(queryPredicates)) {
            return jobInfoAutoRepo.findAll(PageRequest.of(offset / pagesize, pagesize));
        }
        @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
        Predicate allPredicate = 1 < queryPredicates.size() ? criteriaBuilder.and(queryPredicates.toArray(new Predicate[queryPredicates.size()])) : queryPredicates.get(0);
        @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
        Predicate countPredicate = 1 < countPredicates.size() ? criteriaBuilder.and(countPredicates.toArray(new Predicate[countPredicates.size()])) : countPredicates.get(0);

        Long count = entityManager.createQuery(countQuery.select(criteriaBuilder.count(countRoot)).where(countPredicate))
                .getSingleResult();
        List<JobInfo> resultList = 0L >= count ? Collections.emptyList() : entityManager.createQuery(query.select(queryRoot).where(allPredicate))
                .setFirstResult(offset)
                .setMaxResults(pagesize)
                .getResultList();
        return new PageImpl<>(resultList, PageRequest.of(offset / pagesize, pagesize), count);
    }

}
