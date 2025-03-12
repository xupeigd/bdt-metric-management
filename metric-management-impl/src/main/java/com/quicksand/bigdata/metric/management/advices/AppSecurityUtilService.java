package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.Params;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * AppSecurityUtilService
 *
 * @author page
 * @date 2022/11/7
 */
public interface AppSecurityUtilService {

    /**
     * 校验参数
     *
     * @param params 参数数组
     * @return true/false
     */
    boolean validationRequestSignValue(Params... params);

    /**
     * 一般性的接口鉴权
     *
     * @param authentication 授权信息
     * @return true/false
     */
    boolean isApp(Authentication authentication);

    /**
     * App级别的权限
     *
     * @param authentication 授权信息
     * @return true/false
     */
    boolean hasSeniorAuthority(Authentication authentication);

    /**
     * 是否拥有当前指标的权限
     *
     * @param authentication 授权信息
     * @param metricId       指标Id
     * @return true/false
     */
    boolean hasMetric(Authentication authentication, int metricId);

    /**
     * 是否拥有指标的权限
     *
     * @param authentication 授权信息
     * @param metricIds      指标Id
     * @return true/false
     */
    boolean hasMetrics(Authentication authentication, List<Integer> metricIds);

    /**
     * 是否拥有当前数据集的权限
     *
     * @param authentication 授权信息
     * @param datasetId      数据集Id
     * @return true/false
     */
    boolean hasDataset(Authentication authentication, int datasetId);

    /**
     * 是否拥有集群的权限
     *
     * @param authentication 授权信息
     * @param clusterId      集群Id
     * @return true/false
     */
    boolean hasCluster(Authentication authentication, int clusterId);

}
