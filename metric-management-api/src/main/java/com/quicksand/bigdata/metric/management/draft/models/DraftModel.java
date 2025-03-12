package com.quicksand.bigdata.metric.management.draft.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.DraftType;
import com.quicksand.bigdata.metric.management.identify.models.UserOverviewModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DraftModel
 *
 * @author page
 * @date 2022-08-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftModel {


    /**
     * 草稿主键
     */
    Integer id;

    /**
     * 识别flag
     * (模块自定义)
     */
    String flag;

    /**
     * 草稿类型
     * <p>
     * 0 dataset 1 metric
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    DraftType type;

    String content;

    /**
     * 创建用户
     */
    @JsonIgnore
    UserOverviewModel user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

}
