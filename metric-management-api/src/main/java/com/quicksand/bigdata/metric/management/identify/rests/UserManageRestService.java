package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.UserModifyModel;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import com.quicksand.bigdata.metric.management.identify.models.UserStatusModifyModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * UserManagerRestService
 *
 * @author page
 * @date 2020/8/18 13:49
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.UserManageRestService:}",
        contextId = "UserManageRestService")
public interface UserManageRestService {

    /**
     * 创建新用户
     *
     * @param model 创建参数model
     * @return UserOverviewModel
     */
    @PostMapping("/identify/users")
    Response<UserOverviewModel> createUser(@RequestBody UserModifyModel model);


    /**
     * 修改用户数据
     *
     * @param userId 用户id
     * @param model  修改参数model
     * @return UserOverviewModel
     */
    @PutMapping("/identify/users/{userId}")
    Response<UserOverviewModel> modifyUserData(@PathVariable("userId") int userId,
                                               @RequestBody UserModifyModel model);


    /**
     * 修改用户账号的活跃/非活跃状态
     *
     * @param userId 用户Id
     * @param model  参数Model
     * @return UserOverviewModel
     */
    @PutMapping("/identify/users/{userId}/activate")
    Response<UserOverviewModel> changeUserStatus(@PathVariable("userId") int userId,
                                                 @RequestBody UserStatusModifyModel model);

}
