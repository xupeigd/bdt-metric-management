package com.quicksand.bigdata.metric.management.apis.services.impls;

import com.quicksand.bigdata.metric.management.apis.dbvos.AppDBVO;
import com.quicksand.bigdata.metric.management.apis.dbvos.AuthTokenDBVO;
import com.quicksand.bigdata.metric.management.apis.dbvos.QuotaDBVO;
import com.quicksand.bigdata.metric.management.apis.dbvos.RelationOfAppAndMetricDBVO;
import com.quicksand.bigdata.metric.management.apis.models.AppModifyModel;
import com.quicksand.bigdata.metric.management.apis.repos.AppAutoRepo;
import com.quicksand.bigdata.metric.management.apis.repos.AuthTokenAutoRepo;
import com.quicksand.bigdata.metric.management.apis.repos.QuotaAutoRepo;
import com.quicksand.bigdata.metric.management.apis.repos.RelationOfAppAndMetricAutoRepo;
import com.quicksand.bigdata.metric.management.apis.services.AppService;
import com.quicksand.bigdata.metric.management.apis.vos.AppVO;
import com.quicksand.bigdata.metric.management.apis.vos.AuthTokenVO;
import com.quicksand.bigdata.metric.management.consts.AppType;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyPasswordEncoder;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.models.DropDownListModel;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.vos.CommonDownListVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.tools.services.CacheService;
import com.quicksand.bigdata.metric.management.utils.JPAModelMapUtils;
import com.quicksand.bigdata.vars.security.service.AppTokenProvider;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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
 * AppServiceImpl
 *
 * @author page
 * @date 2022/9/28
 */
