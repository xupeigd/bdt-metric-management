package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quicksand.bigdata.metric.management.identify.models.UserDetailsModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * MetricSegmentOverviewModel
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2023/2/27 14:58
 * @description
 */
@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class MetricSegmentVersionModel {

    @Schema(description = "主键id")
    Long id;


    @Schema(description = "版本内容md5值")
    String contentMd5;

    @Schema(description = "版本内容")
    String content;

    @Schema(description = "版本号")
    Integer version;

    @Schema(description = "更新人")
    UserDetailsModel updateUser;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;
}
