package com.quicksand.bigdata.metric.management.yaml.vos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceSegment {
    DatasetSegment data_source;
}
