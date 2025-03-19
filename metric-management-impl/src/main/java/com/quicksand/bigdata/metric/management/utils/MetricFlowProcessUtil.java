package com.quicksand.bigdata.metric.management.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.quicksand.bigdata.metric.management.apis.models.ConditionModel;
import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import com.quicksand.bigdata.metric.management.yaml.vos.DimensionsSegment;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricMergeSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.quicksand.bigdata.metric.management.consts.YamlSegmentKeys.DEFAULT_TIME_DIM_DS;

/**
 * ProcessUtils
 *
 * @author zhaoxin3
 * @date 2022/8/9
 */
@Slf4j
public class MetricFlowProcessUtil {

    /**
     * 编译缓存
     */
    public static final LoadingCache<String, String> EXPLAIN_SQLS = CacheBuilder.newBuilder()
            .maximumSize(1000L)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    return null;
                }
            });
    private static final String TEMPLATE_EXPLAIN_SQL_COMPLeTE = "mf query --metrics %s --dimensions %s %s %s --explain";
    private static final String TEMPLATE_EXPLAIN_SQL = "mf query --metrics %s --dimensions %s  --explain";

    public static String getMetricFlowParseSql(MetricMergeSegment mergeSegment) {
        return getMetricFlowParseSql(mergeSegment.getMetric().getName(),
                mergeSegment.getDimensions().stream()
                        .map(DimensionsSegment.Dimension::getName)
                        .filter(f -> !Objects.equals(DEFAULT_TIME_DIM_DS, f))
                        .collect(Collectors.toList()));
    }

    public static String getMetricFlowParseSql(String metricName, List<String> dimensions) {
        return getExeCmdResult(String.format(TEMPLATE_EXPLAIN_SQL, metricName, StringUtils.collectionToCommaDelimitedString(dimensions)));
    }

    public static String getExplainSql(List<MetricVO> metricVOS, List<String> metricNames, List<String> dimensions, ConditionModel condition, List<SortModel> sorts) {
        String executeCommand = String.format(TEMPLATE_EXPLAIN_SQL_COMPLeTE, StringUtils.collectionToCommaDelimitedString(metricNames),
                StringUtils.collectionToCommaDelimitedString(dimensions), (null == condition ? "" : String.format("--where \"%s\"", ConditionModel.toCmdSegement(condition))),
                (CollectionUtils.isEmpty(sorts) ? "" : String.format("--order %s",
                        StringUtils.collectionToCommaDelimitedString(sorts.stream()
                                .map(v -> v.getAsc() ? v.getName() : String.format("-%s", v.getName()))
                                .collect(Collectors.toList())))));
        String commandMd5 = DigestUtils.md5DigestAsHex(executeCommand.getBytes(StandardCharsets.UTF_8));
        String explainCacheKey = String.format("%s_%s", StringUtils.collectionToCommaDelimitedString(metricVOS.stream().map(MetricVO::getId).collect(Collectors.toList())), commandMd5);
        String cacheResult = EXPLAIN_SQLS.getIfPresent(explainCacheKey);
        if (StringUtils.hasText(cacheResult)) {
            return cacheResult;
        }
        String cmdResult = getExeCmdResult(executeCommand);
        if (StringUtils.hasText(cmdResult)) {
            if (cmdResult.contains(" \nERR")) {
                cmdResult = cmdResult.substring(cmdResult.indexOf(" \nERR"));
            } else if (cmdResult.contains(" \nSELECT")) {
                cmdResult = cmdResult.substring(cmdResult.indexOf(" \nSELECT"));
                EXPLAIN_SQLS.put(explainCacheKey, cmdResult);
            }
        }
        return cmdResult;
    }


    public static String getExeCmdResult(String exeCmd) {
        log.info("---explainCmd=" + exeCmd);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("process start");
        BufferedReader reader = null;
        BufferedReader erroReader = null;
        Process process = null;
        StringBuilder stringBuilder = new StringBuilder(200);
        try {
            process = new ProcessBuilder("/bin/bash", "-c", exeCmd).redirectErrorStream(true).start();
            stopWatch.stop();
            stopWatch.start("process wait reader");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    stopWatch.stop();
                    stopWatch.start("process line end");
                    break;
                }
                stringBuilder.append(" \n").append(line);
            }
            stopWatch.stop();
            stopWatch.start("process done");
            stopWatch.stop();
            log.info("getExeCmdResult timeline：{}", StopWatchUtils.getAllTaskInfo(stopWatch));
            //
            String cmdResult = stringBuilder.toString();
            log.debug("getExeCmdResult result:{}", cmdResult);
            return cmdResult;
        } catch (IOException e) {
            log.error("---getMetricFlowParseSql 执行失败！", e);
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("shell =" + exeCmd + " 运行shell命令异常", e);
                }
            }
        }
    }

}
