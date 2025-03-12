package com.quicksand.bigdata.metric.management.apis.services.impls;

import com.quicksand.bigdata.metric.management.apis.dbvos.AppDBVO;
import com.quicksand.bigdata.metric.management.apis.dbvos.InvokeApplyRecordDBVO;
import com.quicksand.bigdata.metric.management.apis.dbvos.QuotaDBVO;
import com.quicksand.bigdata.metric.management.apis.dbvos.RelationOfAppAndMetricDBVO;
import com.quicksand.bigdata.metric.management.apis.models.InvokeApplyModel;
import com.quicksand.bigdata.metric.management.apis.repos.InvokeApplyRecordAutoRepo;
import com.quicksand.bigdata.metric.management.apis.repos.QuotaAutoRepo;
import com.quicksand.bigdata.metric.management.apis.repos.RelationOfAppAndMetricAutoRepo;
import com.quicksand.bigdata.metric.management.apis.services.InvokeApplyService;
import com.quicksand.bigdata.metric.management.apis.vos.InvokeApplyRecordVO;
import com.quicksand.bigdata.metric.management.apis.vos.RelationOfAppAndMetricVO;
import com.quicksand.bigdata.metric.management.consts.*;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.repos.MetricAutoRepo;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * InvokeSupplyServiceImpl
 *
 * @author page
 * @date 2022/10/12
 */
