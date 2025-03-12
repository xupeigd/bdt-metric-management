package com.quicksand.bigdata.metric.management.apis.vos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quicksand.bigdata.metric.management.consts.AppType;
import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * AppVO
 *
 * @author page
 * @date 2022/10/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppVO {

    /**
     * 逻辑Id
     */
    Integer id;

    /**
     * 应用名称
     */
    String name;

    /**
     * 应用类型
     * <p>
     * 0内部应用 1外部应用
     */
    Integer type;

    /**
     * 描述
     */
    String description;

    /**
     * 负责人
     */
    UserVO owner;

    /**
     * 调用的token对象
     */
    AuthTokenVO token;

    /**
     * 申请关联的指标
     * (rels)
     */
    // List<RelationOfAppAndMetricVO> metrics;

    Integer createUserId;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    Integer updateUserId;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

    /**
     * 状态
     * 0删除 1 可用
     *
     * @see DataStatus
     */
    DataStatus status;

    /**
     * 应用类型(0:数据产品，1:业务产品)
     * 默认0
     */
    AppType appType;


}
