package com.quicksand.bigdata.metric.management.datasource.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * YamlViewModel
 *
 * @author page
 * @date 2022/8/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YamlViewModel {

    List<String> lines;

    String yamlSegment;

}
