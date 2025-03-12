package com.quicksand.bigdata.metric.management.yaml.services.impls;

import com.quicksand.bigdata.metric.management.apis.models.ConditionModel;
import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.yaml.services.ExplainService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ExplainSpliceServiceImplTest
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/12/15 16:49
 * @description
 */
@SpringBootTest
class ExplainSpliceServiceImplTest {
    @Resource(name = "ExplainSpliceServiceImpl")
    ExplainService explainService;

    @Resource
    MetricService metricService;

    @Test
    void expain2Sql() {
        List<MetricVO> metrics = new ArrayList<>();
        List<Integer> integers = Arrays.asList(1502);
        List<MetricVO> metricByIds = metricService.findMetricByIds(integers);
        List<String> city_id = Arrays.asList("name", "email", "status");
        ConditionModel conditionModel = new ConditionModel();
        ConditionModel left = new ConditionModel();
        left.setHitValues(Arrays.asList("20221201"));
        left.setName("name");
        left.setSymbol(11);
        ConditionModel right = new ConditionModel();
        right.setHitValues(Arrays.asList("午餐", "晚餐"));
        right.setName("email");
        right.setSymbol(40);
        conditionModel.setLeft(left);
        conditionModel.setRight(right);
        conditionModel.setSymbol(100);
        List<SortModel> sortModelList = new ArrayList<>();
        SortModel sortModelA = SortModel.builder().name("name").asc(true).build();
        SortModel sortModelB = SortModel.builder().name("email").asc(false).build();
        sortModelList.add(sortModelA);
        sortModelList.add(sortModelB);
        String sql = explainService.expain2Sql(metricByIds, city_id, conditionModel, sortModelList);
        System.out.println(sql);
        assert sql != null;
    }
}