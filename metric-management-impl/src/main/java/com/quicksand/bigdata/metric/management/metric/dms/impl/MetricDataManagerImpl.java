package com.quicksand.bigdata.metric.management.metric.dms.impl;

import com.quicksand.bigdata.metric.management.apis.dbvos.MetricInvokeStatisicsDBVO;
import com.quicksand.bigdata.metric.management.apis.dbvos.RelationOfAppAndMetricDBVO;
import com.quicksand.bigdata.metric.management.apis.repos.MetricInvokeStatisicsAutoRepo;
import com.quicksand.bigdata.metric.management.apis.repos.RelationOfAppAndMetricAutoRepo;
import com.quicksand.bigdata.metric.management.consts.DomainType;
import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.MetricQueryModel;
import com.quicksand.bigdata.metric.management.metric.repos.MetricAutoRepo;
import com.quicksand.bigdata.metric.management.metric.vos.AppInvokeDetailVO;
import com.quicksand.bigdata.metric.management.metric.vos.CommonDownListVO;
import com.quicksand.bigdata.metric.management.utils.JPAModelMapUtils;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MetricDataManagerImpl
 *
 * @author page
 * @date 2022-07-27
 */
@Slf4j
@Component
public class MetricDataManagerImpl
        implements MetricDataManager {

    @Resource
    MetricAutoRepo metricAutoRepo;
    @Resource
    RelationOfAppAndMetricAutoRepo relationOfAppAndMetricAutoRepo;
    @Resource
    MetricInvokeStatisicsAutoRepo metricInvokeStatisicsAutoRepo;
    @PersistenceContext
    EntityManager entityManager;


    @Override
    public MetricDBVO findByMetricId(int metricId) {
        return metricAutoRepo.findById(metricId).orElse(null);
    }

    @Override
    public void updateMetric(MetricDBVO metricDBVO) {
        metricAutoRepo.save(metricDBVO);
    }

    @Override
    public MetricDBVO saveMetric(MetricDBVO metricDBVO) {
        return metricAutoRepo.save(metricDBVO);
    }

    @Override
    public List<MetricDBVO> findMetricByName(String name, Integer datasetId) {
        return metricAutoRepo.findAll(Sort.by("id"))
                .stream()
                .filter(f -> !StringUtils.hasText(name)
                        || f.getCnName().contains(name)
                        || f.getEnName().contains(name)
                        || f.getCnAlias().contains(name)
                        || f.getEnAlias().contains(name))
                .filter(f -> null == datasetId || Objects.equals(datasetId, f.getDataset().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<MetricDBVO> queryAllMetricsByConditions(MetricQueryModel queryModel, Pageable pageable) {
        if (null != queryModel.getAppId()) {
            return queryAllMetricsByAppId(queryModel, pageable);
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MetricDBVO> criteriaQuery = criteriaBuilder.createQuery(MetricDBVO.class);
        Root<MetricDBVO> root = criteriaQuery.from(MetricDBVO.class);
        List<Predicate> predicateList = new ArrayList<>();
        if (StringUtils.hasText(queryModel.getMetricKeyword())) {
            Predicate enName = criteriaBuilder.like(root.get("enName"), "%" + queryModel.getMetricKeyword() + "%");
            Predicate cnName = criteriaBuilder.like(root.get("cnName"), "%" + queryModel.getMetricKeyword() + "%");
            Predicate enAlias = criteriaBuilder.like(root.get("enAlias"), "%" + queryModel.getMetricKeyword() + "%");
            Predicate cnAlias = criteriaBuilder.like(root.get("cnAlias"), "%" + queryModel.getMetricKeyword() + "%");
            predicateList.add(criteriaBuilder.or(enName, cnName, enAlias, cnAlias));
        }
        if (null != queryModel.getPubsub()) {
            Predicate pubSubEqual = criteriaBuilder.equal(root.get("pubsub"), queryModel.getPubsub());
            predicateList.add(pubSubEqual);
        }
        if (null != queryModel.getClusterType()) {
            Predicate clusterTypeEqual = criteriaBuilder.equal(root.get("clusterType"), queryModel.getClusterType());
            predicateList.add(clusterTypeEqual);
        }
        if (!CollectionUtils.isEmpty(queryModel.getDatasetIds()) || StringUtils.hasText(queryModel.getDatasetKeyword())) {
            Join<MetricDBVO, DatasetDBVO> datasetJoin = root.join("dataset", JoinType.LEFT);
            if (!CollectionUtils.isEmpty(queryModel.getDatasetIds())) {
                if (queryModel.getDatasetIds().size() == 1) {
                    Predicate dataSetIdEqual = criteriaBuilder.equal(datasetJoin.get("id"), queryModel.getDatasetIds().get(0));
                    predicateList.add(dataSetIdEqual);
                } else {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(datasetJoin.get("id"));
                    for (Integer id : queryModel.getDatasetIds()) {
                        in.value(id);//存入值
                    }
                    predicateList.add(in);
                }
            }
            if (StringUtils.hasText(queryModel.getDatasetKeyword())) {
                Predicate dataSetNameLike = criteriaBuilder.like(datasetJoin.get("name"), "%" + queryModel.getDatasetKeyword() + "%");
                predicateList.add(dataSetNameLike);
            }
        }
        if (!CollectionUtils.isEmpty(queryModel.getTopicIds())) {
            if (queryModel.getTopicIds().size() == 1) {
                Predicate businessDomainEqual = criteriaBuilder.equal(root.get("topicDomain"), queryModel.getTopicIds().get(0));
                predicateList.add(businessDomainEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("topicDomain"));
                for (Integer id : queryModel.getTopicIds()) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (!CollectionUtils.isEmpty(queryModel.getBusinessIds())) {
            if (queryModel.getBusinessIds().size() == 1) {
                Predicate businessDomainEqual = criteriaBuilder.equal(root.get("businessDomain"), queryModel.getBusinessIds().get(0));
                predicateList.add(businessDomainEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("businessDomain"));
                for (Integer id : queryModel.getBusinessIds()) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }

        }
        if (!CollectionUtils.isEmpty(queryModel.getBusinessOwners())) {
            Join<MetricDBVO, UserDBVO> businessOwnerJoin = root.join("businessOwners", JoinType.LEFT);
            if (queryModel.getBusinessOwners().size() == 1) {
                Predicate businessOwnerIdEqual = criteriaBuilder.equal(businessOwnerJoin.get("id"), queryModel.getBusinessOwners().get(0));
                predicateList.add(businessOwnerIdEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(businessOwnerJoin.get("id"));
                for (Integer id : queryModel.getBusinessOwners()) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (!CollectionUtils.isEmpty(queryModel.getTechOwners())) {
            Join<MetricDBVO, UserDBVO> techOwnerJoin = root.join("techOwners", JoinType.LEFT);
            if (queryModel.getTechOwners().size() == 1) {
                Predicate techOwnerIdEqual = criteriaBuilder.equal(techOwnerJoin.get("id"), queryModel.getTechOwners().get(0));
                predicateList.add(techOwnerIdEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(techOwnerJoin.get("id"));
                for (Integer id : queryModel.getTechOwners()) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (null != queryModel.getAppId()) {
            List<RelationOfAppAndMetricDBVO> byAppId = relationOfAppAndMetricAutoRepo.findByAppId(queryModel.getAppId());
            CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("id"));
            for (RelationOfAppAndMetricDBVO relationOfAppAndMetricDBVO : byAppId) {
                in.value(relationOfAppAndMetricDBVO.getMetric().getId());
            }
            predicateList.add(in);
        }
        List<Order> orderList = new ArrayList<>();
        for (Sort.Order userOrder : pageable.getSort()) {
            if (userOrder.isAscending()) {
                orderList.add(criteriaBuilder.asc(root.get(userOrder.getProperty())));
            } else {
                orderList.add(criteriaBuilder.desc(root.get(userOrder.getProperty())));
            }
        }
        criteriaQuery.distinct(true);
        criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
        //总数
        List<MetricDBVO> countList = entityManager.createQuery(criteriaQuery).getResultList();
        //分页
        criteriaQuery.orderBy(orderList);
        TypedQuery<MetricDBVO> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<MetricDBVO> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, countList.size());
    }

    public Page<MetricDBVO> queryAllMetricsByAppId(MetricQueryModel queryModel, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<MetricDBVO> criteriaQuery = criteriaBuilder.createQuery(MetricDBVO.class);
        Root<RelationOfAppAndMetricDBVO> root = criteriaQuery.from(RelationOfAppAndMetricDBVO.class);
        List<Predicate> predicateList = new ArrayList<>();
        if (null != queryModel.getAppId()) {
            Predicate id = criteriaBuilder.equal(root.get("appId"), queryModel.getAppId());
            predicateList.add(id);
        }
        Join<RelationOfAppAndMetricDBVO, MetricDBVO> metricJoin = root.join("metric", JoinType.LEFT);
        criteriaQuery.select(metricJoin);
        if (StringUtils.hasText(queryModel.getMetricKeyword())) {
            Predicate enName = criteriaBuilder.like(metricJoin.get("enName"), "%" + queryModel.getMetricKeyword() + "%");
            Predicate cnName = criteriaBuilder.like(metricJoin.get("cnName"), "%" + queryModel.getMetricKeyword() + "%");
            Predicate enAlias = criteriaBuilder.like(metricJoin.get("enAlias"), "%" + queryModel.getMetricKeyword() + "%");
            Predicate cnAlias = criteriaBuilder.like(metricJoin.get("cnAlias"), "%" + queryModel.getMetricKeyword() + "%");
            predicateList.add(criteriaBuilder.or(enName, cnName, enAlias, cnAlias));
        }
        if (null != queryModel.getPubsub()) {
            Predicate pubSubEqual = criteriaBuilder.equal(metricJoin.get("pubsub"), queryModel.getPubsub());
            predicateList.add(pubSubEqual);
        }
        if (null != queryModel.getClusterType()) {
            Predicate clusterTypeEqual = criteriaBuilder.equal(metricJoin.get("clusterType"), queryModel.getClusterType());
            predicateList.add(clusterTypeEqual);
        }

        if (!CollectionUtils.isEmpty(queryModel.getDatasetIds()) || StringUtils.hasText(queryModel.getDatasetKeyword())) {
            Join<MetricDBVO, DatasetDBVO> datasetJoin = metricJoin.join("dataset", JoinType.LEFT);
            if (!CollectionUtils.isEmpty(queryModel.getDatasetIds())) {
                if (queryModel.getDatasetIds().size() == 1) {
                    Predicate dataSetIdEqual = criteriaBuilder.equal(datasetJoin.get("id"), queryModel.getDatasetIds().get(0));
                    predicateList.add(dataSetIdEqual);
                } else {
                    CriteriaBuilder.In<Integer> in = criteriaBuilder.in(datasetJoin.get("id"));
                    for (Integer id : queryModel.getDatasetIds()) {
                        in.value(id);//存入值
                    }
                    predicateList.add(in);
                }
            }
            if (StringUtils.hasText(queryModel.getDatasetKeyword())) {
                Predicate dataSetNameLike = criteriaBuilder.like(datasetJoin.get("name"), "%" + queryModel.getDatasetKeyword() + "%");
                predicateList.add(dataSetNameLike);
            }
        }
        if (!CollectionUtils.isEmpty(queryModel.getTopicIds())) {
            if (queryModel.getTopicIds().size() == 1) {
                Predicate businessDomainEqual = criteriaBuilder.equal(metricJoin.get("topicDomain"), queryModel.getTopicIds().get(0));
                predicateList.add(businessDomainEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(metricJoin.get("topicDomain"));
                for (Integer id : queryModel.getTopicIds()) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (!CollectionUtils.isEmpty(queryModel.getBusinessIds())) {
            if (queryModel.getBusinessIds().size() == 1) {
                Predicate businessDomainEqual = criteriaBuilder.equal(metricJoin.get("businessDomain"), queryModel.getBusinessIds().get(0));
                predicateList.add(businessDomainEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(metricJoin.get("businessDomain"));
                for (Integer id : queryModel.getBusinessIds()) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }

        }
        if (!CollectionUtils.isEmpty(queryModel.getBusinessOwners())) {
            Join<MetricDBVO, UserDBVO> businessOwnerJoin = metricJoin.join("businessOwners", JoinType.LEFT);
            if (queryModel.getBusinessOwners().size() == 1) {
                Predicate businessOwnerIdEqual = criteriaBuilder.equal(businessOwnerJoin.get("id"), queryModel.getBusinessOwners().get(0));
                predicateList.add(businessOwnerIdEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(businessOwnerJoin.get("id"));
                for (Integer id : queryModel.getBusinessOwners()) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (!CollectionUtils.isEmpty(queryModel.getTechOwners())) {
            Join<MetricDBVO, UserDBVO> techOwnerJoin = metricJoin.join("techOwners", JoinType.LEFT);
            if (queryModel.getTechOwners().size() == 1) {
                Predicate techOwnerIdEqual = criteriaBuilder.equal(techOwnerJoin.get("id"), queryModel.getTechOwners().get(0));
                predicateList.add(techOwnerIdEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(techOwnerJoin.get("id"));
                for (Integer id : queryModel.getTechOwners()) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }

//        if (null != queryModel.getAppId()) {
//            criteriaQuery.select(root);
//            Root<RelationOfAppAndMetricDBVO> troot = criteriaQuery.from(RelationOfAppAndMetricDBVO.class);
//            Predicate predicate = criteriaBuilder.equal(root.get("id"), troot.get("metric"));
//            predicateList.add(predicate);
//            Predicate techOwnerIdEqual = criteriaBuilder.equal(troot.get("id"), queryModel.getAppId());
//            predicateList.add(techOwnerIdEqual);
//        }
//        if (null != queryModel.getAppId()) {
//            criteriaQuery.select(root);
//            Root<RelationOfAppAndMetricDBVO> troot = criteriaQuery.from(RelationOfAppAndMetricDBVO.class);
//            Join<RelationOfAppAndMetricDBVO, MetricDBVO> metricDBVOMetricDBVOJoin = troot.join("metric", JoinType.INNER);
//            Predicate predicate = criteriaBuilder.equal(metricDBVOMetricDBVOJoin.get("id"),queryModel.getAppId());
//            predicateList.add(predicate);
//        }
        List<Order> orderList = new ArrayList<>();
        for (Sort.Order userOrder : pageable.getSort()) {
            if (userOrder.isAscending()) {
                orderList.add(criteriaBuilder.asc(metricJoin.get(userOrder.getProperty())));
            } else {
                orderList.add(criteriaBuilder.desc(metricJoin.get(userOrder.getProperty())));
            }
        }
        criteriaQuery.distinct(true);
        criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
        //总数
        List<MetricDBVO> countList = entityManager.createQuery(criteriaQuery).getResultList();
        //分页
        criteriaQuery.orderBy(orderList);
        TypedQuery<MetricDBVO> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<MetricDBVO> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, countList.size());
    }

    @Override
    public List<MetricDBVO> findMetricsByDomain(DomainType domainType, int domainId) {
        if (null == domainType) {
            return Collections.emptyList();
        }
        return Objects.equals(DomainType.Topic, domainType)
                ? metricAutoRepo.findByTopicDomainId(domainId)
                : metricAutoRepo.findByBusinessDomainId(domainId);
    }

    @Override
    public List<MetricDBVO> findBySerialNumber(String serialNumber) {
        if (StringUtils.hasText(serialNumber)) {
            return metricAutoRepo.findBySerialNumber(serialNumber);
        }
        return Collections.emptyList();
    }

    @Override
    public List<MetricDBVO> findByMetricIds(Collection<Integer> ids) {
        return metricAutoRepo.findAllById(ids);
    }

    @Override
    public List<CommonDownListVO> findAppSurplusMetricsList(int appId) {
        List<Object[]> onlineMetricList = metricAutoRepo.findAppSurplusOnlineMetrics(appId);
        return JPAModelMapUtils.mapping(onlineMetricList, CommonDownListVO.class);
    }

    @Override
    public Page<AppInvokeDetailVO> findAppInvokeInfoByMetricId(int metricId, Pageable pageable) {
        Page<Object[]> page = metricAutoRepo.findAppInvokeInfoByMetricId(metricId, pageable);
        List<AppInvokeDetailVO> mapping = JPAModelMapUtils.mapping(page.getContent(), AppInvokeDetailVO.class);
        Date curDate = new Date();
        SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> statisticsList = metricInvokeStatisicsAutoRepo.fetchDateRangeStatistics(metricId,
                Try.of(() -> yyyyMMddHHmmss.parse(String.format("%s 00:00:00", yyyyMMdd.format(DateUtils.addDays(curDate, -30))))).get(),
                Try.of(() -> yyyyMMddHHmmss.parse(String.format("%s 23:59:59", yyyyMMdd.format(curDate)))).get());
        if (!CollectionUtils.isEmpty(statisticsList)) {
            Map<Long, MetricInvokeStatisicsDBVO> appId2Statistics = statisticsList.stream()
                    .map(v -> JsonUtils.transfrom(v, MetricInvokeStatisicsDBVO.class))
                    .collect(Collectors.toMap(k -> (long) k.getAppId(), Function.identity()));
            for (AppInvokeDetailVO v : mapping) {
                MetricInvokeStatisicsDBVO statistics = appId2Statistics.get(v.getAppId());
                if (null != statistics) {
                    v.setMonthInvokeAvg(null == statistics.getDayCost() ? "-" : String.valueOf(statistics.getDayCost() / 30));
                    v.setMonthInvokeTop(null == statistics.getMaxQps() ? "-" : String.valueOf(statistics.getMaxQps()));
                }
            }
        }
        return new PageImpl<>(mapping, pageable, page.getTotalElements());
    }

    @Override
    public List<MetricDBVO> findByMetricsByPubsubState(PubsubStatus pubsubStatus) {
        return metricAutoRepo.findAllByPubsubOrderByUpdateTime(pubsubStatus);
    }

    @Override
    public String getMaxSerialNumberByTopicAndBusiness(Integer topicId, Integer businessId) {
        return metricAutoRepo.getMaxSerialNumberByTopicAndBusiness(topicId, businessId);
    }

    @Override
    public List<MetricDBVO> findByEnNameOrCnName(String enName, String cnName) {
        return metricAutoRepo.findAllByEnNameOrCnName(enName, cnName);
    }

    @Override
    public MetricDBVO findByEnName(String name) {
        return metricAutoRepo.findByEnName(name);
    }

    @Override
    public MetricDBVO findMetricByEnNameOrSerialNumber(String name, String serialNumber) {
        return metricAutoRepo.findMetricByEnNameOrSerialNumber(name, serialNumber);
    }

}
