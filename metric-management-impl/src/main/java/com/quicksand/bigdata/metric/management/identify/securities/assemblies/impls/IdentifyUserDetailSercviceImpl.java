package com.quicksand.bigdata.metric.management.identify.securities.assemblies.impls;

import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyUserDetailSercvice;
import com.quicksand.bigdata.metric.management.identify.securities.vos.IdentifyUserDetails;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * IdentifyUserDetailSercviceImpl
 *
 * @author page
 * @date 2020/8/24 11:19
 */
@Component
public class IdentifyUserDetailSercviceImpl
        implements IdentifyUserDetailSercvice {

    private final static String TEMPLATE_TIPS_USER_NAME_NOTFOUND = "name of '%s' not found !";
    @Resource
    UserService userService;

    @Override
    public IdentifyUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserVO userVO = userService.findByName(userName);
        if (null == userVO) {
            throw new UsernameNotFoundException(String.format(TEMPLATE_TIPS_USER_NAME_NOTFOUND, userName));
        }
        return IdentifyUserDetailSercvice.cover2UserDetails(userVO);
    }

}
