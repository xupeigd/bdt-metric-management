package com.quicksand.bigdata.metric.management.identify.services.impls;

import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * UserServiceImplTest
 *
 * @author page
 * @date 2022/9/21
 */
@SpringBootTest
class UserServiceImplTest {

    @Resource
    UserService userService;

    @Test
    void createUser() {
        UserVO userVO = userService.createUser("Tex-0041", "tex@tex.com", "+86", "123456", Collections.emptyList());
        Assert.notNull(userVO);
    }
}