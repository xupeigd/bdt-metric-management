package com.quicksand.bigdata.metric.management.yaml.services.impls;

import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.yaml.services.VarsMetricSegmentService;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricMergeSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author page
 * @date 2022/10/21
 */
@Slf4j
@Service
public class VarsMetricSegmentServiceImpl
        implements VarsMetricSegmentService {

    @Override
    public MetricMergeSegment cover2Segment(List<MetricVO> metrics, DatasetDBVO dataset) {
        //合并纬度，度量
        //创建builder
        return null;
    }

}
