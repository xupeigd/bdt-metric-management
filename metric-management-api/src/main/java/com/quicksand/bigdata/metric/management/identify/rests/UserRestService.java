package com.quicksand.bigdata.metric.management.identify.rests;


import com.quicksand.bigdata.metric.management.identify.models.UserDetailsModel;
import com.quicksand.bigdata.metric.management.identify.models.UserMixDetailModel;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * UserRestService
 *
 * @author page
 * @date 2020/8/18
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.UserRestService:}",
        contextId = "UserRestService")
public interface UserRestService {

    @GetMapping("/identify/users/{userId}/info")
    Response<UserMixDetailModel> queryUserMixInfo(@PathVariable("userId") int userId);


    /**
     * 获取用户列表
     *
     * @param pageNo   页码（基于1），默认1
     * @param pageSize 页容量 默认20
     * @param keyword  模糊搜索关键字
     * @param roleId   角色id
     * @return PageModel of UserDetailsModel
     */
    @GetMapping("/identify/users")
    Response<PageImpl<UserDetailsModel>> listUserDetails(@RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                         @RequestParam(name = "keyword", required = false) String keyword,
                                                         @RequestParam(name = "roleId", required = false) Integer roleId);


}
