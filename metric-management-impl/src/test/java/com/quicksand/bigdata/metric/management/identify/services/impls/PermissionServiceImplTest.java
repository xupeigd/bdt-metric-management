package com.quicksand.bigdata.metric.management.identify.services.impls;

import com.quicksand.bigdata.metric.management.identify.dbvos.RoleDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.RoleDataManager;
import com.quicksand.bigdata.metric.management.identify.services.PermissionService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PermissionServiceImplTest
 *
 * @author page
 * @date 2022/9/27
 */
@SpringBootTest
class PermissionServiceImplTest {

    @Resource
    UserService userService;
    @Resource
    RoleDataManager roleDataManager;
    @Resource
    PermissionService permissionService;

    @Test
    public void permissionLifeCycleTest() {
        //创建用户，grant以admin角色
        //查询所有权限
        //随机撤销30%的权限
        UserVO userVO = userService.createUser("pmsl", "pmsl@pmsl.com", "+86", "123456", Collections.singletonList(RoleVO.builder().id(1).build()));
        Assert.notNull(userVO);
        UserVO queryUser = userService.queryUser(userVO.getId());
        RoleDBVO adminRole = roleDataManager.findRole(1);
        Assert.isTrue(PermissionService.resolveUserPermission(queryUser, false).size() == adminRole.getPermissions().size());
        //清理权限
        List<Integer> revokePermissionIds = new ArrayList<>();
        for (int i = 0; i < adminRole.getPermissions().size(); i++) {
            if (0 == i % 2) {
                revokePermissionIds.add(-1 * adminRole.getPermissions().get(i).getId());
            }
        }
        List<PermissionVO> permissionVOS = permissionService.modifyUserPermissions(queryUser, revokePermissionIds);
        queryUser = userService.queryUser(userVO.getId());
        Assert.isTrue(PermissionService.resolveUserPermission(queryUser, false).size() == (adminRole.getPermissions().size() + revokePermissionIds.size()));
    }

}