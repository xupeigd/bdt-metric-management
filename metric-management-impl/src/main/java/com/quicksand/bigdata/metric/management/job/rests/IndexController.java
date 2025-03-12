package com.quicksand.bigdata.metric.management.job.rests;

import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.job.BasePath;
import com.quicksand.bigdata.metric.management.job.services.LoginService;
import com.quicksand.bigdata.metric.management.job.services.MetricJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

    @Resource
    MetricJobService metricJobService;
    @Resource
    LoginService loginService;

    @GetMapping(BasePath.BASE_PATH + "/")
    public String index(Model model) {
        Authentication authentication = AuthUtil.getAuthentication();
        Map<String, Object> dashboardMap = metricJobService.dashboardInfo();
        model.addAllAttributes(dashboardMap);
        return "index";
    }

    @RequestMapping(BasePath.BASE_PATH + "/chartInfo")
    @ResponseBody
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        return metricJobService.chartInfo(startDate, endDate);
    }

    // @RequestMapping(BasePath.BASE_PATH + "/toLogin")
    // @PermissionLimit(limit = false)
    // public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {
    //     if (loginService.ifLogin(request, response) != null) {
    //         modelAndView.setView(new RedirectView("/", true, false));
    //         return modelAndView;
    //     }
    //     return new ModelAndView("login");
    // }

    // @PostMapping(BasePath.BASE_PATH + "/logins")
    // @ResponseBody
    // @PermissionLimit(limit = false)
    // public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String name, String password, String ifRemember) {
    //     boolean ifRem = (ifRemember != null && ifRemember.trim().length() > 0 && "on".equals(ifRemember)) ? true : false;
    //     return loginService.login(request, response, name, password, ifRem);
    // }

    // @PostMapping(BasePath.BASE_PATH + "/logout")
    // @ResponseBody
    // @PermissionLimit(limit = false)
    // public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
    //     return loginService.logout(request, response);
    // }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
