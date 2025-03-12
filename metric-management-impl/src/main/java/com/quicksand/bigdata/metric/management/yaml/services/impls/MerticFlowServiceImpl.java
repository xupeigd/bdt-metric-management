package com.quicksand.bigdata.metric.management.yaml.services.impls;

import com.google.common.collect.Lists;
import com.quicksand.bigdata.metric.management.apis.models.ConditionModel;
import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.consts.IdentifierType;
import com.quicksand.bigdata.metric.management.consts.YamlSegmentType;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.DatasetDataManager;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.datasource.vos.IdentifierVO;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.services.MetricTransformService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricDimensionVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.utils.MetricFlowProcessUtil;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;
import com.quicksand.bigdata.metric.management.yaml.dms.SegmentDataManager;
import com.quicksand.bigdata.metric.management.yaml.services.ExplainService;
import com.quicksand.bigdata.metric.management.yaml.services.MerticFlowService;
import com.quicksand.bigdata.metric.management.yaml.services.YamlService;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.ValidationException;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MerticFlowServiceImpl
 * (MF的实现类)
 *
 * @author page
 * @date 2022/10/18
 */
@Slf4j
@Service
public class MerticFlowServiceImpl
        implements MerticFlowService, ExplainService {

    @Value("${vars.metric.join.enable:false}")
    boolean joinEnable;

    @Resource
    MetricFileService metricFileService;
    @Resource
    SegmentDataManager segmentDataManager;
    @Resource
    DatasetDataManager datasetDataManager;
    @Resource
    MetricService metricService;
    @Resource
    MetricTransformService metricTransformService;
    @Resource
    YamlService yamlService;

    /**
     * 校验的临时结果
     */
    ThreadLocal<Map<String, Boolean>> validationResult = new ThreadLocal<>();

    @Override
    public String flag() {
        return "MetricFlow";
    }

    @SneakyThrows
    @Override
    public String expain2Sql(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) {
        if (metrics.stream().allMatch(v -> validation(v, v.getDataset(), dimensions, condition, sorts))) {
            dispatchConfigs(metrics, dimensions, condition, sorts);
            return Try.of(() -> explain(metrics, dimensions, condition, sorts))
                    .andFinally(() -> clean(metrics, dimensions, condition, sorts))
                    .andFinally(() -> validationResult.remove())
                    .get();
        }
        return null;
    }

    @Override
    public boolean validation(MetricVO metric, DatasetVO dataset, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) {
        String vrk = String.format("%d:%d:%s:%s:%s", metric.getId(), dataset.getId(),
                DigestUtils.md5DigestAsHex(StringUtils.collectionToCommaDelimitedString(dimensions).getBytes()),
                null == condition ? "" : DigestUtils.md5DigestAsHex(JsonUtils.toJsonString(condition).getBytes()),
                CollectionUtils.isEmpty(sorts) ? "" : DigestUtils.md5DigestAsHex(JsonUtils.toJsonString(sorts).getBytes()));
        Map<String, Boolean> vrc = validationResult.get();
        if (null == vrc) {
            synchronized (this) {
                vrc = validationResult.get();
                if (null == vrc) {
                    vrc = new HashMap<>();
                    validationResult.set(vrc);
                }
            }
        }
        Boolean validation = vrc.get(vrk);
        if (null != validation) {
            return Objects.equals(true, validation);
        }
        Map<String, Boolean> finalVrc = vrc;
        //是否支持外表
        List<IdentifierVO> foreignKeys = CollectionUtils.isEmpty(dataset.getIdentifiers())
                ? Collections.emptyList()
                : dataset.getIdentifiers().stream()
                .filter(v -> Objects.equals(IdentifierType.Foreign, v.getType()))
                .collect(Collectors.toList());
        Map<String, MetricDimensionVO> name2Dimenisons = metric.getDimensions().stream()
                .collect(Collectors.toMap(MetricDimensionVO::getName, Function.identity()));
        boolean hasCrossDataset = dimensions.stream().anyMatch(v -> v.contains("__"));
        if (hasCrossDataset && foreignKeys.isEmpty()) {
            throw new ValidationException("不支持关联纬度！");
        }
        if (!joinEnable
                && dimensions.stream().anyMatch(v -> v.contains("__"))) {
            //v1.1版本不开放
            throw new ValidationException("不支持关联纬度！");
        }
        //先验证正常的纬度
        List<String> notExistDimensions = dimensions.stream()
                .filter(v -> !v.contains("__"))
                .filter(v -> !name2Dimenisons.containsKey(v))
                .collect(Collectors.toList());
        if (!notExistDimensions.isEmpty()) {
            throw new ValidationException(String.format("纬度[%s]不存在！", StringUtils.collectionToCommaDelimitedString(notExistDimensions)));
        }
        if (null != condition) {
            //验证条件纬度
            if (!ConditionModel.validation(condition)) {
                throw new ValidationException("错误的condition！");
            }
            List<String> notExistConditionDimensions = ConditionModel.resolveNames(condition).stream()
                    .filter(v -> !name2Dimenisons.containsKey(v))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(notExistConditionDimensions)) {
                throw new ValidationException(String.format("condition中的纬度[%s]不存在！", StringUtils.collectionToCommaDelimitedString(notExistConditionDimensions)));
            }
        }
        //再验证排序纬度
        if (!CollectionUtils.isEmpty(sorts)) {
            List<String> notExistSortDimensions = sorts.stream().map(SortModel::getName)
                    .filter(v -> !name2Dimenisons.containsKey(v))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(notExistSortDimensions)) {
                throw new ValidationException(String.format("排序中的纬度[%s]不存在！", StringUtils.collectionToCommaDelimitedString(notExistSortDimensions)));
            }
        }
        if (joinEnable) {
            //验证穿越纬度
            List<String> crossDimensions = dimensions.stream()
                    .filter(v -> v.contains("__"))
                    .collect(Collectors.toList());
            if (!crossDimensions.isEmpty()) {
                //todo 一度穿越，二度穿越
            }
        }
        vrc.put(vrk, true);
        return true;
    }

    @Override
    public void dispatchConfigs(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) throws Exception {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        //链接配置(必须是同源数据集合)
        if (metricFileService.replaceConfigFile(metrics.get(0).getDataset().getCluster())) {
            if (joinEnable) {
                //dispatch穿越纬度的dataset
                List<String> crossDimensions = dimensions.stream()
                        .filter(v -> v.contains("__"))
                        .filter(v -> !(v.endsWith("day") || v.endsWith("week") || v.endsWith("month")
                                || v.endsWith("quarter") || v.endsWith("year")))
                        .collect(Collectors.toList());
                List<String> dsNames = new ArrayList<>();
                List<String> finalDsNames = dsNames;
                crossDimensions.stream()
                        .map(v -> v.split("__"))
                        .forEach(v -> finalDsNames.addAll(Lists.newArrayList(v)));
                dsNames = new ArrayList<>(new HashSet<>(dsNames));
                //查询数据集
                Map<String, DatasetDBVO> name2Dataset = datasetDataManager.findDatasetByNames(dsNames).stream()
                        .collect(Collectors.toMap(DatasetDBVO::getName, Function.identity()));
                List<String> lostDs = dsNames.stream()
                        .filter(v -> !name2Dataset.containsKey(v))
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(lostDs)) {
                    throw new Exception(String.format("数据集[%s]不存在！", StringUtils.collectionToCommaDelimitedString(lostDs)));
                }
                //查询数据集的片段
                Map<String, SegmentDBVO> name2dsSegs = name2Dataset.values().stream()
                        .map(v -> {
                            List<SegmentDBVO> segs = segmentDataManager.findSegmentByDatasetId(v.getId());
                            return CollectionUtils.isEmpty(segs) ? null : segs.get(0);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(k -> k.getDataset().getName(), Function.identity()));
                Collection<String> subtractDs = org.apache.commons.collections4.CollectionUtils.subtract(name2Dataset.keySet(), name2dsSegs.keySet());
                if (!CollectionUtils.isEmpty(subtractDs)) {
                    throw new Exception(String.format("数据集[%s]配置异常！", StringUtils.collectionToCommaDelimitedString(subtractDs)));
                }
                //循环写配置
                for (Map.Entry<String, SegmentDBVO> entry : name2dsSegs.entrySet()) {
                    if (!metricFileService.createYamlFiles(null == userDetail ? "" : userDetail.getName(), entry.getKey(), entry.getValue().getContent())) {
                        throw new FileNotFoundException(String.format("dispatchConfigs cross dataset [%s %d] yaml files fail ! ",
                                entry.getValue().getDataset().getName(), entry.getValue().getDataset().getId()));
                    }
                }
            }

            //todo 理想状态下
            // {
            //     //数据集配置
            //     List<SegmentDBVO> datasetSegments = segmentDataManager.findSegmentByDatasetId(metrics.get(0).getDataset().getId());
            //     if (CollectionUtils.isEmpty(datasetSegments)) {
            //         throw new Exception("数据集状态异常！");
            //     }
            //     SegmentDBVO datasetSegment = datasetSegments.get(0);
            //     for (MetricVO metric : metrics) {
            //         List<SegmentDBVO> metricSegments = segmentDataManager.findSegmentByMetricId(YamlSegmentType.Metric, metric.getId());
            //         if (CollectionUtils.isEmpty(metricSegments)) {
            //             throw new Exception("指标状态异常！");
            //         }
            //         SegmentDBVO metricSegment = metricSegments.get(0);
            //         //指标配置
            //         if (!metricFileService.createYamlFiles(null == userDetail ? "" : userDetail.getName(), metric.getEnName(), metricSegment.getContent() + "\n" + metricSegment.getContent())) {
            //             throw new FileNotFoundException("dispatchConfigs yaml files fail ! ");
            //         }
            //     }
            // }

            // 目前 非理想状态
            for (MetricVO metric : metrics) {
                List<SegmentDBVO> metricSegments = segmentDataManager.findSegmentByMetricId(YamlSegmentType.Metric, metric.getId());
                if (CollectionUtils.isEmpty(metricSegments)) {
                    throw new Exception("指标状态异常！");
                }
                SegmentDBVO metricSegment = metricSegments.get(0);
                String executeYamlContent = metricTransformService.transformToExecuteYaml(metricSegment.getContent());
                //指标配置
                if (!metricFileService.createYamlFiles(null == userDetail ? "" : userDetail.getName(), metric.getEnName(), executeYamlContent)) {
                    throw new FileNotFoundException("dispatchConfigs yaml files fail ! ");
                }
            }
        } else {
            log.warn("dispatchConfigs fail ! metric:[{}]", StringUtils.collectionToCommaDelimitedString(metrics.stream().map(v -> v.getCnName() + ":" + v.getId()).collect(Collectors.toList())));
            throw new FileNotFoundException("dispatchConfigs fail ! ");
        }
    }

    @Override
    public String explain(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) {
        //执行解析
        return MetricFlowProcessUtil.getExplainSql(metrics, metrics.stream().map(MetricVO::getEnName).collect(Collectors.toList()), dimensions, condition, sorts);
    }

    @Override
    public void clean(List<MetricVO> metrics, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) {
        //清理配置文件
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        for (MetricVO metric : metrics) {
            metricFileService.deleteYamlFiles(null == userDetail ? "" : userDetail.getName(), metric.getEnName());
        }
    }

}
