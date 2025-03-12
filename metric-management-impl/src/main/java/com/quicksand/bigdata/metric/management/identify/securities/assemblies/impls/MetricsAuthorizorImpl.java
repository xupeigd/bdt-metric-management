package com.quicksand.bigdata.metric.management.identify.securities.assemblies.impls;

import com.quicksand.bigdata.metric.management.identify.securities.assemblies.MetricsAuthorizor;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * MetricsAuthorizorImpl
 *
 * @author page
 * @date 2020/8/25 13:05
 */
@Component("metricsAuthorizor")
public class MetricsAuthorizorImpl
        implements MetricsAuthorizor {

    @Override
    public boolean isMe(Integer userId, Authentication authentication) {
        if (null != userId
                && 0 < userId
                && null != authentication
                && null != authentication.getPrincipal()
                && authentication.getPrincipal() instanceof UserSecurityDetails) {
            UserSecurityDetails usd = (UserSecurityDetails) authentication.getPrincipal();
            return usd.isValidation() && usd.isConsensus() && usd.getId() == userId;
        }
        return false;
    }

}