package com.quicksand.bigdata.metric.management.yaml.vos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

/**
 * DatasetSegmentTest
 *
 * @author page
 * @date 2022/8/3
 */
class DatasetSegmentTest {

    @Test
    public void datasetSegment() throws JsonProcessingException {
        DatasetSegment datasetSegment = DatasetSegment.builder()
                .name("transactions")
                .description("mock_transactions")
                .owners(Lists.newArrayList("page@quicksand.com", "page1@quicksand.com"))
                .sqlTable("transactions")
                .identifiers(
                        Lists.newArrayList(
                                DatasetSegment.Identifier.builder().name("id_transaction").type("primary").expr("id_transaction").build(),
                                DatasetSegment.Identifier.builder().name("id_order").type("foreign").expr("id_order").build(),
                                DatasetSegment.Identifier.builder().name("id_user").type("foreign").expr("UBSTRING(id_order from 2)").build()
                        ))
                .build();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.writeValueAsString(datasetSegment);
    }

}