package com.quicksand.bigdata.metric.management.identify.securities.assemblies;

import org.springframework.security.core.Authentication;

/**
 * MetricsAuthorizor
 * (Metrics授权器)
 *
 * @author page
 * @date 2020/8/25 13:05
 */
public interface MetricsAuthorizor {

    /**
     * 是否自己的数据
     *
     * @param userId         当前的用户Id
     * @param authentication 当前会话的授权对象
     * @return true/false
     */
    boolean isMe(Integer userId, Authentication authentication);

}
