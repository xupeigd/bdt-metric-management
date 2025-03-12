package com.quicksand.bigdata.metric.management.job.rests;

import com.quicksand.bigdata.metric.management.job.BasePath;
import com.quicksand.bigdata.metric.management.job.core.model.JobGroup;
import com.quicksand.bigdata.metric.management.job.core.model.JobInfo;
import com.quicksand.bigdata.metric.management.job.core.model.JobRegistry;
import com.quicksand.bigdata.metric.management.job.core.util.I18nUtil;
import com.quicksand.bigdata.metric.management.job.repos.JobGroupAutoRepo;
import com.quicksand.bigdata.metric.management.job.repos.JobInfoAutoRepo;
import com.quicksand.bigdata.metric.management.job.repos.JobRegistryAutoRepo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import io.vavr.control.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * job group controller
 *
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
public class JobGroupController {

    @Resource
    JobInfoAutoRepo jobInfoAutoRepo;
    @Resource
    JobGroupAutoRepo jobGroupAutoRepo;
    @Resource
    JobRegistryAutoRepo jobRegistryAutoRepo;

    @RequestMapping(BasePath.BASE_PATH + "/jobgroup")
    public String index(Model model) {
        return "jobgroup/jobgroup.index";
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobgroup/pageList")
    @ResponseBody
    public Map<String, Object> pageList(HttpServletRequest request,
                                        @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String appname, String title) {

        String appKeyword = StringUtils.hasText(appname) ? "%" + appname + "%" : "%";
        String titleKeyword = StringUtils.hasText(title) ? "%" + title + "%" : "%";
        Page<JobGroup> xxlJobGroupPage = jobGroupAutoRepo.findByAppnameLikeAndTitleLike(appKeyword, titleKeyword,
                PageRequest.of(start / length, length, Sort.by(Sort.Direction.DESC, "id")));
        // page query
        List<JobGroup> list = xxlJobGroupPage.getContent();
        int list_count = (int) xxlJobGroupPage.getTotalElements();
        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);        // 总记录数
        maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobgroup/save")
    @ResponseBody
    public ReturnT<String> save(JobGroup jobGroup) {

        // valid
        if (jobGroup.getAppname() == null || jobGroup.getAppname().trim().length() == 0) {
            return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + "AppName"));
        }
        if (jobGroup.getAppname().length() < 4 || jobGroup.getAppname().length() > 64) {
            return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (jobGroup.getAppname().contains(">") || jobGroup.getAppname().contains("<")) {
            return new ReturnT<String>(500, "AppName" + I18nUtil.getString("system_unvalid"));
        }
        if (jobGroup.getTitle() == null || jobGroup.getTitle().trim().length() == 0) {
            return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
        }
        if (jobGroup.getTitle().contains(">") || jobGroup.getTitle().contains("<")) {
            return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_unvalid"));
        }
        if (jobGroup.getAddressType() != 0) {
            if (jobGroup.getAddressList() == null || jobGroup.getAddressList().trim().length() == 0) {
                return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            if (jobGroup.getAddressList().contains(">") || jobGroup.getAddressList().contains("<")) {
                return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_unvalid"));
            }

            String[] addresss = jobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (item == null || item.trim().length() == 0) {
                    return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        // process
        jobGroup.setUpdateTime(new Date());
        int ret = Try.of(() -> {
                    jobGroupAutoRepo.save(jobGroup);
                    return 1;
                })
                .getOrElse(0);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobgroup/update")
    @ResponseBody
    public ReturnT<String> update(JobGroup jobGroup) {
        // valid
        if (jobGroup.getAppname() == null || jobGroup.getAppname().trim().length() == 0) {
            return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + "AppName"));
        }
        if (jobGroup.getAppname().length() < 4 || jobGroup.getAppname().length() > 64) {
            return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_appname_length"));
        }
        if (jobGroup.getTitle() == null || jobGroup.getTitle().trim().length() == 0) {
            return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
        }
        if (jobGroup.getAddressType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(jobGroup.getAppname());
            String addressListStr = null;
            if (registryList != null && !registryList.isEmpty()) {
                Collections.sort(registryList);
                addressListStr = "";
                for (String item : registryList) {
                    addressListStr += item + ",";
                }
                addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
            }
            jobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            if (jobGroup.getAddressList() == null || jobGroup.getAddressList().trim().length() == 0) {
                return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
            }
            String[] addresss = jobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (item == null || item.trim().length() == 0) {
                    return new ReturnT<String>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        // process
        jobGroup.setUpdateTime(new Date());

        int ret = Try.of(() -> {
                    jobGroupAutoRepo.save(jobGroup);
                    return 1;
                })
                .getOrElse(0);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    private List<String> findRegistryByAppName(String appnameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
        List<JobRegistry> list = jobRegistryAutoRepo.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
        if (list != null) {
            for (JobRegistry item : list) {
                if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    String appname = item.getRegistryKey();
                    List<String> registryList = appAddressMap.get(appname);
                    if (registryList == null) {
                        registryList = new ArrayList<String>();
                    }

                    if (!registryList.contains(item.getRegistryValue())) {
                        registryList.add(item.getRegistryValue());
                    }
                    appAddressMap.put(appname, registryList);
                }
            }
        }
        return appAddressMap.get(appnameParam);
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobgroup/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {
        Page<JobInfo> page = jobInfoAutoRepo.findAll(PageRequest.of(0, 1));
        int count = (int) page.getTotalElements();
        // valid
        // int count = jobInfoAutoRepo.pageListCount(0, 10, id, -1, null, null, null);
        if (count > 0) {
            return new ReturnT<String>(500, I18nUtil.getString("jobgroup_del_limit_0"));
        }
        List<JobGroup> allList = jobGroupAutoRepo.findAll();
        if (allList.size() == 1) {
            return new ReturnT<String>(500, I18nUtil.getString("jobgroup_del_limit_1"));
        }
        // int ret = xxlJobGroupDao.remove(id);
        int ret = Try.of(() -> {
                    jobGroupAutoRepo.deleteById(id);
                    return 1;
                })
                .getOrElse(0);
        return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping(BasePath.BASE_PATH + "/jobgroup/loadById")
    @ResponseBody
    public ReturnT<JobGroup> loadById(int id) {
        JobGroup jobGroup = jobGroupAutoRepo.findById(id).orElse(null);
        // XxlJobGroup jobGroup = xxlJobGroupDao.load(id);
        return jobGroup != null ? new ReturnT<JobGroup>(jobGroup) : new ReturnT<JobGroup>(ReturnT.FAIL_CODE, null);
    }

}
