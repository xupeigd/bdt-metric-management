package com.quicksand.bigdata.metric.management.job.rests;

import com.quicksand.bigdata.metric.management.job.BasePath;
import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import com.quicksand.bigdata.metric.management.job.core.model.JobLogGlue;
import com.quicksand.bigdata.metric.management.job.core.util.I18nUtil;
import com.quicksand.bigdata.metric.management.job.repos.JobInfoAutoRepo;
import com.quicksand.bigdata.metric.management.job.repos.JobLogGlueAutoRepo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * job code controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller

public class JobCodeController {

    @Resource
    JobInfoAutoRepo jobInfoAutoRepo;
    @Resource
    JobLogGlueAutoRepo jobLogGlueAutoRepo;

    @RequestMapping(BasePath.BASE_PATH + "/jobcode")
    public String index(HttpServletRequest request, Model model, int jobId) {
        JobInfo jobInfo = jobInfoAutoRepo.findById(jobId).orElse(null);
        List<JobLogGlue> jobLogGlues = jobLogGlueAutoRepo.findByJobIdOrderByIdDesc(jobId);

        if (jobInfo == null) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
            throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
        }

        // valid permission
        JobInfoController.validPermission(request, jobInfo.getJobGroup());

        // Glue类型-字典
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

        model.addAttribute("jobInfo", jobInfo);
        model.addAttribute("jobLogGlues", jobLogGlues);
        return "jobcode/jobcode.index";
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobcode/save")
    @ResponseBody
    public ReturnT<String> save(Model model, int id, String glueSource, String glueRemark) {
        // valid
        if (glueRemark == null) {
            return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark")));
        }
        if (glueRemark.length() < 4 || glueRemark.length() > 100) {
            return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_remark_limit"));
        }
        JobInfo exists_jobInfo = jobInfoAutoRepo.findById(id).orElse(null);
        if (exists_jobInfo == null) {
            return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }

        // update new code
        exists_jobInfo.setGlueSource(glueSource);
        exists_jobInfo.setGlueRemark(glueRemark);
        exists_jobInfo.setGlueUpdatetime(new Date());

        exists_jobInfo.setUpdateTime(new Date());
        jobInfoAutoRepo.save(exists_jobInfo);

        // log old code
        JobLogGlue jobLogGlue = new JobLogGlue();
        jobLogGlue.setJobId(exists_jobInfo.getId());
        jobLogGlue.setGlueType(exists_jobInfo.getGlueType());
        jobLogGlue.setGlueSource(glueSource);
        jobLogGlue.setGlueRemark(glueRemark);

        jobLogGlue.setAddTime(new Date());
        jobLogGlue.setUpdateTime(new Date());
        jobLogGlueAutoRepo.save(jobLogGlue);

        // remove code backup more than 30
        jobLogGlueAutoRepo.removeOld(exists_jobInfo.getId(), 30);

        return ReturnT.SUCCESS;
    }

}
