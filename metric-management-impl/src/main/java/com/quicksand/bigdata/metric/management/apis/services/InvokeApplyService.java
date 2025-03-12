package com.quicksand.bigdata.metric.management.apis.services;

import com.quicksand.bigdata.metric.management.apis.models.InvokeApplyModel;
import com.quicksand.bigdata.metric.management.apis.vos.InvokeApplyRecordVO;
import com.quicksand.bigdata.metric.management.apis.vos.RelationOfAppAndMetricVO;
import com.quicksand.bigdata.metric.management.consts.ApproveState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.SQLDataException;
import java.util.List;

/**
 * InvokeSupplyService
 *
 * @author page
 * @date 2022/10/12
 */
public interface InvokeApplyService {

    /**
     * 查找调用申请记录
     *
     * @param id 申请主键
     * @return instance of InvokeApplyRecordVO / null
     */
    InvokeApplyRecordVO findApplyRecord(int id);

    /**
     * 审批动作
     *
     * @param id             id
     * @param state          同意/拒绝
     * @param approveComment 审批意见
     * @return 记录vo
     */
    InvokeApplyRecordVO approve(int id, ApproveState state, String approveComment) throws SQLDataException;

//    /**
//     * 创建调用申请
//     *
//     * @param appId       appId
//     * @param metricIds   指标Ids
//     * @param description 申请原因
//     * @return 记录vo
//     */
//    List<InvokeApplyRecordVO> createApply(int appId, List<Integer> metricIds, String description);

    /**
     * 创建调用申请
     *
     * @param model InvokeApplyModel
     * @return 记录vo
     */
    List<InvokeApplyRecordVO> createApply(InvokeApplyModel model);

    /**
     * 查询应用的申请记录
     *
     * @param appIds        应用Id
     * @param filterUserIds 申请用户Id
     * @param fiterStates   筛选的审核状态
     * @param metricKeyword 指标名称keyword 模糊匹配
     * @param pageNo        页码，基于1
     * @param pageSize      页容量
     * @return page of InvokeApplyRecordVO
     */
    Page<InvokeApplyRecordVO> fetchApplys(List<Integer> appIds, List<Integer> filterUserIds, List<ApproveState> fiterStates, String metricKeyword, Integer pageNo, Integer pageSize);

    /**
     * 查询待审批列表
     *
     * @param appIds        应用Id
     * @param filterUserIds 申请用户Id
     * @param fiterStates   筛选的审核状态
     * @param metricKeyword 指标名称keyword 模糊匹配
     * @return page of InvokeApplyRecordVO
     */
    Page<InvokeApplyRecordVO> fetchApproveLists(List<Integer> appIds, List<Integer> filterUserIds, List<Integer> fiterStates, String metricKeyword, Pageable pageable);

    /**
     * 根据appId查找所有可用的调用关联
     *
     * @param appId 应用id
     * @return list of RelationOfAppAndMetricVO
     */
    List<RelationOfAppAndMetricVO> findAllEffectiveRelasByAppId(int appId);

    /**
     * 查找所有生效的调用关系
     *
     * @return list of RelationOfAppAndMetricVO
     */
    List<RelationOfAppAndMetricVO> findAllEffectiveRelas();

}
