package com.quicksand.bigdata.metric.management.consts;

/**
 * YamlSegmentKeys
 *
 * @author zhaoxin3
 * @date 2022/8/9
 */
public class YamlSegmentKeys {

    /**
     * 数据源片段标识
     */
    public static final String DATASOURCE = "data_source";


    /**
     * 度量片段标识
     */
    public static final String MEASURES = "measures";

    /**
     * 维度片段标识
     */
    public static final String DIMENSIONS = "dimensions";

    /**
     * 指标片段标识
     */
    public static final String METRIC = "metric";

    /**
     * 片段分割标识
     */
    public static final String SEGMENT_SEPARATOR = "---\n";

    public static final String DEFAULT_TIME_DIM_DS = "default_time_dim_ds";

    public static final String YAML_NAME_RULE = "(?!.*__).*^[a-z][a-z0-9_]*[a-z0-9]$";

}