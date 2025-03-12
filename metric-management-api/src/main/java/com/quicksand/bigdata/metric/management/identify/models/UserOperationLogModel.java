package com.quicksand.bigdata.metric.management.identify.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * UserOperationLogModel
 *
 * @author page
 * @date 2020/8/18 14:59
 */
@Data
public class UserOperationLogModel {

    /**
     * 日志的id
     */
    Long id;
    /**
     * 操作时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date operateTime;
    /**
     * 操作ip
     */
    String ip;
    /**
     * 操作地区
     */
    String address;
    /**
     * 操作用户名
     */
    String userName;
    /**
     * 日志详情
     */
    String detail;
    /**
     * 操作的用户
     */
    Integer userId;

}
