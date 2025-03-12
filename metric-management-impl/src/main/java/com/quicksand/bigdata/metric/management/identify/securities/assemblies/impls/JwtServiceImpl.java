package com.quicksand.bigdata.metric.management.identify.securities.assemblies.impls;

import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyUserDetailSercvice;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.JwtService;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.MetricsAuthenticationSuccessFilter;
import com.quicksand.bigdata.metric.management.identify.securities.vos.IdentifyUserDetails;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.UserModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.jwt.JwtToken;
import com.quicksand.bigdata.vars.security.props.SecurityAlgoritmProps;
import com.quicksand.bigdata.vars.security.vos.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * JWTServiceImpl
 *
 * @author page
 * @date 2020/8/27 15:46
 */
@Slf4j
@Service
public class JwtServiceImpl
        implements JwtService {


    static final String KEY_DEFAULT = "default";

    @Value("${spring.application.name:BDT-METRIC-MANAGEMENT}")
    String applicationName;
    /**
     * 签发jwt最长有效期
     * （默认 12H）
     */
    @Value("${bdt.management.jwt.expired:43200000}")
    long jwtExpired;
    @Resource
    SecurityAlgoritmProps securityAlgoritmProps;
    @Resource
    UserService userService;

    @Override
    public JwtToken<UserInfo> signJwtToken(@NotNull UserVO userVO) {
        JwtToken<UserInfo> jwtToken = new JwtToken<>();
        try {
            //签发jwtToken
            IdentifyUserDetails identifyUserDetails = IdentifyUserDetailSercvice.cover2UserDetails(userVO);
            UserInfo payload = UserModelAdapter.cover2UserINfo(userVO);
            payload.setExpired(jwtExpired);
            jwtToken.setHeader(MetricsAuthenticationSuccessFilter.genernateHeader(
                    securityAlgoritmProps.findAlgorithmSetting(KEY_DEFAULT, JwtToken.SIGNATURE_ALG_HS256_HEADER, KEY_DEFAULT)
                    , identifyUserDetails, applicationName)
            );
            jwtToken.setPayload(payload);
            jwtToken.setSignKeyExchanger(securityAlgoritmProps);
            String tokenStr = jwtToken.toTokenString();
            if (StringUtils.hasText(tokenStr)) {
                identifyUserDetails.setValidation(true);
                identifyUserDetails.setConsensus(true);
                identifyUserDetails.setTokenKey(tokenStr);
            }
        } catch (Exception e) {
            log.warn("JWTService sign jwt token error! userName:{}`", userVO.getName(), e);
        }
        return jwtToken;
    }

    @Override
    public JwtToken<UserInfo> signJwtToken(@NotNull Authentication authentication) {
        if (null == authentication) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof IdentifyUserDetails) {
            IdentifyUserDetails identifyUserDetails = (IdentifyUserDetails) principal;
            if (!identifyUserDetails.isLogout()
                    && StringUtils.isEmpty(identifyUserDetails.getTokenKey())) {
                return signJwtToken(userService.queryUser(identifyUserDetails.getId()));
            }
        }
        return null;
    }

}
