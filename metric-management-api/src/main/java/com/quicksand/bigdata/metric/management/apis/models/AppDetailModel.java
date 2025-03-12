package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicksand.bigdata.metric.management.consts.AppType;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import com.quicksand.bigdata.metric.management.metric.models.MetricOverviewModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * AppDetailModel
 *
 * @author page
 * @date 2022/9/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "应用信息详情")
public class AppDetailModel {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    UserOverviewModel owner;

    @Schema(description = "token")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String token;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

    @Schema(description = "指标")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<MetricOverviewModel> metrics;
    /**
     * 应用类型(0:数据产品，1:业务产品)
     * 默认0
     */
    @Schema(description = "应用类型 (0:数据产品，1:业务产品)")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    AppType appType;

    @Schema(description = "指标个数")
    @JsonProperty
    public int metricsCount() {
        return CollectionUtils.isEmpty(metrics) ? 0 : metrics.size();
    }

    @JsonProperty
    public int appType() {
        return null == appType ? 0 : appType.getCode();
    }

}
