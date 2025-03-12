package com.quicksand.bigdata.metric.management.metric.services.impl;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricLabelDBVO;
import com.quicksand.bigdata.metric.management.metric.dbvos.RelationOfLabelAndMetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.MetricLabelBindModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricLabelModifyModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import com.quicksand.bigdata.metric.management.metric.repos.MetricAutoRepo;
import com.quicksand.bigdata.metric.management.metric.repos.MetricLabelAutoRepo;
import com.quicksand.bigdata.metric.management.metric.repos.RelationOfLabelAndMetricAutoRepo;
import com.quicksand.bigdata.metric.management.metric.services.MetricLabelService;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MetricLabelServiceImpl
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/24 15:32
 * @description
 */
@Slf4j
@Service
public class MetricLabelServiceImpl implements MetricLabelService {

    @Resource
    MetricLabelAutoRepo metricLabelAutoRepo;

    @Resource
    RelationOfLabelAndMetricAutoRepo relationOfLabelAndMetricAutoRepo;


    @Resource
    MetricAutoRepo metricAutoRepo;

    @Resource
    OperationLogService operationLogService;

    @Resource
    MetricDataManager metricDataManager;

    /**
     * @param metricId 指标id
     * @return List<MetricLabelModifyModel>
     * @author zhao_xin
     * @description 获取用户单个指标下的已有标签
     **/
    @Override
    public List<MetricLabelModifyModel> findUserMetricLabelsBind(Integer metricId) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "未获取到当前用户信息");
        List<MetricLabelDBVO> allByUserAndMetric = metricLabelAutoRepo.findAllByUserIdAndMetric(userDetail.getId(), metricId);
        return allByUserAndMetric.stream().map(m -> JsonUtils.transfrom(m, MetricLabelModifyModel.class)).collect(Collectors.toList());
    }

    /**
     * @param metricId 指标id
     * @return List<MetricLabelModifyModel>
     * @author zhao_xin
     * @description 获取用户对某个指标下的可添加标签
     **/
    @Override
    public List<MetricLabelModifyModel> findUserMetricLabelsForAdd(Integer metricId) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "未获取到当前用户信息");

        //用户全部标签
        List<MetricLabelDBVO> userAllLabels = metricLabelAutoRepo.findAllByUserIdOrderByName(userDetail.getId());
        //已有标签
        List<MetricLabelDBVO> removeList = metricLabelAutoRepo.findAllByUserIdAndMetric(userDetail.getId(), metricId);
        //过滤
        userAllLabels.removeAll(removeList);
        return userAllLabels.stream().map(m -> JsonUtils.transfrom(m, MetricLabelModifyModel.class)).collect(Collectors.toList());
    }

    @Override
    public List<MetricLabelModifyModel> findUserAllLabels() {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "未获取到当前用户信息");
        //用户全部标签
        List<MetricLabelDBVO> userAllLabels = metricLabelAutoRepo.findAllByUserIdOrderByName(userDetail.getId());
        return userAllLabels.stream().map(m -> JsonUtils.transfrom(m, MetricLabelModifyModel.class)).collect(Collectors.toList());
    }

    @Override
    public PageImpl<MetricOverviewModel> findAllMetricsByLabelId(Integer labelId, Pageable pageable) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "未获取到当前用户信息");
        MetricLabelDBVO byUserAndId = metricLabelAutoRepo.findByUserIdAndId(userDetail.getId(), labelId);
        Assert.notNull(byUserAndId, "用户下无此标签");

        org.springframework.data.domain.Page<MetricDBVO> metricDBVOS = metricAutoRepo.findAllByLabelId(labelId, pageable);
        PageImpl<MetricOverviewModel> page = PageImpl.buildEmptyPage(pageable.getPageNumber(), pageable.getPageSize());
        page.setTotal(metricDBVOS.getTotalElements());
        page.setTotalPage(metricDBVOS.getTotalPages());
        page.setItems(null);
        List<MetricOverviewModel> collect = metricDBVOS.getContent().stream().map(m -> JsonUtils.transfrom(m, MetricOverviewModel.class)).collect(Collectors.toList());
        page.setItems(collect);
        return page;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void markDeleteByLabelId(Integer labelId) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "未获取到当前用户信息");
        MetricLabelDBVO label = metricLabelAutoRepo.findByUserIdAndId(userDetail.getId(), labelId);
        Assert.notNull(label, "用户下无此标签");
        label.setStatus(DataStatus.DISABLE);
        label.setUpdateTime(new Date());
        metricLabelAutoRepo.save(label);
        List<RelationOfLabelAndMetricDBVO> allByLabel = relationOfLabelAndMetricAutoRepo.findAllByLabelId(labelId);
        for (RelationOfLabelAndMetricDBVO relationOfLabelAndMetricDBVO : allByLabel) {
            relationOfLabelAndMetricDBVO.setStatus(DataStatus.DISABLE);
            relationOfLabelAndMetricDBVO.setUpdateTime(new Date());
        }
        relationOfLabelAndMetricAutoRepo.saveAll(allByLabel);
        operationLogService.log(OperationLogType.LABEL_DELETE, String.format("删除用户标签：用户[%s %d]删除了标签[%s %d]",
                userDetail.getUsername(), userDetail.getId(), label.getName(), label.getId()));
        return null;


    }

    @Override
    public MetricLabelModifyModel upsertMetricLabel(MetricLabelModifyModel model) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "未获取到当前用户信息");
        boolean isAdd = model.getId() == null;
        if (isAdd) {
            List<MetricLabelDBVO> exitsNames = metricLabelAutoRepo.findAllByUserIdAndNameIn(userDetail.getId(), Collections.singletonList(model.getName()));
            Assert.isTrue(exitsNames.size() == 0, "用户已存在此标签名称");
            MetricLabelDBVO labelDBVO = MetricLabelDBVO.builder().name(model.getName()).status(DataStatus.ENABLE)
                    .createTime(new Date())
                    .user(UserDBVO.builder().id(userDetail.getId()).build())
                    .updateTime(new Date()).build();
            MetricLabelDBVO save = metricLabelAutoRepo.save(labelDBVO);
            return JsonUtils.transfrom(save, MetricLabelModifyModel.class);
        } else {
            MetricLabelDBVO labelDBVO = metricLabelAutoRepo.findByUserIdAndId(userDetail.getId(), model.getId());
            Assert.notNull(labelDBVO, "用户下无此标签");
            labelDBVO.setName(model.getName());
            labelDBVO.setUpdateTime(new Date());
            metricLabelAutoRepo.save(labelDBVO);
            return model;
        }
    }

    @Override
    public Void markRelationDeleteByLabelIdAndMetricId(Integer metricId, Integer labelId) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "未获取到当前用户信息");
        MetricLabelDBVO byUserAndId = metricLabelAutoRepo.findByUserIdAndId(userDetail.getId(), labelId);
        Assert.notNull(byUserAndId, "用户下无此标签");
        List<RelationOfLabelAndMetricDBVO> allByLabelAndMetric = relationOfLabelAndMetricAutoRepo.findAllByLabelIdAndMetricId(labelId, metricId);
        for (RelationOfLabelAndMetricDBVO relationOfLabelAndMetricDBVO : allByLabelAndMetric) {
            relationOfLabelAndMetricDBVO.setStatus(DataStatus.DISABLE);
            relationOfLabelAndMetricDBVO.setUpdateTime(new Date());
        }
        relationOfLabelAndMetricAutoRepo.saveAll(allByLabelAndMetric);
        operationLogService.log(OperationLogType.LABEL_METRIC_REL_DELETE, String.format("解除绑定关系：用户[%s %d]删除了标签%d与指标%d的绑定关系",
                userDetail.getUsername(), userDetail.getId(), labelId, metricId));
        return null;

    }

    @Override
    public List<RelationOfLabelAndMetricDBVO> bindMetricAndLabel(MetricLabelBindModel model) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        Assert.notNull(userDetail, "未获取到当前用户信息");
        //检查指标是否存在
        MetricDBVO metricDBVO = metricDataManager.findByMetricId(model.getMetricId());
        Assert.notNull(metricDBVO, "指标不存在或已删除");

        List<MetricLabelDBVO> allByUserIdAndNames = metricLabelAutoRepo.findAllByUserIdAndNameIn(userDetail.getId(), model.getLabelNames());
        //用户没有的标签，需要先添加
        List<String> insertLabelNames = model.getLabelNames();
        if (!CollectionUtils.isEmpty(allByUserIdAndNames)) {
            List<String> existLabelList = allByUserIdAndNames.stream().map(v -> v.getName()).collect(Collectors.toList());
            insertLabelNames.removeAll(existLabelList);
        }
        if (!CollectionUtils.isEmpty(insertLabelNames)) {
            List<MetricLabelDBVO> insertLabelList = new ArrayList<>();
            for (String insertLabelName : insertLabelNames) {
                MetricLabelDBVO labelDBVO = MetricLabelDBVO.builder().name(insertLabelName).status(DataStatus.ENABLE)
                        .createTime(new Date())
                        .user(UserDBVO.builder().id(userDetail.getId()).build())
                        .updateTime(new Date()).build();
                insertLabelList.add(labelDBVO);
            }

            List<MetricLabelDBVO> newLabelList = metricLabelAutoRepo.saveAll(insertLabelList);
            allByUserIdAndNames.addAll(newLabelList);
        }

        List<RelationOfLabelAndMetricDBVO> insertRelLabelList = new ArrayList<>();
        for (MetricLabelDBVO label : allByUserIdAndNames) {
            List<RelationOfLabelAndMetricDBVO> allByLabelIdAndMetricId = relationOfLabelAndMetricAutoRepo.findAllByLabelIdAndMetricId(label.getId(), model.getMetricId());
            if (CollectionUtils.isEmpty(allByLabelIdAndMetricId)) {
                RelationOfLabelAndMetricDBVO newModel = RelationOfLabelAndMetricDBVO.builder()
                        .label(label)
                        .metric(metricDBVO)
                        .status(DataStatus.ENABLE)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                insertRelLabelList.add(newModel);
            }
        }
        operationLogService.log(OperationLogType.LABEL_METRIC_REL_ADD, String.format("创建绑定关系：用户[%s %d]添加了标签%s与指标%s的绑定关系",
                userDetail.getUsername(), userDetail.getId(), model.getLabelNames(), model.getMetricId()));
        return relationOfLabelAndMetricAutoRepo.saveAll(insertRelLabelList);
    }
}