@Slf4j
@Service
public class AppServiceImpl
        implements AppService, AppTokenProvider {

    @Resource
    AppAutoRepo appAutoRepo;
    @Resource
    QuotaAutoRepo quotaAutoRepo;
    @Resource
    RelationOfAppAndMetricAutoRepo relationOfAppAndMetricAutoRepo;
    @Resource
    IdentifyPasswordEncoder identifyPasswordEncoder;
    @Resource
    AuthTokenAutoRepo authTokenAutoRepo;
    @Resource
    MetricService metricService;
    @Resource
    CacheService cacheService;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public AppVO findApp(int appId) {
        return appAutoRepo.findById(appId)
                .map(v -> Try.of(() -> JsonUtils.transfrom(v, AppVO.class)).get())
                .orElse(null);
    }

    @Override
    public Page<AppVO> fetchApps(String nameKeyword, List<Integer> filterOwnerIds, int pageNo, int pageSize) {
        Page<AppDBVO> dbvoPage;
        if (StringUtils.hasText(nameKeyword)
                && !CollectionUtils.isEmpty(filterOwnerIds)) {
            dbvoPage = appAutoRepo.findByNameLikeAndOwnerIdIn("%" + nameKeyword + "%", filterOwnerIds, PageRequest.of(pageNo - 1, pageSize, Sort.by("updateTime").descending()));
        } else if (!StringUtils.hasText(nameKeyword)
                && CollectionUtils.isEmpty(filterOwnerIds)) {
            dbvoPage = appAutoRepo.findAll(PageRequest.of(pageNo - 1, pageSize, Sort.by("updateTime").descending()));
        } else if (!CollectionUtils.isEmpty(filterOwnerIds)) {
            dbvoPage = appAutoRepo.findByOwnerIdIn(filterOwnerIds, PageRequest.of(pageNo - 1, pageSize, Sort.by("updateTime").descending()));
        } else {
            dbvoPage = appAutoRepo.findByNameLike("%" + nameKeyword + "%"
                    , PageRequest.of(pageNo - 1, pageSize, Sort.by("updateTime").descending()));
        }
        return dbvoPage.map(v -> JsonUtils.transfrom(v, AppVO.class));
    }

    @Override
    public Page<AppVO> fetchApps(String nameKeyword, List<Integer> filterOwnerIds, List<Integer> appTypes, int pageNo, int pageSize) {
        Page<AppDBVO> dbvoPage;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AppDBVO> criteriaQuery = criteriaBuilder.createQuery(AppDBVO.class);
        Root<AppDBVO> root = criteriaQuery.from(AppDBVO.class);
        List<Predicate> predicateList = new ArrayList<>();
        if (StringUtils.hasText(nameKeyword)) {
            Predicate name = criteriaBuilder.like(root.get("name"), "%" + nameKeyword + "%");
            predicateList.add(name);
        }

        if (!CollectionUtils.isEmpty(appTypes)) {
            if (appTypes.size() == 1) {
                Predicate businessDomainEqual = criteriaBuilder.equal(root.get("appType"), appTypes.get(0));
                predicateList.add(businessDomainEqual);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("appType"));
                for (Integer id : appTypes) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }
        }
        if (!CollectionUtils.isEmpty(filterOwnerIds)) {
            Join<AppDBVO, UserDBVO> ownerJoin = root.join("owner", JoinType.LEFT);
            if (filterOwnerIds.size() == 1) {
                Predicate owner = criteriaBuilder.equal(ownerJoin.get("id"), filterOwnerIds.get(0));
                predicateList.add(owner);
            } else {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(ownerJoin.get("id"));
                for (Integer id : filterOwnerIds) {
                    in.value(id);//存入值
                }
                predicateList.add(in);
            }

        }

        List<Order> orderList = new ArrayList<>();
        orderList.add(criteriaBuilder.asc(root.get("updateTime")));
//        criteriaQuery.distinct(true);
        criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
        //总数
        List<AppDBVO> countList = entityManager.createQuery(criteriaQuery).getResultList();
        //分页
        criteriaQuery.orderBy(orderList);
        TypedQuery<AppDBVO> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageSize * (pageNo - 1));
        query.setMaxResults(pageSize);
        List<AppDBVO> resultList = query.getResultList();

        dbvoPage = new PageImpl<>(resultList, PageRequest.of(pageNo - 1, pageSize, Sort.by("updateTime").descending()), countList.size());
        return dbvoPage.map(v -> JsonUtils.transfrom(v, AppVO.class));
    }

    @Override
    public List<AppVO> fetchAppList(boolean admin) {
        List<AppVO> backList = new ArrayList<AppVO>();
        List<AppDBVO> list;
        if (admin) {
            list = appAutoRepo.findAll(Sort.by("name"));
        } else {
            UserSecurityDetails userDetail = AuthUtil.getUserDetail();
            if (userDetail == null) {
                return backList;
            }
            list = appAutoRepo.findByOwnerIdOrderByName(userDetail.getId());
        }
        if (CollectionUtils.isEmpty(list)) {
            return backList;
        } else {
            return list.stream().map(v -> JsonUtils.transfrom(v, AppVO.class)).collect(Collectors.toList());
        }

    }

    @Transactional
    @Override
    public void removeApp(int id) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        AppDBVO dbvo = appAutoRepo.findById(id).orElse(null);
        if (null != dbvo) {
            dbvo.setStatus(DataStatus.DISABLE);
            dbvo.setUpdateTime(new Date());
            dbvo.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
        }
        List<QuotaDBVO> quotas = quotaAutoRepo.findByAppId(id);
        quotas.forEach(v -> {
            v.setStatus(DataStatus.DISABLE);
            v.setUpdateTime(new Date());
            v.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
        });
        List<RelationOfAppAndMetricDBVO> relas = relationOfAppAndMetricAutoRepo.findByAppId(id);
        relas.forEach(v -> {
            v.setStatus(DataStatus.DISABLE);
            v.setUpdateTime(new Date());
            v.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
        });
        if (null != dbvo) {
            appAutoRepo.save(dbvo);
            log.info("removeApp: user[{} {}] removeApp[{} {}]", null == userDetail ? "" : userDetail.getName(),
                    null == userDetail ? 0 : userDetail.getId(), dbvo.getName(), dbvo.getId());
        }
        if (!quotas.isEmpty()) {
            quotaAutoRepo.saveAll(quotas);
            log.info("removeApp: remove quotas:{}", StringUtils.collectionToCommaDelimitedString(quotas.stream().map(QuotaDBVO::getId).collect(Collectors.toList())));
        }
        if (!relas.isEmpty()) {
            relationOfAppAndMetricAutoRepo.saveAll(relas);
            log.info("removeApp: remove relas:{}", StringUtils.collectionToCommaDelimitedString(relas.stream().map(RelationOfAppAndMetricDBVO::getId).collect(Collectors.toList())));
        }
    }

    @Transactional
    @Override
    public AuthTokenVO exchangeToken(int appId, AuthTokenVO token) {
        Date date = new Date();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        List<AuthTokenDBVO> list = new ArrayList<>();
        if (null != token) {
            AuthTokenDBVO oToken = JsonUtils.transfrom(token, AuthTokenDBVO.class);
            oToken.setUpdateTime(date);
            oToken.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
            oToken.setStatus(DataStatus.DISABLE);
            list.add(oToken);
        }
        AuthTokenDBVO build = AuthTokenDBVO.builder()
                .appId(appId)
                .status(DataStatus.ENABLE)
                .token(identifyPasswordEncoder.encode(String.format("%d:%d:%s", appId, null == token ? 0 : token.getId(), date)))
                .createUserId(null == userDetail ? 0 : userDetail.getId())
                .updateUserId(null == userDetail ? 0 : userDetail.getId())
                .createTime(date)
                .updateTime(date)
                .build();
        list.add(build);
        authTokenAutoRepo.saveAll(list);
        AppDBVO byId = appAutoRepo.getById(appId);
        byId.setToken(build);
        appAutoRepo.save(byId);
        return JsonUtils.transfrom(build, AuthTokenVO.class);
    }

    @Transactional
    @Override
    public AppVO modifyApp(int id, AppModifyModel model) {
        AppDBVO dbvo = appAutoRepo.findById(id).orElse(null);
        if (null != dbvo) {
            UserSecurityDetails userDetail = AuthUtil.getUserDetail();
            dbvo.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
            dbvo.setUpdateTime(new Date());
            dbvo.setName(model.getName());
            dbvo.setDescription(model.getDescription());
            dbvo.setAppType(AppType.findByCode(model.getAppType()));
            return JsonUtils.transfrom(appAutoRepo.saveAndFlush(dbvo), AppVO.class);
        }
        return null;
    }

    @Transactional
    @Override
    public AppVO createApp(AppModifyModel model) {
        Date date = new Date();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        //创建app，创建token
        AppDBVO dbvo = AppDBVO.builder()
                .name(model.getName())
                .token(AuthTokenDBVO.builder().id(0).build())
                .description(model.getDescription())
                // .metrics(Collections.emptyList())
                .owner(UserDBVO.builder().id(null == userDetail ? 0 : userDetail.getId()).build())
                .createTime(date)
                .updateTime(date)
                .createUserId(null == userDetail ? 0 : userDetail.getId())
                .updateUserId(null == userDetail ? 0 : userDetail.getId())
                .status(DataStatus.ENABLE)
                .type(1)//默认外部应用
                .appType(AppType.findByCode(model.getAppType()))
                .build();
        dbvo = appAutoRepo.saveAndFlush(dbvo);
        //创建token
        AuthTokenDBVO authDbvo = AuthTokenDBVO.builder()
                .appId(dbvo.getId())
                .token(identifyPasswordEncoder.encode(String.format("%d:%d:%s", dbvo.getId(), 0, date)))
                .createTime(date)
                .updateTime(date)
                .createUserId(null == userDetail ? 0 : userDetail.getId())
                .updateUserId(null == userDetail ? 0 : userDetail.getId())
                .status(DataStatus.ENABLE)
                .build();
        authTokenAutoRepo.saveAndFlush(authDbvo);
        dbvo.setToken(authDbvo);
        dbvo = appAutoRepo.saveAndFlush(dbvo);
        return appAutoRepo.findById(dbvo.getId())
                .map(v -> JsonUtils.transfrom(v, AppVO.class))
                .orElse(null);
    }

    @Override
    public AppVO findApp(String appName) {
        AppDBVO dbvo = appAutoRepo.findByName(appName);
        return null == dbvo ? null : JsonUtils.transfrom(dbvo, AppVO.class);
    }

    @Override
    public Page<MetricVO> findMetrics(int appId, String metricKeyword, int effective, int pageNo, int pageSize) {
        //effective经过上游，仅有0/1值
        if (0 == effective || 1 == effective) {
            Page<RelationOfAppAndMetricDBVO> relas;
            if (0 == effective) {
                relas = relationOfAppAndMetricAutoRepo.findIneffectiveRelasByAppIdAndMetricNameLike(appId,
                        StringUtils.hasText(metricKeyword) ? "%" + metricKeyword + "%" : "%",
                        PageRequest.of(pageNo - 1, pageSize, Sort.by("update_time").descending()));
                //失效指标的过期时间，在是解绑原因时显示为解绑更新时间
                relas.getContent().forEach(m -> {
                    if (Objects.equals(DataStatus.DISABLE, m.getStatus())) {
                        m.getMetric().setUpdateTime(m.getUpdateTime());
                    }
                });
            } else {
                relas = relationOfAppAndMetricAutoRepo.findEffectiveRelasByAppIdAndMetricNameLike(appId,
                        StringUtils.hasText(metricKeyword) ? "%" + metricKeyword + "%" : "%",
                        PageRequest.of(pageNo - 1, pageSize, Sort.by("update_time").descending()));
            }
            return relas.map(r -> JsonUtils.transfrom(r.getMetric(), MetricVO.class));
        } else {
            return Page.empty(PageRequest.of(0, 0));
        }
    }

    @Override
    public String fetchToken(int id) {
        AppVO app = findApp(id);
        return null == app || null == app.getToken() ? "" : app.getToken().getToken();
    }

    @Override
    public List<DropDownListModel> findUserApps(boolean admin) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        if (userDetail == null) {
            return Collections.emptyList();
        }
        List<Object[]> externalApps = admin ? appAutoRepo.findAllApps() : appAutoRepo.findUserApps(userDetail.getId());
        List<CommonDownListVO> mapping = JPAModelMapUtils.mapping(externalApps, CommonDownListVO.class);
        return mapping.stream().map(m -> JsonUtils.transfrom(m, DropDownListModel.class)).collect(Collectors.toList());
    }

    @Override
    public List<DropDownListModel> getExternalAppsDownListFromCache() {
        return null;
//        return cacheService.getAppsDownList().stream().map(m -> JsonUtils.transfrom(m, DropDownListModel.class)).collect(Collectors.toList());
    }

    @Override
    public void unbindAppAndMetric(int appId, int metricId) {
        Date date = new Date();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(appAutoRepo.getById(appId), "应用不存在");
        Assert.notNull(metricService.findMetricById(metricId), "指标不存在");
        List<RelationOfAppAndMetricDBVO> allByAppIdAndMetric = relationOfAppAndMetricAutoRepo.findAllByAppIdAndMetricId(appId, metricId);
        for (RelationOfAppAndMetricDBVO relationOfAppAndMetricDBVO : allByAppIdAndMetric) {
            relationOfAppAndMetricDBVO.setStatus(DataStatus.DISABLE);
            relationOfAppAndMetricDBVO.setUpdateTime(date);
            relationOfAppAndMetricDBVO.setUpdateUserId(null == userDetail ? 0 : userDetail.getId());
        }
        relationOfAppAndMetricAutoRepo.saveAll(allByAppIdAndMetric);
        log.info("用户{}解绑appid:{}与metricId:{}的绑定关系", userDetail.getName(), appId, metricId);
    }
}
