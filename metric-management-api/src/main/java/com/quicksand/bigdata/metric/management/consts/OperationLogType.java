package com.quicksand.bigdata.metric.management.consts;

import lombok.Getter;

/**
 * OperationLogType
 *
 * @author page
 * @date 2020/8/26 12:36
 */
@Getter
public enum OperationLogType {

    /**
     * 默认，未分类
     */
    DEFAULT(0),

    /**
     * 登陆
     */
    LOGIN(1),

    /**
     * F
     * 登出
     */
    LOGOUT(2),

    /**
     * 数据集-创建
     */
    DATASET_CREATE(3),

    /**
     * 数据集-修改
     */
    DATASET_MODIFY(4),

    /**
     * 数据集-删除
     */
    DATASET_DELETE(5),

    /**
     * 数据目录-创建
     */
    DATACATLOG_CREATE(6),

    /**
     * 数据目录-修改
     */
    DATACATLOG_MODIFY(7),

    /**
     * 数据目录-删除
     */
    DATACATLOG_DELETE(8),

    /**
     * Jwt登陆
     */
    JWT_LOGIN(9),

    /**
     * APP-创建
     */
    APP_CREATE(10),

    /**
     * APP-修改
     */
    APP_MODIFY(11),

    /**
     * APP更换token
     */
    APP_MODIFY_TOKEN(12),

    /**
     * APP-删除
     */
    APP_DELETE(13),

    /**
     * 越权操作
     */
    OVERSTEP_PERMISSION(14),

    /**
     * 调用申请
     */
    INVOKE_APPLY(15),

    /**
     * 调用申请-核准
     */
    INVOKE_APPLY_APPROVED(16),

    /**
     * 调用申请-拒绝
     */
    INVOKE_APPLY_REJECT(17),

    /**
     * 标签添加
     */
    LABEL_ADD(18),

    /**
     * 标签删除
     */
    LABEL_DELETE(19),

    /**
     * 标签指标绑定添加
     */
    LABEL_METRIC_REL_ADD(20),

    /**
     * 标签指标绑定解除
     */
    LABEL_METRIC_REL_DELETE(21),


    ;

    private int code;

    OperationLogType(int code) {
        this.code = code;
    }

}
