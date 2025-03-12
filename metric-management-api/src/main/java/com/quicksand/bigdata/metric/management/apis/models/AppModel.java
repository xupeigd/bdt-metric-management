package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicksand.bigdata.metric.management.consts.AppType;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * AppModel
 *
 * @author page
 * @date 2022/9/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppModel {

    /**
     * 主键
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    /**
     * 名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 创建人
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    UserOverviewModel owner;

    /**
     * 描述
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;

    /**
     * 创建时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer metricsCount;


    /**
     * 应用类型(0:数据产品，1:业务产品)
     * 默认0
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    AppType appType;

    @JsonProperty
    public int appType() {
        return null == appType ? 0 : appType.getCode();
    }

}
