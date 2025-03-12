package com.quicksand.bigdata.metric.management.yaml.dms.impls;

import com.quicksand.bigdata.metric.management.consts.YamlSegmentType;
import com.quicksand.bigdata.metric.management.yaml.dbvos.SegmentDBVO;
import com.quicksand.bigdata.metric.management.yaml.dms.SegmentDataManager;
import com.quicksand.bigdata.metric.management.yaml.repos.SegmentAutoRepo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * SegmentDataManagerImpl
 *
 * @author page
 * @date 2022/8/1
 */
@Component
public class SegmentDataManagerImpl
        implements SegmentDataManager {

    @Resource
    SegmentAutoRepo segmentAutoRepo;

    @Override
    public List<SegmentDBVO> findSegmentByDatasetId(int datasetId) {
        return segmentAutoRepo.findAllByTypeAndDatasetIdOrderByVersionDesc(YamlSegmentType.Dataset, datasetId);
    }

    @Override
    public List<SegmentDBVO> findSegmentByMetricId(YamlSegmentType type, int metricId) {
        return segmentAutoRepo.findAllByTypeAndMetricIdOrderByVersionDesc(type, metricId);
    }

    @Override
    public void updateSegments(List<SegmentDBVO> segments) {
        segmentAutoRepo.saveAll(segments);
    }

    @Override
    public SegmentDBVO createSegment(SegmentDBVO segment) {
        return segmentAutoRepo.save(segment);
    }

    @Override
    public List<SegmentDBVO> findAllVersionSegmentByMetricId(YamlSegmentType type, int metricId) {
        return segmentAutoRepo.findAllVersionByTypeAndMetricId(type.getCode(), metricId);
    }
}
