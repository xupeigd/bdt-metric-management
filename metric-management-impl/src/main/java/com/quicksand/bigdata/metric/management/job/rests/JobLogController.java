package com.quicksand.bigdata.metric.management.job.rests;

import com.quicksand.bigdata.metric.management.job.BasePath;
import com.quicksand.bigdata.metric.management.job.core.complete.JobCompleter;
import com.quicksand.bigdata.metric.management.job.core.exception.MetricJobException;
import com.quicksand.bigdata.metric.management.job.core.model.JobGroup;
import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import com.quicksand.bigdata.metric.management.job.core.model.JobLog;
import com.quicksand.bigdata.metric.management.job.core.scheduler.MetricJobScheduler;
import com.quicksand.bigdata.metric.management.job.core.util.I18nUtil;
import com.quicksand.bigdata.metric.management.job.repos.JobGroupAutoRepo;
import com.quicksand.bigdata.metric.management.job.repos.JobInfoAutoRepo;
import com.quicksand.bigdata.metric.management.job.repos.JobLogAutoRepo;
import com.quicksand.bigdata.metric.management.job.repos.JobLogRepo;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.LogParam;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Slf4j
@Controller
public class JobLogController {

    @Resource
    JobLogRepo jobLogRepo;
    @Resource
    JobLogAutoRepo jobLogAutoRepo;
    @Resource
    JobInfoAutoRepo jobInfoAutoRepo;
    @Resource
    JobGroupAutoRepo jobGroupAutoRepo;

    @RequestMapping(BasePath.BASE_PATH + "/joblog")
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "0") Integer jobId) {
        // 执行器列表
        List<JobGroup> jobGroupList_all = jobGroupAutoRepo.findAll();
        // filter group
        List<JobGroup> jobGroupList = JobInfoController.filterJobGroupByRole(request, jobGroupList_all);
        if (!CollectionUtils.isEmpty(jobGroupList_all)
                && (jobGroupList == null || jobGroupList.size() == 0)) {
            throw new MetricJobException(I18nUtil.getString("jobgroup_empty"));
        }
        model.addAttribute("JobGroupList", jobGroupList);
        // 任务
        if (jobId > 0) {
            JobInfo jobInfo = jobInfoAutoRepo.findById(jobId).orElse(null);
            if (jobInfo == null) {
                throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
            }
            model.addAttribute("jobInfo", jobInfo);
            // valid permission
            JobInfoController.validPermission(request, jobInfo.getJobGroup());
        }
        return "joblog/joblog.index";
    }

    @RequestMapping(BasePath.BASE_PATH + "/joblog/getJobsByGroup")
    @ResponseBody
    public ReturnT<List<JobInfo>> getJobsByGroup(int jobGroup) {
        List<JobInfo> list = jobInfoAutoRepo.findByJobGroup(jobGroup);
        return new ReturnT<List<JobInfo>>(list);
    }

    @RequestMapping(BasePath.BASE_PATH + "/joblog/pageList")
    @ResponseBody
    public Map<String, Object> pageList(HttpServletRequest request,
                                        @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int jobGroup, int jobId, int logStatus, String filterTime) {
        // valid permission
        JobInfoController.validPermission(request, jobGroup);    // 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup

        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (filterTime != null && filterTime.trim().length() > 0) {
            String[] temp = filterTime.split(" - ");
            if (temp.length == 2) {
                triggerTimeStart = DateUtil.parseDateTime(temp[0]);
                triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
            }
        }

        Page<JobLog> page = jobLogRepo.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        // page query
        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements());        // 总记录数
        maps.put("recordsFiltered", page.getTotalElements());    // 过滤后的总记录数
        maps.put("data", page.getContent());                    // 分页列表
        return maps;
    }

    @RequestMapping(BasePath.BASE_PATH + "/joblog/logDetailPage")
    public String logDetailPage(int id, Model model) {

        // base check
        ReturnT<String> logStatue = ReturnT.SUCCESS;
        JobLog jobLog = jobLogAutoRepo.findById((long) id).orElse(null);
        if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
        }

        model.addAttribute("triggerCode", jobLog.getTriggerCode());
        model.addAttribute("handleCode", jobLog.getHandleCode());
        model.addAttribute("executorAddress", jobLog.getExecutorAddress());
        model.addAttribute("triggerTime", jobLog.getTriggerTime().getTime());
        model.addAttribute("logId", jobLog.getId());
        return "joblog/joblog.detail";
    }

    @RequestMapping(BasePath.BASE_PATH + "/joblog/logDetailCat")
    @ResponseBody
    public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId, int fromLineNum) {
        try {
            ExecutorBiz executorBiz = MetricJobScheduler.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.log(new LogParam(triggerTime, logId, fromLineNum));

            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                JobLog jobLog = jobLogAutoRepo.findById(logId).orElse(null);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return logResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ReturnT<LogResult>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @RequestMapping(BasePath.BASE_PATH + "/joblog/logKill")
    @ResponseBody
    public ReturnT<String> logKill(int id) {
        // base check
        JobLog jobLog = jobLogAutoRepo.findById((long) id).orElse(null);
        JobInfo jobInfo = jobInfoAutoRepo.findById(jobLog.getJobId()).orElse(null);
        if (jobInfo == null) {
            return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (ReturnT.SUCCESS_CODE != jobLog.getTriggerCode()) {
            return new ReturnT<String>(500, I18nUtil.getString("joblog_kill_log_limit"));
        }

        // request of kill
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = MetricJobScheduler.getExecutorBiz(jobLog.getExecutorAddress());
            runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            runResult = new ReturnT<String>(500, e.getMessage());
        }

        if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
            jobLog.setHandleCode(ReturnT.FAIL_CODE);
            jobLog.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
            jobLog.setHandleTime(new Date());
            JobCompleter.updateHandleInfoAndFinish(jobLog);
            return new ReturnT<String>(runResult.getMsg());
        } else {
            return new ReturnT<String>(500, runResult.getMsg());
        }
    }

    @Transactional
    @RequestMapping(BasePath.BASE_PATH + "/joblog/clearLog")
    @ResponseBody
    public ReturnT<String> clearLog(int jobGroup, int jobId, int type) {

        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -1);    // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -3);    // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtil.addMonths(new Date(), -6);    // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtil.addYears(new Date(), -1);    // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;        // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;        // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;        // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;    // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;            // 清理所有日志数据
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_clean_type_unvalid"));
        }

        List<Long> logIds = null;
        do {
            logIds = jobLogRepo.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && logIds.size() > 0) {
                jobLogAutoRepo.deleteByIds(logIds);
            }
        } while (logIds != null && logIds.size() > 0);

        return ReturnT.SUCCESS;
    }

}
