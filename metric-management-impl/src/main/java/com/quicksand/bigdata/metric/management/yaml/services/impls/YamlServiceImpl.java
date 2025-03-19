package com.quicksand.bigdata.metric.management.yaml.services.impls;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.Mutability;
import com.quicksand.bigdata.metric.management.consts.YamlSegmentType;
import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.metric.dbvos.MetricDBVO;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.utils.YamlUtil;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;
import com.quicksand.bigdata.metric.management.yaml.dms.SegmentDataManager;
import com.quicksand.bigdata.metric.management.yaml.services.YamlService;
import com.quicksand.bigdata.metric.management.yaml.vos.DatasetSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.SegmentVO;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * YamlServiceImpl
 *
 * @author page
 * @date 2022/8/1
 */
@Service
public class YamlServiceImpl
        implements YamlService {

    @Resource
    SegmentDataManager segmentDataManager;

    @Resource
    MetricService metricService;

    @Override
    public SegmentVO transform2Segement(DatasetVO datasetVO) {
        DatasetSegment datasetSegment = getDatasetSegment(datasetVO);
        //build segmentVO
        String yamlStr = YamlUtil.toYaml(datasetSegment);
        return SegmentVO.builder()
                .dataset(datasetVO)
                .type(YamlSegmentType.Dataset)
                .content(yamlStr)
                .contentMd5(DigestUtils.md5DigestAsHex(yamlStr.getBytes()))
                .build();
    }

    @Override
    public DatasetSegment getDatasetSegment(DatasetVO datasetVO) {
        List<DatasetSegment.Identifier> identifiers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(datasetVO.getIdentifiers())) {
            identifiers.addAll(datasetVO.getIdentifiers()
                    .stream()
                    .map(v ->
                            DatasetSegment.Identifier.builder()
                                    .name(v.getName())
                                    .type(v.getType().getFlag())
                                    .expr(v.getExpr())
                                    .build()
                    )
                    .collect(Collectors.toList()));

        }
        DatasetSegment.MutabilitySeg mutabilitySeg = DatasetSegment.MutabilitySeg.builder()
                .type(datasetVO.getMutability().getType().getFlag())
                .build();
        if (Objects.equals(Mutability.FullMutation, datasetVO.getMutability().getType())
                && StringUtils.hasText(datasetVO.getMutability().getUpdateCron())) {
            DatasetSegment.MutabilityParamSeg mutabilityParamSeg = DatasetSegment.MutabilityParamSeg.builder()
                    .updateCron(datasetVO.getMutability().getUpdateCron())
                    .build();
            mutabilitySeg.setTypeParams(mutabilityParamSeg);
        }
        if (Objects.equals(Mutability.AppendOnly, datasetVO.getMutability().getType())
                && StringUtils.hasText(datasetVO.getMutability().getAlongColumn())) {
            DatasetSegment.MutabilityParamSeg mutabilityParamSeg = DatasetSegment.MutabilityParamSeg.builder()
                    .alongColumn(datasetVO.getMutability().getAlongColumn())
                    .build();
            mutabilitySeg.setTypeParams(mutabilityParamSeg);
        }
        return DatasetSegment.builder()
                // .name(DigestUtils.md5DigestAsHex(datasetVO.getName().getBytes()))
                .name("on79")
                .description(datasetVO.getDescription())
                //postgresql
                .sqlTable(datasetVO.getCluster().getDefaultDatabase() + "." + datasetVO.getTableName())
                .identifiers(identifiers)
                .mutability(mutabilitySeg)
                .build();
    }

    @Override
    public SegmentVO transform2Segement(MetricVO metricVO, YamlSegmentType type) {
        //build segmentVO
        String storeContent = metricVO.getYamlSegment();
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
//        //验证
//        if (Objects.equals(YamlSegmentType.Metric, type)) {
//            MetricMergeSegment metricMergeSegment = metricService.verifyYamlMergeSegment(metricVO.getDataset(), storeContent, false);
//            Assert.isTrue(metricMergeSegment.getVerifySuccess(), "指标信息验证失败");
//            //更新指标签名
//            metricMergeSegment.getMetric().setName(metricVO.getEnName());
//            metricMergeSegment.getMetric().setOwners(Collections.singletonList(userDetail.getUsername()));
//            metricMergeSegment.setVerifySuccess(null);
//            storeContent = YamlUtil.toYaml(metricMergeSegment);
//        }
        return SegmentVO.builder()
                .dataset(JsonUtils.transfrom(metricVO.getDataset(), DatasetVO.class))
                .type(type)
                .content(storeContent) //固定格式存储
                .contentMd5(DigestUtils.md5DigestAsHex((StringUtils.hasText(storeContent) ? storeContent : "").getBytes()))
                .build();
    }

    @Override
    public SegmentVO upsertSegment(MetricVO metricVO, YamlSegmentType type) {
        SegmentVO segment = transform2Segement(metricVO, type);
        List<SegmentDBVO> segments = segmentDataManager.findSegmentByMetricId(type, metricVO.getId());
        boolean sameAs = false;
        int maxVersion = 1;
        if (!CollectionUtils.isEmpty(segments)) {
            SegmentDBVO lastSegment = segments.get(0);
            if (Objects.equals(segment.getContentMd5(), lastSegment.getContentMd5())) {
                sameAs = true;
                segment = JsonUtils.transfrom(lastSegment, SegmentVO.class);
            }
            maxVersion = lastSegment.getVersion() + 1;
        }
        if (!sameAs) {
            Date operationTime = new Date();
            UserSecurityDetails userDetail = AuthUtil.getUserDetail();
            if (!CollectionUtils.isEmpty(segments)) {
                for (SegmentDBVO tmp : segments) {
                    tmp.setStatus(DataStatus.DISABLE);
                    tmp.setUpdateTime(operationTime);
                    tmp.setUpdateUser(UserDBVO.builder().id(null == userDetail ? 0 : userDetail.getId()).build());
                }
                segmentDataManager.updateSegments(segments);
            }
            //直接构建segmentDBVO
            SegmentDBVO newInstance = SegmentDBVO.builder()
                    .dataset(JsonUtils.transfrom(metricVO.getDataset(), DatasetDBVO.class))
                    .content(segment.getContent())
                    .contentMd5(segment.getContentMd5())
                    .type(type)
                    .version(maxVersion)
                    .updateTime(operationTime)
                    .createTime(operationTime)
                    .updateUser(UserDBVO.builder().id(null == userDetail ? 0 : userDetail.getId()).build())
                    .createUserId(null == userDetail ? 0 : userDetail.getId())
                    .status(DataStatus.ENABLE)
                    .metric(JsonUtils.transfrom(metricVO, MetricDBVO.class))
                    .build();
            SegmentDBVO segmentDBVO = segmentDataManager.createSegment(newInstance);
            newInstance.setId(segmentDBVO.getId());
            segment = JsonUtils.transfrom(newInstance, SegmentVO.class);
        }
        return segment;
    }

    @Override
    public SegmentVO upsertSegment(DatasetVO dataset) {
        SegmentVO segment = transform2Segement(dataset);
        List<SegmentDBVO> segments = segmentDataManager.findSegmentByDatasetId(dataset.getId());
        boolean sameAs = false;
        int maxVersion = 1;
        if (!CollectionUtils.isEmpty(segments)) {
            SegmentDBVO lastSegment = segments.get(0);
            if (Objects.equals(segment.getContentMd5(), lastSegment.getContentMd5())) {
                sameAs = true;
                segment = JsonUtils.transfrom(lastSegment, SegmentVO.class);
            }
            maxVersion = lastSegment.getVersion() + 1;
        }
        if (!sameAs) {
            Date operationTime = new Date();
            UserSecurityDetails userDetail = AuthUtil.getUserDetail();
            if (!CollectionUtils.isEmpty(segments)) {
                for (SegmentDBVO tmp : segments) {
                    tmp.setStatus(DataStatus.DISABLE);
                    tmp.setUpdateTime(operationTime);
                    tmp.setMetric(MetricDBVO.builder().id(0).build());
                    tmp.setUpdateUser(UserDBVO.builder().id(null == userDetail ? 0 : userDetail.getId()).build());
                }
                segmentDataManager.updateSegments(segments);
            }
            //直接构建dbvo
            SegmentVO newInstance = SegmentVO.builder()
                    .dataset(dataset)
                    .content(segment.getContent())
                    .contentMd5(segment.getContentMd5())
                    .type(YamlSegmentType.Dataset)
                    .version(maxVersion)
                    .updateTime(operationTime)
                    .createTime(operationTime)
                    .updateUser(UserDBVO.builder().id(null == userDetail ? 0 : userDetail.getId()).build())
                    .createUserId(null == userDetail ? 0 : userDetail.getId())
                    .status(DataStatus.ENABLE)
                    .build();
            SegmentDBVO transfrom = JsonUtils.transfrom(newInstance, SegmentDBVO.class);
            transfrom.setMetric(MetricDBVO.builder().id(0).build());
            SegmentDBVO newDbvo = segmentDataManager.createSegment(transfrom);
            newInstance.setId(newDbvo.getId());
            segment = newInstance;
        }
        return segment;
    }

    @Override
    public void removeSegmentByDatasetId(int datasetId) {
        List<SegmentDBVO> segments = segmentDataManager.findSegmentByDatasetId(datasetId);
        if (!CollectionUtils.isEmpty(segments)) {
            Date operationTime = new Date();
            UserSecurityDetails userDetail = AuthUtil.getUserDetail();
            segments.forEach(v -> {
                v.setStatus(DataStatus.DISABLE);
                v.setUpdateTime(operationTime);
                v.setUpdateUser(UserDBVO.builder().id(null == userDetail ? 0 : userDetail.getId()).build());
            });
            segmentDataManager.updateSegments(segments);
        }
    }

    @Override
    public SegmentVO getLastEnableSegmentByMetricIdAndType(YamlSegmentType type, Integer metricId) {
        List<SegmentDBVO> segments = segmentDataManager.findSegmentByMetricId(type, metricId);
        return CollectionUtils.isEmpty(segments) ? null : JsonUtils.transfrom(segments.get(0), SegmentVO.class);
    }
}