@Service
public class InvokeApplyServiceImpl
        implements InvokeApplyService {

    @Resource
    QuotaAutoRepo quotaAutoRepo;
    @Resource
    MetricAutoRepo metricAutoRepo;
    @Resource
    RelationOfAppAndMetricAutoRepo relationOfAppAndMetricAutoRepo;
    @Resource
    InvokeApplyRecordAutoRepo invokeApplyRecordAutoRepo;
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public InvokeApplyRecordVO findApplyRecord(int id) {
        return invokeApplyRecordAutoRepo.findById(id)
                .map(v -> JsonUtils.transfrom(v, InvokeApplyRecordVO.class))
                .orElse(null);
    }

    @Transactional
    @Override
    public InvokeApplyRecordVO approve(int id, ApproveState state, String approveComment) throws IllegalArgumentException {
        InvokeApplyRecordDBVO record = invokeApplyRecordAutoRepo.findById(id).orElse(null);
        if (null == record) {
            throw new IllegalArgumentException(String.format("申请记录不存在！id:%d", id));
        }
        UserSecurityDetails approveUser = AuthUtil.getUserDetail();
        if (null == approveUser) {
            throw new IllegalArgumentException("审批人信息过期！");
        }
        //审核中的所有指标的业务负责人应该包含当前用户
        for (MetricDBVO metric : record.getMetrics()) {
            List<Integer> businessOwner = metric.getBusinessOwners().stream().map(UserDBVO::getId).collect(Collectors.toList());
            if (!businessOwner.contains(approveUser.getId())) {
                throw new IllegalArgumentException(String.format("审批的指标[%s]不在当前用户审核权限范围内", metric.getCnName()));
            }
            List<RelationOfAppAndMetricDBVO> allByAppIdAndMetricId = relationOfAppAndMetricAutoRepo.findAllByAppIdAndMetricId(record.getApp().getId(), metric.getId());
            if (!CollectionUtils.isEmpty(allByAppIdAndMetricId)) {
                throw new IllegalArgumentException(String.format("审批的指标[%s]与应用[%s]已存在绑定关系", metric.getCnName(), record.getApp().getName()));
            }
        }

        Date date = new Date();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        record.setApprovedState(state);
        record.setApprovedComment(StringUtils.hasText(approveComment) ? approveComment : "");
        record.setApprovedUserId(null == userDetail ? 0 : userDetail.getId());
        record.setUpdateTime(date);
        record.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
        record.setApprovedTime(date);
        if (Objects.equals(ApproveState.APPROVED, state)) {
            //移除可能存在的失效记录
            Map<Integer, RelationOfAppAndMetricDBVO> metricId2Rela = relationOfAppAndMetricAutoRepo.findIneffectiveRelasByAppIdAndMetricIds(record.getApp().getId(),
                            record.getMetrics().stream()
                                    .map(MetricDBVO::getId)
                                    .collect(Collectors.toList())).stream()
                    .collect(Collectors.toMap(k -> k.getMetric().getId(), v -> v));
            //核准时需要增加调用的额度等数据
            for (MetricDBVO metric : record.getMetrics()) {
                QuotaDBVO quota = QuotaDBVO.builder()
                        .appId(record.getApp().getId())
                        .metricId(metric.getId())
                        .quota(null == metric.getDefaultQuota() ? 1000L : metric.getDefaultQuota())
                        .applyRecord(record)
                        .grantType(GrantType.FIXED)
                        .grantStartTime(date)
                        .grantEndTime(date)
                        .cronExpress(StringUtils.hasText(metric.getDefaultCronExpress()) ? metric.getDefaultCronExpress() : MetricDBVO.CRON_MONTH)
                        .mode(QuotaRefreshMode.Periodicity)
                        .status(DataStatus.ENABLE)
                        .createTime(date)
                        .updateTime(date)
                        .createUserId(null == userDetail ? 0 : userDetail.getId())
                        .updateUserId(null == userDetail ? 0 : userDetail.getId())
                        .build();
                quotaAutoRepo.save(quota);
                RelationOfAppAndMetricDBVO rela = metricId2Rela.getOrDefault(metric.getId(), RelationOfAppAndMetricDBVO.builder()
                        .appId(record.getApp().getId())
                        .metric(metric)
                        .quota(quota)
                        .applyRecord(record)
                        .grantType(GrantType.FIXED)
                        .grantStartTime(date)
                        .grantEndTime(date)
                        .createTime(date)
                        .createUserId(null == userDetail ? 0 : userDetail.getId())
                        .build());
                rela.setApplyRecord(record);
                rela.setGrantType(GrantType.FIXED);
                rela.setGrantStartTime(date);
                rela.setGrantEndTime(date);
                rela.setStatus(DataStatus.ENABLE);
                rela.setUpdateTime(date);
                rela.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
                relationOfAppAndMetricAutoRepo.save(rela);
            }
        }
        return JsonUtils.transfrom(invokeApplyRecordAutoRepo.save(record), InvokeApplyRecordVO.class);
    }

    @Override
    public List<InvokeApplyRecordVO> createApply(InvokeApplyModel model) {
        Date date = new Date();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        List<MetricDBVO> metricDBVOList = metricAutoRepo.findAllById(model.getMetricIds());
        //单个指标的时候，可能是从指标详情中发起的调用申请，由后台校验是否已经有调用权限
        if (model.getMetricIds().size() == 1) {
            List<RelationOfAppAndMetricDBVO> allByAppIdAndMetric = relationOfAppAndMetricAutoRepo.findAllByAppIdAndMetricId(model.getAppId(), model.getMetricIds().get(0));
            if (!CollectionUtils.isEmpty(allByAppIdAndMetric)) {
                throw new IllegalArgumentException("应用已拥有该指标的调用权限！");
            }
        }

        //逻辑调整为指标业务负责人负责审核，所以按业务负责人拆分为多条
        Map<String, List<MetricDBVO>> groupMap = metricDBVOList.stream().collect(Collectors.groupingBy(v -> getGroupKey(v.getBusinessOwners())));
        List<InvokeApplyRecordDBVO> insertList = new ArrayList<>();
        for (List<MetricDBVO> metrics : groupMap.values()) {
            insertList.add(InvokeApplyRecordDBVO.builder()
                    .app(AppDBVO.builder()
                            .id(model.getAppId())
                            .build())
                    .metrics(metrics)
                    .description(StringUtils.hasText(model.getDescription()) ? model.getDescription() : "")
                    .approvedComment("")
                    .status(DataStatus.ENABLE)
                    .approvedState(ApproveState.DEFAULT)
                    .createTime(date)
                    .updateTime(date)
                    .approvedTime(date)
                    .approvedType(ApproveType.DEFAULT)
                    .createUser(UserDBVO.builder().id(null == userDetail ? 0 : userDetail.getId()).build())
                    .updateUserId(null == userDetail ? 0 : userDetail.getId())
                    .approvedUserId(null == userDetail ? 0 : userDetail.getId())
                    .qpd(model.getQpd())
                    .qps(model.getQps())
                    .tp99(model.getTp99())
                    .build());
        }
        return invokeApplyRecordAutoRepo.saveAll(insertList).stream().map(v -> JsonUtils.transfrom(v, InvokeApplyRecordVO.class)).collect(Collectors.toList());
        //return JsonUtils.transfrom(invokeApplyRecordAutoRepo.saveAll(insertList), InvokeApplyRecordVO.class);
    }

    @Override
    public Page<InvokeApplyRecordVO> fetchApplys(List<Integer> appIds, List<Integer> filterUserIds, List<ApproveState> filterStates, String metricKeyword, Integer pageNo, Integer pageSize) {
        Page<InvokeApplyRecordDBVO> page;
        String metricsCnNameKeyword = StringUtils.hasText(metricKeyword) ? "%" + metricKeyword + "%" : "%";
        PageRequest pageReq = PageRequest.of(pageNo - 1, pageSize, Sort.by("updateTime").descending());
        if (!CollectionUtils.isEmpty(appIds) && !CollectionUtils.isEmpty(filterUserIds)) {
            page = invokeApplyRecordAutoRepo.findDistinctByAppIdInAndCreateUserIdInAndApprovedStateInAndMetricsCnNameLike(appIds, filterUserIds, filterStates, metricsCnNameKeyword, pageReq);
        } else if (!CollectionUtils.isEmpty(appIds)) {
            page = invokeApplyRecordAutoRepo.findDistinctByAppIdInAndApprovedStateInAndMetricsCnNameLike(appIds, filterStates, metricsCnNameKeyword, pageReq);
        } else if (!CollectionUtils.isEmpty(filterUserIds)) {
            page = invokeApplyRecordAutoRepo.findDistinctByCreateUserIdInAndApprovedStateInAndMetricsCnNameLike(filterUserIds, filterStates, metricsCnNameKeyword, pageReq);
        } else {
            page = invokeApplyRecordAutoRepo.findDistinctByApprovedStateInAndMetricsCnNameLike(filterStates, metricsCnNameKeyword, pageReq);
        }
        return page.map(v -> JsonUtils.transfrom(v, InvokeApplyRecordVO.class));
    }

    @Override
    public List<RelationOfAppAndMetricVO> findAllEffectiveRelasByAppId(int appId) {
        return relationOfAppAndMetricAutoRepo.findAllEffectiveRelasByAppId(appId).stream()
                .map(v -> JsonUtils.transfrom(v, RelationOfAppAndMetricVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<InvokeApplyRecordVO> fetchApproveLists(List<Integer> appIds, List<Integer> filterUserIds, List<Integer> fiterStates, String metricKeyword, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<InvokeApplyRecordDBVO> criteriaQuery = criteriaBuilder.createQuery(InvokeApplyRecordDBVO.class);
        Root<InvokeApplyRecordDBVO> root = criteriaQuery.from(InvokeApplyRecordDBVO.class);
        List<Predicate> predicateList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(appIds)) {
            if (appIds.size() == 1) {
                Predicate businessOwnerIdEqual = criteriaBuilder.equal(root.get("app"), appIds.get(0));
                predicateList.add(businessOwnerIdEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("app"));
                for (Integer id : appIds) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (!CollectionUtils.isEmpty(filterUserIds)) {
            if (filterUserIds.size() == 1) {
                Predicate businessOwnerIdEqual = criteriaBuilder.equal(root.get("createUser"), filterUserIds.get(0));
                predicateList.add(businessOwnerIdEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("createUser"));
                for (Integer id : filterUserIds) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (!CollectionUtils.isEmpty(fiterStates)) {
            if (fiterStates.size() == 1) {
                Predicate businessOwnerIdEqual = criteriaBuilder.equal(root.get("approvedState"), fiterStates.get(0));
                predicateList.add(businessOwnerIdEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("approvedState"));
                for (Integer id : fiterStates) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (!AuthUtil.isAdmin() || StringUtils.hasText(metricKeyword)) {
            Join<InvokeApplyRecordDBVO, MetricDBVO> metricJoin = root.join("metrics", javax.persistence.criteria.JoinType.LEFT);
            if (StringUtils.hasText(metricKeyword)) {
                Predicate enName = criteriaBuilder.like(metricJoin.get("enName"), "%" + metricKeyword + "%");
                Predicate cnName = criteriaBuilder.like(metricJoin.get("cnName"), "%" + metricKeyword + "%");
                Predicate enAlias = criteriaBuilder.like(metricJoin.get("enAlias"), "%" + metricKeyword + "%");
                Predicate cnAlias = criteriaBuilder.like(metricJoin.get("cnAlias"), "%" + metricKeyword + "%");
                predicateList.add(criteriaBuilder.or(enName, cnName, enAlias, cnAlias));
            }
            if (!AuthUtil.isAdmin()) {
                Join<MetricDBVO, UserDBVO> metricJoinUserJoin = metricJoin.join("businessOwners", javax.persistence.criteria.JoinType.LEFT);
                Predicate userCondition = criteriaBuilder.equal(metricJoinUserJoin.get("id"), Objects.requireNonNull(AuthUtil.getUserDetail()).getId());
                predicateList.add(userCondition);
            }
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
        List<InvokeApplyRecordDBVO> countList = entityManager.createQuery(criteriaQuery).getResultList();
        //分页
        criteriaQuery.orderBy(orderList);
        TypedQuery<InvokeApplyRecordDBVO> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<InvokeApplyRecordDBVO> resultList = query.getResultList();
        PageImpl<InvokeApplyRecordDBVO> page = new PageImpl<>(resultList, pageable, countList.size());
        return page.map(v -> JsonUtils.transfrom(v, InvokeApplyRecordVO.class));
    }

    public List<RelationOfAppAndMetricVO> findAllEffectiveRelas() {
        return relationOfAppAndMetricAutoRepo.findAllEffectiveRelas().stream()
                .map(v -> JsonUtils.transfrom(v, RelationOfAppAndMetricVO.class))
                .collect(Collectors.toList());
    }

    private String getGroupKey(List<UserDBVO> businessOwners) {
        if (CollectionUtils.isEmpty(businessOwners)) {
            return "none";
        } else {
            return businessOwners.stream().map(v -> v.getId().toString()).collect(Collectors.joining(","));
        }
    }

}
