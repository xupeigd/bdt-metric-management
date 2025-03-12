package com.quicksand.bigdata.metric.management.job.rests;

import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.repos.UserAutoRepo;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyPasswordEncoder;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.job.BasePath;
import com.quicksand.bigdata.metric.management.job.core.model.JobGroup;
import com.quicksand.bigdata.metric.management.job.core.model.JobUser;
import com.quicksand.bigdata.metric.management.job.core.model.ProxyUser;
import com.quicksand.bigdata.metric.management.job.core.util.I18nUtil;
import com.quicksand.bigdata.metric.management.job.repos.JobGroupAutoRepo;
import com.quicksand.bigdata.metric.management.job.repos.ProxyUserAutoRepo;
import com.quicksand.bigdata.metric.management.job.rests.annotation.PermissionLimit;
import com.quicksand.bigdata.metric.management.job.services.LoginService;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xuxueli 2019-05-04 16:39:50
 */
@Controller
public class ProxyUserController {

    // @Resource
    // JobUserAutoRepo jobUserAutoRepo;
    @Resource
    ProxyUserAutoRepo proxyUserAutoRepo;
    @Resource
    JobGroupAutoRepo jobGroupAutoRepo;
    @Resource
    IdentifyPasswordEncoder identifyPasswordEncoder;
    @Resource
    UserDataManager userDataManager;
    @Resource
    UserAutoRepo userAutoRepo;

    @RequestMapping(BasePath.BASE_PATH + "/user")
    @PermissionLimit(adminuser = true)
    public String index(Model model) {
        // 执行器列表
        List<JobGroup> groupList = jobGroupAutoRepo.findAll();
        model.addAttribute("groupList", groupList);
        return "user/user.index";
    }

    @RequestMapping(BasePath.BASE_PATH + "/user/pageList")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String username, int role) {
        PageRequest pageRequest = PageRequest.of(start / length, length);

        Page<UserDBVO> userDBVOPage = StringUtils.hasText(username)
                ? (0 < role ? userAutoRepo.findByKeywordAndRoleId("%" + username + "%", role, pageRequest)
                : (0 == role ? userAutoRepo.findByKeywordAndRoleIdNot("%" + username + "%", 1, pageRequest)
                : userAutoRepo.findByNameLike("%" + username + "%", pageRequest)))
                : (0 < role ? userAutoRepo.findByRoleId(role, pageRequest)
                : (0 == role ? userAutoRepo.findByRoleIdNot(1, pageRequest)
                : userAutoRepo.findAll(pageRequest)));
        List<JobUser> retData = new ArrayList<>();
        if (null != userDBVOPage
                && !CollectionUtils.isEmpty(userDBVOPage.getContent())) {
            List<Integer> hitUserIds = userDBVOPage.getContent().stream()
                    .map(UserDBVO::getId)
                    .collect(Collectors.toList());
            Map<Integer, ProxyUser> userId2ProxyUsers = proxyUserAutoRepo.findByUserIdIn(hitUserIds).stream()
                    .collect(Collectors.toMap(ProxyUser::getUserId, Function.identity()));

            userDBVOPage.getContent()
                    .forEach(v -> retData.add(JobUser.builder()
                            .id(v.getId())
                            .username(v.getName())
                            .role(!userId2ProxyUsers.containsKey(v.getId()) ? (AuthUtil.isAdmin(v) ? 1 : 0) : userId2ProxyUsers.get(v.getId()).getRole())
                            .password("")
                            .permission(!userId2ProxyUsers.containsKey(v.getId()) ? "" : userId2ProxyUsers.get(v.getId()).getPermission())
                            .build()));
        }
        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", null == userDBVOPage ? 0 : userDBVOPage.getTotalElements());        // 总记录数
        maps.put("recordsFiltered", null == userDBVOPage ? 0 : userDBVOPage.getTotalElements());    // 过滤后的总记录数
        maps.put("data", retData);                    // 分页列表
        return maps;
    }

    @Transactional
    @RequestMapping(BasePath.BASE_PATH + "/user/update")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> update(HttpServletRequest request, JobUser jobUser) {
        // avoid opt login seft
        JobUser loginUser = (JobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getUsername().equals(jobUser.getUsername())) {
            return new ReturnT<>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }
        UserDBVO opUser = userAutoRepo.findById(jobUser.getId()).orElse(null);
        if (null == opUser) {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("user_not_exist"));
        }
        ProxyUser proxyUser = proxyUserAutoRepo.findByUserId(jobUser.getId());
        proxyUser = null != proxyUser ? proxyUser : ProxyUser.builder()
                .userId(jobUser.getId())

                .build();
        proxyUser.setRole(jobUser.getRole());
        proxyUser.setPermission(jobUser.getPermission());
        boolean changePrimaryData = false;
        // valid password
        if (StringUtils.hasText(jobUser.getPassword())) {
            jobUser.setPassword(jobUser.getPassword().trim());
            if (!(jobUser.getPassword().length() >= 4 && jobUser.getPassword().length() <= 20)) {
                return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
            }
            // md5 password
            opUser.setPassword(identifyPasswordEncoder.encode(jobUser.getPassword()));
            changePrimaryData = true;
        } else {
            jobUser.setPassword(null);
        }
        proxyUserAutoRepo.save(proxyUser);
        if (changePrimaryData) {
            userAutoRepo.save(opUser);
        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping(BasePath.BASE_PATH + "/user/remove")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> remove(HttpServletRequest request, int id) {
        // avoid opt login seft
        ProxyUser proxyUser = proxyUserAutoRepo.findByUserId(id);
        if (null == proxyUser) {
            return new ReturnT<>(ReturnT.FAIL.getCode(), I18nUtil.getString("proxy_user_not_exist"));
        }
        JobUser loginUser = (JobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getId() == proxyUser.getUserId()) {
            return new ReturnT<>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }
        proxyUserAutoRepo.deleteById(proxyUser.getId());
        return ReturnT.SUCCESS;
    }

    @Transactional
    @RequestMapping(BasePath.BASE_PATH + "/user/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request, String password) {
        // valid password
        if (password == null || password.trim().length() == 0) {
            return new ReturnT<>(ReturnT.FAIL.getCode(), "密码不可为空");
        }
        password = password.trim();
        if (!(password.length() >= 4 && password.length() <= 20)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit") + "[4-20]");
        }
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        if (null == userDetail) {
            return ReturnT.FAIL;
        }
        UserDBVO user = userDataManager.findUser(userDetail.getId());
        if (null == user) {
            return ReturnT.FAIL;
        }
        user.setPassword(identifyPasswordEncoder.encode(password));
        userDataManager.saveUser(user);
        return ReturnT.SUCCESS;
    }

}
