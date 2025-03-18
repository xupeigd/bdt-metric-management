package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.google.common.collect.Lists;
import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.consts.PubsubStatus;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricQueryModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricQuotaModel;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Class MetricAmisRestController
 *
 * @Author: page
 * @Date: 2025/3/16
 * @Description:
 */
@RequestMapping(Vars.PATH_ROOT + "/amis/metrics")
@RestController
public class MetricAmisRestController {

    @Resource
    MetricDataManager metricDataManager;
    @Resource
    MetricService metricService;

    @GetMapping
    @PreAuthorize("hasAuthority('OP_METRICS_LIST') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    public FrameworkResponse<PageImpl<MetricOverviewModel>, Void> listMetrics(@RequestParam(name = "businessCatalog", required = false, defaultValue = "0") Integer businessId,
                                                                              @RequestParam(name = "page", defaultValue = "1") int pageNo,
                                                                              @RequestParam(name = "perPage", defaultValue = "20") int pageSize) {
        MetricQueryModel metricQueryModel = new MetricQueryModel();
        if (null != businessId && 0 != businessId) {
            metricQueryModel.setBusinessIds(Lists.newArrayList(businessId));
        }
        PageImpl<MetricOverviewModel> overviewModelPage = metricService.queryAllMetrics(metricQueryModel, PageRequest.of(pageNo - 1, pageNo));
        return FrameworkResponse.frameworkResponse(overviewModelPage, null, 0, "");
    }

    @Builder
    @Data
    public static final class Content<T> {
        T content;
        T value;
    }

    @GetMapping("/{metricId}/previewSql")
    public FrameworkResponse<Content<String>, Void> getMetricPreviewSql(@Parameter(name = "指标Id")
                                                                        @Min(value = 1L, message = "不存在的指标！")
                                                                        @Max(value = Integer.MAX_VALUE, message = "不存在的指标！")
                                                                        @PathVariable("metricId") int metricId) {
        MetricDBVO metric = metricDataManager.findByMetricId(metricId);
        if (null == metric) {
            return FrameworkResponse.frameworkResponse(1, "指标不存在或已被删除！");
        }
        String transformSql = metricService.getMetricQuerySql(metricId);
        //获取sql
        if (!StringUtils.hasText(transformSql)) {
            //不重新获取
            return FrameworkResponse.frameworkResponse(1, "指标状态异常：不存在或已被删除！");
        }
        return FrameworkResponse.frameworkResponse(new Content<>(transformSql, null), null, 0, "sucess!");
    }

    @Transactional
    @PutMapping("/{id}/{pubsub}")
    public FrameworkResponse<Content<PubsubStatus>, Void> modifyMetricPubsubStatus(@PathVariable("id") int id,
                                                                                   @PathVariable("pubsub") int pubsub,
                                                                                   @Validated @RequestBody(required = false) MetricQuotaModel metricQuota) {
        PubsubStatus newStatus = PubsubStatus.findByCode(pubsub);
        if (null == newStatus) {
            return FrameworkResponse.frameworkResponse(1, "参数错误！");
        }
        if (null != metricQuota && 0L == metricQuota.getQuota()) {
            return FrameworkResponse.frameworkResponse(1, "额度不能小于0!");
        }
        PubsubStatus pubsubStatus = metricService.modifyMetricPubsubStatus(id, newStatus, metricQuota);
        return FrameworkResponse.frameworkResponse(new Content<>(pubsubStatus, null), null, 0, "success");
    }

    /**
     * 根据主题域和业务域自动生成指标编码
     *
     * @return 指标编码字符串
     */
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE') " +
            "|| @varsSecurityUtilService.isAnonymousUser(authentication) " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetric(authentication,#id) ")
    @GetMapping("/serialNumber")
    public FrameworkResponse<Content<String>, Void> getSerialNumber(@Parameter(name = "topicId", required = false)
                                                                    @Min(value = 1L, message = "不存在的主题域! ")
                                                                    @RequestParam(name = "topicId", required = false) Integer topicId,
                                                                    @Parameter(name = "businessId", required = false)
                                                                    @Min(value = 1L, message = "不存在的业务域! ")
                                                                    @RequestParam(name = "businessId", required = false) Integer businessId) {
        String metricSerialNumber = null != topicId && null != businessId ? metricService.getMetricSerialNumber(topicId, businessId) : "";
        return FrameworkResponse.frameworkResponse(new Content<>(null, metricSerialNumber), null, 0, "success ! ");
    }

}
