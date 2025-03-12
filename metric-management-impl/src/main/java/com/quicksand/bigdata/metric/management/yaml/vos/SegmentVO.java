package com.quicksand.bigdata.metric.management.yaml.vos;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.YamlSegmentType;
import com.quicksand.bigdata.metric.management.datasource.vos.DatasetVO;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.metric.vos.MetricVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SegmentVO
 *
 * @author page
 * @date 2022/8/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentVO {

    Long id;

    YamlSegmentType type;

    /**
     * 关联的数据集
     */
    DatasetVO dataset;

    /**
     * 关联的指标
     */
    MetricVO metric;

    /**
     * 片段内容的Md5
     */
    String contentMd5;

    /**
     * 片段内容
     */
    String content;

    /**
     * 版本
     */
    Integer version;

    Integer createUserId;

    /**
     * 创建时间
     */
    Date createTime;

    UserDBVO updateUser;

    /**
     * 更新时间
     */
    Date updateTime;

    /**
     * 状态
     * 0 删除 1 可用
     *
     * @see DataStatus
     */
    DataStatus status;

}
