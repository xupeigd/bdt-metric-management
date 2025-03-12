package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quicksand.bigdata.metric.management.apis.models.SortModel;
import com.quicksand.bigdata.metric.management.consts.Insert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * MetricQueryModel
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/24 17:00
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "指标查询参数")
public class MetricQueryModel {

    @Schema(description = "指标关键字")
    String metricKeyword;

    @Schema(description = "数据集关键字")
    String datasetKeyword;

    @Schema(description = "指标id")
    @Min(value = 1L, message = "不存在的应用id", groups = {Insert.class})
    Integer appId;

    @Schema(description = "数据集id列表")
    List<Integer> datasetIds;

    @Schema(description = "业务负责人列表")
    List<Integer> businessOwners;

    @Schema(description = "技术负责人列表")
    List<Integer> techOwners;

    @Schema(description = "是否发布(0:下线，1:上线) 可多选")
    List<Integer> pubsubs;

    @Schema(description = "是否发布(0:下线，1:上线)")
    @JsonIgnore
    @Min(value = 0, message = "异常的上线状态", groups = {Insert.class})
    @Max(value = 1, message = "异常的上线状态", groups = {Insert.class})
    Integer pubsub;

    @Schema(description = "当前分页数", defaultValue = "1")
    @Min(value = 1, message = "当前页数为大于0的整数", groups = {Insert.class})
    Integer pageNo = 1;

    @Min(value = 1, message = "分页大小为大于0的整数", groups = {Insert.class})
    @Schema(description = "每页条数", defaultValue = "20")
    Integer pageSize = 20;

    @Schema(description = "主题域id列表")
    List<Integer> topicIds;

    @Schema(description = "业务域id列表")
    List<Integer> businessIds;

    @Schema(description = "指标实效(0:离线，1:实时),可多选")
    List<Integer> clusterTypes;

    @Schema
    @Min(value = 0, message = "异常的实效状态", groups = {Insert.class})
    @Max(value = 1, message = "异常的实效状态", groups = {Insert.class})
    @JsonIgnore
    Integer clusterType;

    @Schema(description = "排序字段,可多个，排序字段：cnName(指标中文名)，enName(指标英文名)，serialNumber(指标编码)")
    List<SortModel> sorts;

}
