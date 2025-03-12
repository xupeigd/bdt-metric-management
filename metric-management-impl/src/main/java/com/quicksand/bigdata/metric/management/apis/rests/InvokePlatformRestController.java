package com.quicksand.bigdata.metric.management.apis.rests;

import com.quicksand.bigdata.metric.management.apis.models.ConditionModel;
import com.quicksand.bigdata.metric.management.apis.models.ExplainAttributesModel;
import com.quicksand.bigdata.metric.management.apis.models.ExplainGroupModel;
import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.apis.rest.InvokePlatformRestService;
import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.ClusterInfoDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.ClusterInfoModel;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.yaml.services.ExplainService;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author page
 * @date 2022/10/20
 */
@Slf4j
@RestController
@Tag(name = "调用平台服务")
public class InvokePlatformRestController
        implements InvokePlatformRestService {

    @Resource
    MetricService metricService;
    @Resource(name = "ExplainSpliceServiceImpl")
    ExplainService explainService;
    @Resource
    ClusterInfoDataManager clusterInfoDataManager;

    /**
     * 获取所有的clsuter连接信息
     *
     * @param clusterIds 指定查询的集群id
     * @return list of ClusterInfoModel
     */
    @Operation(description = "获取所有的clsuter连接信息：内部专用")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN') " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) ")
    @Override
    public Response<List<ClusterInfoModel>> fecthAllClusterInfos(@RequestParam(name = "clusterIds", required = false) List<Integer> clusterIds) {
        List<ClusterInfoDBVO> clusterInfoDBVOS = CollectionUtils.isEmpty(clusterIds) ? clusterInfoDataManager.findAllClusterInfos() : clusterInfoDataManager.findClusterInfosById(clusterIds);
        return Response.ok(clusterInfoDBVOS.stream()
                .map(v -> {
                    ClusterInfoModel cim = new ClusterInfoModel();
                    BeanUtils.copyProperties(v, cim);
                    return cim;
                })
                .collect(Collectors.toList()));
    }

    /**
     * 解析指标
     *
     * @param attributes 解析参数
     * @return list of ExplainGroupModel
     */
    @Operation(description = "解析指标")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasRole('ADMIN') " +
            "|| @appSecurityUtilService.hasSeniorAuthority(authentication) " +
            "|| @appSecurityUtilService.hasMetrics(authentication,#attributes.metricIds) ")
    @Override
    public Response<List<ExplainGroupModel>> explainMetrics(@Validated @RequestBody ExplainAttributesModel attributes) {
        long starMills = System.currentTimeMillis();
        //初步验证参数
        if (CollectionUtils.isEmpty(attributes.getMetricIds()) && CollectionUtils.isEmpty(attributes.getMetrics())) {
            return Response.response(HttpStatus.BAD_REQUEST, "请提供需要解析的指标Id或者指标编码或者指标英文名！");
        }
        if (attributes.getMetricIds().stream().anyMatch(v -> 0 >= v)) {
            return Response.response(HttpStatus.BAD_REQUEST, "指标Id不小于0！");
        }
        if (attributes.getDimensions().stream().anyMatch(v -> !StringUtils.hasText(v))) {
            return Response.response(HttpStatus.BAD_REQUEST, "解析纬度包含不支持的纬度！");
        }
        if (null != attributes.getCondition()
                && ConditionModel.resolveNames(attributes.getCondition()).stream().anyMatch(v -> !attributes.getDimensions().contains(v))) {
            return Response.response(HttpStatus.BAD_REQUEST, "Condition中包含非选定的纬度！");
        }
        if (!CollectionUtils.isEmpty(attributes.getSorts())
                && attributes.getSorts().stream().map(SortModel::getName).anyMatch(v -> !attributes.getDimensions().contains(v))) {
            return Response.response(HttpStatus.BAD_REQUEST, "排序中包含非选定纬度！");
        }
        //查询指标数据
        List<MetricVO> metricVOs = metricService.findMetricByIds(attributes.getMetricIds());
        if (CollectionUtils.isEmpty(metricVOs)
                || new HashSet<>(attributes.getMetricIds()).size() != metricVOs.size()) {
            return Response.response(HttpStatus.BAD_REQUEST, "解析指标缺失！");
        }
        log.info("explainMetrics findMetricByIds ! cost:{}", System.currentTimeMillis() - starMills);
        //逐个指标校验是否符合解析约束
        List<MetricVO> invalidationMetrics = metricVOs.stream()
                .filter(v -> !explainService.validation(v, v.getDataset(), attributes.getDimensions(), attributes.getCondition(), attributes.getSorts()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(invalidationMetrics)) {
            return Response.response(HttpStatus.BAD_REQUEST, String.format("指标[%s]不符合解析约束!",
                    StringUtils.collectionToCommaDelimitedString(invalidationMetrics.stream().map(MetricVO::getId).collect(Collectors.toList()))));
        }
        log.info("explainMetrics validation passed ! cost:{}", System.currentTimeMillis() - starMills);
        //分批 合并解析
        Map<DatasetVO, List<MetricVO>> ds2Metrics = new HashMap<>();
        if (!CollectionUtils.isEmpty(metricVOs)) {
            for (MetricVO metricVO : metricVOs) {
                List<MetricVO> tmpMetrics = ds2Metrics.getOrDefault(metricVO.getDataset(), new ArrayList<>());
                ds2Metrics.put(metricVO.getDataset(), tmpMetrics);
                tmpMetrics.add(metricVO);
            }
        }
        log.info("explainMetrics compile start ! cost:{}", System.currentTimeMillis() - starMills);
        //运行解析
        List<ExplainGroupModel> explainGroupModels = new ArrayList<>();
        ds2Metrics.forEach((k, v) -> {
            String explainSql = explainService.expain2Sql(v, attributes.getDimensions(), attributes.getCondition(), attributes.getSorts());
            //判断成败，组装对象
            if (!StringUtils.hasText(explainSql) || explainSql.contains("ERR")) {
                log.error("explainMetrics fail ! metrics:[{}],dimenisons:[{}],condition:[{}], sorts:[{}],explainSql:[{}]", JsonUtils.toJsonString(v),
                        StringUtils.collectionToCommaDelimitedString(attributes.getDimensions()), JsonUtils.toJsonString(attributes.getCondition()),
                        StringUtils.collectionToCommaDelimitedString(attributes.getSorts()), explainSql);
            } else {
                explainGroupModels.add(ExplainGroupModel.builder()
                        .metricsIds(v.stream().map(MetricVO::getId).collect(Collectors.toList()))
                        .metricsNames(v.stream().map(MetricVO::getEnName).collect(Collectors.toList()))
                        .condition(attributes.getCondition())
                        .dimensions(attributes.getDimensions())
                        .sorts(attributes.getSorts())
                        .sql(explainSql)
                        .datasetId(k.getId())
                        .clusterId(null == k.getCluster() ? 0 : k.getCluster().getId())
                        .build());
            }
        });
        log.info("explainMetrics compile end ! cost:{}", System.currentTimeMillis() - starMills);
        return Response.ok(explainGroupModels);
    }

}
