package com.quicksand.bigdata.metric.management.utils;

import lombok.Data;

import java.util.List;

/**
 * yamlModel
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/9/15 14:07
 * @description
 */
@Data
public class YamlModel {
    String name;
    String desc;
    List<String> list;
    YamlModel child;
}
