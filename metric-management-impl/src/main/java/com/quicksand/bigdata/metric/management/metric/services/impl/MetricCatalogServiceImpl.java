package com.quicksand.bigdata.metric.management.metric.services.impl;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.DomainType;
import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricCatalogDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricCatalogDataManager;
import com.quicksand.bigdata.metric.management.metric.models.MetricCatalogModifyModel;
import com.quicksand.bigdata.metric.management.metric.services.MetricCatalogService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricCatalogGroupVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricCatalogVO;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MetricCatalogServiceImpl
 *
 * @author page
 * @date 2022-07-31
 */
@Slf4j
@Service
public class MetricCatalogServiceImpl
        implements MetricCatalogService {

    @Resource
    MetricCatalogDataManager metricCatalogDataManager;
    @Resource
    OperationLogService operationLogService;

    @Override
    public List<MetricCatalogVO> queryCatalogs(String parentCode, int mode, int type) {
        List<MetricCatalogVO> catalogs = metricCatalogDataManager.findAllCatalogs()
                .stream()
                .filter(v -> 0 > type || Objects.equals(DomainType.cover(type), v.getType()))
                .map(v -> JsonUtils.transfrom(v, MetricCatalogVO.class))
                .collect(Collectors.toList());
        modeDealCount(catalogs);
        return modeDeal(parentCode, mode, catalogs);
    }

    @Override
    public List<MetricCatalogVO> queryCatalogs(int mode, int type) {
        return queryCatalogs("000", mode, type);
    }

    @Override
    public MetricCatalogVO findCatalog(int id) {
        MetricCatalogDBVO metricCatalogVO = metricCatalogDataManager.findById(id);
        return null == metricCatalogVO ? null : JsonUtils.transfrom(metricCatalogVO, MetricCatalogVO.class);
    }

    @Override
    public MetricCatalogVO upsertCatalog(MetricCatalogModifyModel model) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        boolean isCreate = null == model.getId() || 0 >= model.getId();
        Date operationTime = new Date();
        MetricCatalogDBVO parentCatalog = metricCatalogDataManager.findById(model.getParent());
        List<MetricCatalogDBVO> updates = new ArrayList<>();
        MetricCatalogDBVO metricCatalog = null;
        if (isCreate) {
            metricCatalog = MetricCatalogDBVO.builder()
                    .parent(parentCatalog)
                    .code(getNewCode(parentCatalog))
                    .businessCode(model.getBusinessCode())
                    .name(model.getName())
                    .type(0 == model.getParent() ? DomainType.Topic : DomainType.Business)
                    .status(DataStatus.ENABLE)
                    .updateTime(operationTime)
                    .createTime(operationTime)
                    .createUserId(null != userDetail ? userDetail.getId() : 0)
                    .updateUserId(null != userDetail ? userDetail.getId() : 0)
                    .build();
            updates.add(metricCatalog);
            Try.run(() ->
                    operationLogService.log(OperationLogDBVO.builder()
                            .operationTime(operationTime)
                            .address("未知")
                            .ip("0.0.0.0")
                            .userId(null == userDetail ? 0 : userDetail.getId())
                            .type(OperationLogType.DATACATLOG_MODIFY)
                            .detail(String.format("创建数据目录，参数：%s", JsonUtils.toJsonString(model)))
                            .build())
            ).onFailure(ex -> log.warn("log error! ", ex));
        } else {
            MetricCatalogDBVO dbvo = metricCatalogDataManager.findById(model.getId());
            metricCatalog = dbvo;
            // 正常数据不存在parent为null的情况
            //noinspection AlibabaAvoidComplexCondition
            if ((null != dbvo.getParent() && !Objects.equals(model.getParent(), dbvo.getParent().getId()))
                    || (null == dbvo.getParent() && 0 < model.getParent())) {
                //修改了父节点，需要重新编号
                dbvo.setCode(getNewCode(parentCatalog));
                dbvo.setParent(parentCatalog);
                //修改当前目录的子节点
                List<MetricCatalogDBVO> curSubCatalogs = metricCatalogDataManager.findByParentId(model.getId());
                int index = 1;
                for (MetricCatalogDBVO curSubCatalog : curSubCatalogs) {
                    curSubCatalog.setCode(String.format("%s%s", dbvo.getCode(), String.valueOf(1000 + index).substring(1, 4)));
                    curSubCatalog.setUpdateTime(operationTime);
                    curSubCatalog.setUpdateUserId(null != userDetail ? userDetail.getId() : 0);
                    updates.add(curSubCatalog);
                }
            }
            dbvo.setName(model.getName());
            dbvo.setType(0 == model.getParent() ? DomainType.Topic : DomainType.Business);
            dbvo.setUpdateTime(operationTime);
            dbvo.setUpdateUserId(null != userDetail ? userDetail.getId() : 0);
            updates.add(dbvo);
            Try.run(() ->
                    operationLogService.log(OperationLogDBVO.builder()
                            .operationTime(operationTime)
                            .address("未知")
                            .ip("0.0.0.0")
                            .userId(null == userDetail ? 0 : userDetail.getId())
                            .type(OperationLogType.DATACATLOG_MODIFY)
                            .detail(String.format("修改数据目录，参数：%s", JsonUtils.toJsonString(model)))
                            .build())
            ).onFailure(ex -> log.warn("log error! ", ex));
        }
        metricCatalogDataManager.saveCatalogs(updates);
        MetricCatalogVO transfrom = JsonUtils.transfrom(metricCatalog, MetricCatalogVO.class);
        transfrom.setParent(null);
        transfrom.setChildren(null);
        return transfrom;
    }

    String getNewCode(MetricCatalogDBVO parentCatalog) {
        List<MetricCatalogDBVO> broCatalogs = metricCatalogDataManager.findByParentCode(parentCatalog.getCode());
        int maxCode = 1;
        if (!broCatalogs.isEmpty()) {
            for (MetricCatalogDBVO broCatalog : broCatalogs) {
                int index = Integer.parseInt(broCatalog.getCode().replaceFirst(parentCatalog.getCode(), ""));
                maxCode = Math.max(index, maxCode);
            }
            maxCode += 1;
        }
        return String.format("%s%s", "000".equals(parentCatalog.getCode()) ? "" : parentCatalog.getCode(), String.valueOf((1000 + maxCode)).substring(1, 4));
    }

    @Override
    public void saveMetricCatalog(MetricCatalogVO catalogVO) {
        List<MetricCatalogDBVO> updates = new ArrayList<>();
        MetricCatalogDBVO dbvo = JsonUtils.transfrom(catalogVO, MetricCatalogDBVO.class);
        updates.add(dbvo);
        List<MetricCatalogDBVO> subCatalogs = metricCatalogDataManager.findByParentId(dbvo.getId());
        if (!CollectionUtils.isEmpty(subCatalogs)) {
            subCatalogs.forEach(v -> v.setStatus(DataStatus.DISABLE));
            updates.addAll(subCatalogs);
        }
        metricCatalogDataManager.saveCatalogs(updates);
    }

    @Override
    public MetricCatalogVO findCatalog(String name) {
        MetricCatalogDBVO byName = metricCatalogDataManager.findByName(name);
        return null == byName ? null : JsonUtils.transfrom(byName, MetricCatalogVO.class);
    }

    @Override
    public MetricCatalogVO findCatalogByBusinessCode(String businessCode) {
        MetricCatalogDBVO byName = metricCatalogDataManager.findByBusinessCode(businessCode);
        return null == byName ? null : JsonUtils.transfrom(byName, MetricCatalogVO.class);
    }

    private List<MetricCatalogVO> modeDeal(String parentCode, int mode, List<MetricCatalogVO> catalogs) {
        switch (mode) {
            case 0:
                catalogs = catalogs.stream()
                        .filter(v -> null != v.getParent())
                        .filter(v -> parentCode.equals(v.getParent().getCode()) || Objects.equals(parentCode, "000"))
                        .peek(v -> v.setParent(null))
                        .collect(Collectors.toList());
                break;
            case 2:
                catalogs = catalogs.stream()
                        .filter(v -> null != v.getParent())
                        .filter(v -> parentCode.equals(v.getParent().getCode()))
                        .collect(Collectors.toList());
                break;
            case 1:
                Map<Integer, MetricCatalogVO> id2CatalogMap = catalogs.stream()
                        .collect(Collectors.toMap(MetricCatalogVO::getId, v -> v));

                List<MetricCatalogVO> ret = catalogs.stream()
                        .filter(v -> null != v.getParent())
                        .filter(v -> parentCode.equals(v.getParent().getCode()))
                        .peek(v -> v.setParent(null))
                        .collect(Collectors.toList());

                for (MetricCatalogVO catalog : catalogs) {
                    if (null != catalog.getParent()) {
                        MetricCatalogVO parentVo = id2CatalogMap.get(catalog.getParent().getId());
                        if (null != parentVo) {
                            List<MetricCatalogVO> children = parentVo.getChildren();
                            if (null == children) {
                                children = new ArrayList<>();
                                catalog.setParent(null);
                            }
                            children.add(catalog);
                            parentVo.setChildren(children);
                        }
                    }
                    catalog.setParent(null);
                }
                catalogs = ret;
            default:
                break;
        }
        return catalogs;
    }

    private void modeDealCount(List<MetricCatalogVO> catalogs) {
        List<MetricCatalogGroupVO> metricCount = metricCatalogDataManager.findMetricCount();
        Map<Integer, Integer> catalogCountMap = metricCount.stream()
                .collect(Collectors.toMap(MetricCatalogGroupVO::getCatalogId, MetricCatalogGroupVO::getMetricCount));
        for (MetricCatalogVO catalog : catalogs) {
            Integer count = catalogCountMap.get(catalog.getId());
            catalog.setMetricCount(count == null ? 0 : count);
        }
    }

    @Override
    public List<MetricCatalogVO> queryBusinessCatalogsByParentIds(List<Integer> parentIds) {
        List<MetricCatalogDBVO> metricCatalogDBVOS = CollectionUtils.isEmpty(parentIds) ? metricCatalogDataManager.findAllByType(DomainType.Business) : metricCatalogDataManager.findAllByTypeAndParentIds(DomainType.Business, parentIds);
        return metricCatalogDBVOS.stream().map(v -> JsonUtils.transfrom(v, MetricCatalogVO.class)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void removeCatalogs(Collection<Integer> ids) {
        List<MetricCatalogDBVO> catalogs = metricCatalogDataManager.findCatalogs(ids);
        if(!CollectionUtils.isEmpty(catalogs)){
            for (MetricCatalogDBVO catalog : catalogs) {
                catalog.setStatus(DataStatus.DISABLE);
                catalog.setUpdateTime(new Date());
                catalog.setBusinessCode("RE:"+catalog.getBusinessCode());
                catalog.setCode("-"+catalog.getCode());
                catalog.setUpdateUserId(Objects.requireNonNull(AuthUtil.getUserDetail()).getId());
            }
            metricCatalogDataManager.saveCatalogs(catalogs);
        }
    }

}
