package com.quicksand.bigdata.metric.management.identify.rests;


import com.quicksand.bigdata.metric.management.identify.models.UserAuthModel;
import com.quicksand.bigdata.metric.management.identify.models.UserLoginModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.web.bind.annotation.*;

/**
 * AuthenticationRestService
 * (认证服务)
 *
 * @author page
 * @date 2020/8/21 16:48
 */
public interface AuthenticationRestService {

    /**
     * 登陆
     *
     * @param model 登陆参数
     * @return UserAuthModel
     */
    @PostMapping("/identify/login")
    Response<UserAuthModel> login(@RequestBody UserLoginModel model);

    /**
     * 退出登陆
     *
     * @param userId 退出登陆的用户Id
     * @return Void
     */
    @RequestMapping("/identify/logout/{userId}")
    Response<Void> logout(@PathVariable("userId") int userId);


    /**
     * 退出登陆
     *
     * @return Void
     */
    @RequestMapping("/identify/logout")
    Response<Void> logout();

    /**
     * Jwt注册/登陆
     *
     * @return Void
     */
    @GetMapping("/identify/jwtLogin")
    Response<Void> jwtRegsiterOrLogin(@RequestParam(name = "token", required = false) String token);


}
