package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.consts.UserStatus;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.models.OrsUserModel;
import com.quicksand.bigdata.metric.management.identify.models.UserAuthModel;
import com.quicksand.bigdata.metric.management.identify.models.UserLoginModel;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyPasswordEncoder;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyUserDetailSercvice;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.JwtService;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.MetricLogoutHandler;
import com.quicksand.bigdata.metric.management.identify.securities.vos.IdentifyUserDetails;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.OperationLogUtils;
import com.quicksand.bigdata.metric.management.identify.utils.UserModelAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.jwt.JwtToken;
import com.quicksand.bigdata.vars.security.consts.VarsSecurityConsts;
import com.quicksand.bigdata.vars.security.props.SecurityAlgoritmProps;
import com.quicksand.bigdata.vars.security.service.VarsSecurityJudger;
import com.quicksand.bigdata.vars.security.service.VarsSecurityPersistenceService;
import com.quicksand.bigdata.vars.security.vos.UserInfo;
import com.quicksand.bigdata.vars.util.GatewayConsts;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * AuthenticationController
 *
 * @author page
 * @date 2020/8/21 16:58
 */
@Slf4j
@CrossOrigin
// @Api("用户验证Apis")
@RestController
@Tag(name = "用户验证Apis")
public class AuthenticationController
        implements AuthenticationRestService {

    @Value("${vars.security.auth.expired.seconds:1800}")
    int authExpiredSeconds;
    /**
     * 默认的注册角色
     */
    @Value("${vars.jwt.ors.role.default:3}")
    int defaultRoleId;

    @Resource
    JwtService jwtService;
    @Resource
    UserService userService;
    @Resource
    UserDataManager userDataManager;
    @Resource
    VarsSecurityJudger varsSecurityJudger;
    @Resource
    OperationLogService operationLogService;
    @Resource
    MetricLogoutHandler metricLogoutHandler;
    @Resource
    IdentifyPasswordEncoder identifyPasswordEncoder;
    @Resource
    SecurityAlgoritmProps securityAlgoritmProps;
    @Resource
    VarsSecurityPersistenceService varsSecurityPersistenceService;

    /**
     * 线程安全（URL访问带入）
     */
    @Resource
    HttpServletRequest request;
    @Resource
    HttpServletResponse response;

    @SneakyThrows
    @Operation(description = "登陆")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "404", description = "用户名不存在 !"),
            @ApiResponse(responseCode = "401, response = Response.class", description = "密码错误 !"),
            @ApiResponse(responseCode = "423, response = Response.class", description = "账号不可用 !")
    })
    @Override
    public Response<UserAuthModel> login(@RequestBody @Validated UserLoginModel model) {
        //校验登陆依据
        UserVO userVO = userService.findByName(model.getName());
        if (null == userVO) {
            return Response.response(HttpStatus.NOT_FOUND, String.format("用户名为%s不存在 !", model.getName()));
        }
        if (!userVO.getPassword().equals(identifyPasswordEncoder.encode(model.getPassword()))) {
            return Response.response(HttpStatus.UNAUTHORIZED, "密码错误 !");
        }
        if (!UserStatus.ACTIVE.equals(userVO.getStatus())) {
            return Response.response(HttpStatus.LOCKED, "账号不可用 !");
        }
        JwtToken<UserInfo> jwtToken = makeJwtAndCookie(userVO, false);
        //记录当前的UserDetails
        return Response.ok(UserModelAdapter.cover2AuthModel(userVO, jwtToken));
    }

    private JwtToken<UserInfo> makeJwtAndCookie(UserVO userVO, boolean isJwt) throws NoSuchAlgorithmException, InvalidKeyException {
        JwtToken<UserInfo> jwtToken = jwtService.signJwtToken(userVO);
        //签发jwtToken
        IdentifyUserDetails identifyUserDetails = IdentifyUserDetailSercvice.cover2UserDetails(userVO);
        if (null != jwtToken) {
            String tokenStr = jwtToken.toTokenString();
            identifyUserDetails.setValidation(true);
            identifyUserDetails.setConsensus(true);
            identifyUserDetails.setTokenKey(tokenStr);
            //暂存对象
            varsSecurityPersistenceService.saveUserDetails(tokenStr, identifyUserDetails, authExpiredSeconds * 1000L);
            if (varsSecurityJudger.useCookie()) {
                //设置cookie
                Cookie cookie = new Cookie(VarsSecurityConsts.KEY_COOKIES_AUTH, identifyUserDetails.getTokenKey());
                cookie.setPath("/");
                cookie.setMaxAge(-1);
                response.addCookie(cookie);
            }
        }
        //创建验证上下文
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(identifyUserDetails, null, identifyUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("user login! userName:{}`userId:{}`jwtSigned:{}", userVO.getName(), userVO.getId(), null != jwtToken);
        OperationLogDBVO logEntry = OperationLogUtils.buildLoginLog(SecurityContextHolder.getContext().getAuthentication(),
                userVO.getName(), userVO, null != jwtToken);
        logEntry.setType(isJwt ? OperationLogType.JWT_LOGIN : OperationLogType.LOGIN);
        operationLogService.log(logEntry);
        return jwtToken;
    }

    @Operation(description = "登出")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    // @PreAuthorize("isAuthenticated()")
    @Override
    public Response<Void> logout(@PathVariable("userId") int userId) {
        return logout();
    }

    @Operation(description = "登出")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    // @PreAuthorize("isAuthenticated()")
    @Override
    public Response<Void> logout() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (null == context
                || null == context.getAuthentication()
                || null == context.getAuthentication().getPrincipal()) {
            return Response.response(HttpStatus.FORBIDDEN);
        }
        //退出
        metricLogoutHandler.logout(request, response, context.getAuthentication());
        return Response.ok();
    }

    @SneakyThrows
    @Override
    public Response<Void> jwtRegsiterOrLogin(@RequestParam(name = "token", required = false) String token) {
        if (StringUtils.hasText(token)) {
            try {
                //解析并验证jwt
                JwtToken<OrsUserModel> jwtToken = JwtToken.verfifyToken(token, OrsUserModel.class, securityAlgoritmProps);
                if (null == jwtToken.getPayload()) {
                    return Response.response(HttpStatus.NOT_ACCEPTABLE, "无效的token！");
                }
                //判断jwt的有效时间(1分钟)
                if (System.currentTimeMillis() < jwtToken.getHeader().getIat()
                        || System.currentTimeMillis() > (jwtToken.getHeader().getIat() + 60000L)) {
                    return Response.response(HttpStatus.NOT_ACCEPTABLE, "失效的token！");
                }
                UserVO targetUser = null;
                OrsUserModel orsUserModel = jwtToken.getPayload();
                if (Objects.equals("id", orsUserModel.getPk())) {
                    //以id为主键，意味着可能是同系的服务报送
                    targetUser = userService.queryUser(orsUserModel.getId());
                } else if (Objects.equals("mobile", orsUserModel.getPk()) || Objects.equals("email", orsUserModel.getPk())) {
                    @SuppressWarnings("unchecked") HashMap<String, Object> transfrom = JsonUtils.transfrom(orsUserModel, HashMap.class);
                    String pkv = String.valueOf(transfrom.getOrDefault(orsUserModel.getPk(), ""));
                    List<UserDBVO> pkHits = Try.of(() -> userDataManager.findUsersBySpecialProperty(orsUserModel.getPk(), pkv))
                            .onFailure(ex -> log.warn(String.format("findUsersBySpecialProperty error! column:%s,value:%s", orsUserModel.getPk(), pkv), ex))
                            .getOrElse(Collections.emptyList());
                    if (1 < pkHits.size()) {
                        return Response.response(HttpStatus.NOT_ACCEPTABLE, "不能唯一确定用户账户数据！");
                    } else if (CollectionUtils.isEmpty(pkHits)) {
                        // 暂时默认开启jwt注册，但是不授予任何角色
                        // return Response.response(HttpStatus.NOT_ACCEPTABLE, "用户入册暂未开放！");
                        if (null == userService.findByName(orsUserModel.getName())) {
                            targetUser = userService.createUser(orsUserModel.getName(), orsUserModel.getEmail(), orsUserModel.getMobile(),
                                    String.format("Jwt.%s.%s.123", pkv, jwtToken.getHeader().getOrgination()), Collections.singletonList(RoleVO.builder().id(defaultRoleId).build()));
                            log.info("jwt create user ! user:{}", JsonUtils.toString(targetUser));
                        } else {
                            return Response.response(HttpStatus.NOT_ACCEPTABLE, String.format("账户名为%s的账户已经存在!", orsUserModel.getName()));
                        }
                    } else {
                        targetUser = JsonUtils.transfrom(pkHits.get(0), UserVO.class);
                    }
                } else {
                    return Response.response(HttpStatus.NOT_ACCEPTABLE, "不支持的token！");
                }
                if (null != targetUser
                        && !UserStatus.ACTIVE.equals(targetUser.getStatus())) {
                    return Response.response(HttpStatus.LOCKED, "账号不可用 !");
                }
                if (null != targetUser
                        && null != targetUser.getId()
                        && null != makeJwtAndCookie(targetUser, true)) {
                    GatewayConsts.redirectTo(request, response, String.format("/metric/list?refId=%d", targetUser.getId()));
                    return Response.ok("token认证通过！");
                }
            } catch (Exception e) {
                log.warn(String.format("jwtRegsiterOrLogin error ! jwt:【%s】", token), e);
            }
        }
        return Response.response(HttpStatus.NOT_ACCEPTABLE, "不支持的token！");
    }

}
