package com.quicksand.bigdata.metric.management.utils;

import org.springframework.util.StopWatch;

/**
 * StopWatchUtils
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/9/8 10:27
 * @description
 */
public class StopWatchUtils {

    public static String getAllTaskInfo(StopWatch stopWatch) {
        if (stopWatch == null) {
            return null;
        }
        StopWatch.TaskInfo[] taskInfos = stopWatch.getTaskInfo();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < taskInfos.length; i++) {
            StopWatch.TaskInfo taskInfo = taskInfos[i];
            sb.append("\n步骤").append(i).append(":名称=").append(taskInfo.getTaskName()).append(",用时=").append(taskInfo.getTimeMillis()).append("ms");
        }
        return sb.toString();
    }
}
