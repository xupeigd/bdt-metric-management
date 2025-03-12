package com.quicksand.bigdata.metric.management.job.rests;

import com.quicksand.bigdata.metric.management.job.BasePath;
import com.quicksand.bigdata.metric.management.job.core.exception.MetricJobException;
import com.quicksand.bigdata.metric.management.job.core.model.JobGroup;
import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import com.quicksand.bigdata.metric.management.job.core.model.JobUser;
import com.quicksand.bigdata.metric.management.job.core.route.ExecutorRouteStrategyEnum;
import com.quicksand.bigdata.metric.management.job.core.scheduler.MisfireStrategyEnum;
import com.quicksand.bigdata.metric.management.job.core.scheduler.ScheduleTypeEnum;
import com.quicksand.bigdata.metric.management.job.core.thread.JobScheduleHelper;
import com.quicksand.bigdata.metric.management.job.core.thread.JobTriggerPoolHelper;
import com.quicksand.bigdata.metric.management.job.core.trigger.TriggerTypeEnum;
import com.quicksand.bigdata.metric.management.job.core.util.I18nUtil;
import com.quicksand.bigdata.metric.management.job.repos.JobGroupAutoRepo;
import com.quicksand.bigdata.metric.management.job.services.LoginService;
import com.quicksand.bigdata.metric.management.job.services.MetricJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Slf4j
@Controller

public class JobInfoController {


    @Resource
    JobGroupAutoRepo jobGroupAutoRepo;
    @Resource
    MetricJobService metricJobService;

    public static List<JobGroup> filterJobGroupByRole(HttpServletRequest request, List<JobGroup> allJobGroups) {
        List<JobGroup> jobGroupList = new ArrayList<>();
        if (allJobGroups != null && allJobGroups.size() > 0) {
            JobUser loginUser = (JobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
            if (loginUser.getRole() == 1) {
                jobGroupList = allJobGroups;
            } else {
                List<String> groupIdStrs = new ArrayList<>();
                if (loginUser.getPermission() != null && loginUser.getPermission().trim().length() > 0) {
                    groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
                }
                for (JobGroup groupItem : allJobGroups) {
                    if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
                        jobGroupList.add(groupItem);
                    }
                }
            }
        }
        return jobGroupList;
    }

    public static void validPermission(HttpServletRequest request, int jobGroup) {
        JobUser loginUser = (JobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (!loginUser.validPermission(jobGroup)) {
            throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username=" + loginUser.getUsername() + "]");
        }
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo")
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());        // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());        // 阻塞处理策略-字典
        model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());                        // 调度类型
        model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());                    // 调度过期策略
        // 执行器列表
        List<JobGroup> allJobGroups = jobGroupAutoRepo.findAll();
        // filter group
        List<JobGroup> jobGroupList = filterJobGroupByRole(request, allJobGroups);
        if (!CollectionUtils.isEmpty(allJobGroups)
                && (jobGroupList == null || jobGroupList.size() == 0)) {
            throw new MetricJobException(I18nUtil.getString("jobgroup_empty"));
        }
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);
        return "jobinfo/jobinfo.index";
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        Integer jobGroup, Integer triggerStatus, String jobDesc, String executorHandler, String author) {

        return metricJobService.pageList(start, length, null == jobGroup ? -1 : jobGroup, null == triggerStatus ? -1 : triggerStatus, jobDesc, executorHandler, author);
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo/add")
    @ResponseBody
    public ReturnT<String> add(JobInfo jobInfo) {
        return metricJobService.add(jobInfo);
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo/update")
    @ResponseBody
    public ReturnT<String> update(JobInfo jobInfo) {
        return metricJobService.update(jobInfo);
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {
        return metricJobService.remove(id);
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo/stop")
    @ResponseBody
    public ReturnT<String> pause(int id) {
        return metricJobService.stop(id);
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo/start")
    @ResponseBody
    public ReturnT<String> start(int id) {
        return metricJobService.start(id);
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo/trigger")
    @ResponseBody
    //@PermissionLimit(limit = false)
    public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }
        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return ReturnT.SUCCESS;
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobinfo/nextTriggerTime")
    @ResponseBody
    public ReturnT<List<String>> nextTriggerTime(String scheduleType, String scheduleConf) {

        JobInfo paramJobInfo = new JobInfo();
        paramJobInfo.setScheduleType(scheduleType);
        paramJobInfo.setScheduleConf(scheduleConf);

        List<String> result = new ArrayList<>();
        try {
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = JobScheduleHelper.generateNextValidTime(paramJobInfo, lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type") + I18nUtil.getString("system_unvalid")) + e.getMessage());
        }
        return new ReturnT<>(result);

    }

}
