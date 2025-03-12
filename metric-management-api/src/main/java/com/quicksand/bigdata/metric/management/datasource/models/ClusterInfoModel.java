package com.quicksand.bigdata.metric.management.datasource.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * ClusterInfoModel
 * (集群信息)
 *
 * @author page
 * @date 2022/7/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterInfoModel {

    Integer id;

    String name;

    String type;

    String address;

    String defaultDatabase;

    String defaultSchema;

    String userName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String password;

    String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

}
