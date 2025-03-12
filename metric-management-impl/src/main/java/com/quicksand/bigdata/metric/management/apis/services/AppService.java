package com.quicksand.bigdata.metric.management.apis.services;

import com.quicksand.bigdata.metric.management.apis.models.AppModifyModel;
import com.quicksand.bigdata.metric.management.apis.vos.AppVO;
import com.quicksand.bigdata.metric.management.apis.vos.AuthTokenVO;
import com.quicksand.bigdata.metric.management.metric.models.DropDownListModel;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * AppService
 *
 * @author page
 * @date 2022/9/28
 */
public interface AppService {

    /**
     * 根据appId查询app数据
     *
     * @param appId 应用主键
     * @return insatnce of AppVO
     */
    AppVO findApp(int appId);

    /**
     * @param nameKeyword    名称keyword（模糊）
     * @param filterOwnerIds 选定的
     * @param pageNo         页码（基于1）
     * @param pageSize       页容量
     * @return Page of AppVO
     */
    Page<AppVO> fetchApps(String nameKeyword, List<Integer> filterOwnerIds, int pageNo, int pageSize);

    /**
     * @param nameKeyword    名称keyword（模糊）
     * @param filterOwnerIds 选定的
     * @param appTypes       应用类型
     * @param pageNo         页码（基于1）
     * @param pageSize       页容量
     * @return Page of AppVO
     */
    Page<AppVO> fetchApps(String nameKeyword, List<Integer> filterOwnerIds, List<Integer> appTypes, int pageNo, int pageSize);


    /**
     * 获取所有applist列表
     *
     * @param admin
     * @return List of AppVO
     */
    List<AppVO> fetchAppList(boolean admin);

    /**
     * 按id删除app
     *
     * @param id appId
     */
    void removeApp(int id);

    /**
     * 更换authToken
     *
     * @param appId
     * @param token token
     * @return instance of AuthTokenVO
     */
    AuthTokenVO exchangeToken(int appId, AuthTokenVO token);

    /**
     * 修改应用信息
     *
     * @param model 修改参数
     * @return 新的appVO
     */
    AppVO modifyApp(int id, AppModifyModel model);

    /**
     * 创建应用
     *
     * @param model 创建model
     * @return appVO
     */
    AppVO createApp(AppModifyModel model);

    /**
     * 根据应用名称检索app
     *
     * @param appName 应用名称
     * @return instance of AppVO / null
     */
    AppVO findApp(String appName);

    /**
     * 根据应用id查找生效/失效的指标
     *
     * @param appId         应用Id
     * @param metricKeyword 指标名称keyword
     * @param effective     是否有效 1有效 0失效
     * @param pageNo        页码
     * @param pageSize      页大小
     * @return page of MetricVO
     */
    Page<MetricVO> findMetrics(int appId, String metricKeyword, int effective, int pageNo, int pageSize);

    /**
     * 获取有效外部app信息，用于下拉列表
     *
     * @return List<CommonDownListVO>
     */
    List<DropDownListModel> findUserApps(boolean admin);

    /**
     * @return List<DropDownListModel>
     * @author zhao_xin
     * @description 获取指标列表用于下拉缓存
     **/
    List<DropDownListModel> getExternalAppsDownListFromCache();

    /**
     * @return List<DropDownListModel>
     * @author zhao_xin
     * @description 逻辑删除应用与指标的绑定关系
     **/
    void unbindAppAndMetric(int appId, int metricId);

}
