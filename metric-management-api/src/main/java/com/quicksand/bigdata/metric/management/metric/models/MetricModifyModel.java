package com.quicksand.bigdata.metric.management.metric.models;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.StatisticPeriod;
import com.quicksand.bigdata.metric.management.consts.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.List;

/**
 * MetricModifyModel
 *
 * @author page
 * @date 2022/7/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "指标构建实体")
public class MetricModifyModel {

    @Schema(description = "指标中文名称")
    @Length(min = 1, max = 256, message = "名称长度必须在1～256之间", groups = {Insert.class, Update.class})
    @NotBlank(message = "中文名称不能为空", groups = {Insert.class, Update.class})
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\-\\._&~·|【】（）#/+￥$《》；：‘’“”]{1,256}$",
            message = "请勿使用不支持的字符，中文名称仅支持中文及特许符号 ._&~·|【】（）#/+￥$《》；：‘’“” ",
            groups = {Insert.class, Update.class})
    String cnName;

    @Schema(description = "指标认证信息")
    @Length(min = 1, max = 256, message = "名称长度必须在1～256之间", groups = {Insert.class, Update.class})
    @NotBlank(message = "名称不能为空", groups = {Insert.class, Update.class})
    @Pattern(regexp = "(?!.*__).*^[a-z][a-z0-9_]*[a-z0-9]$",
            message = "名称仅支持小写字母，数字与_ ,可参考正则 (?!.*__).*^[a-z][a-z0-9_]*[a-z0-9]$",
            groups = {Insert.class, Update.class})
    String enName;

    /**
     * 编码
     */
    @Schema(description = "指标编码")
    @Length(min = 4, max = 32, message = "编号长度必须在4～32之间", groups = {Insert.class, Update.class})
    @NotBlank(message = "编号不能为空", groups = {Insert.class, Update.class})
    @Pattern(regexp = "^[A-z0-9]{4,32}$",
            message = "编号仅支持字母，数字与_",
            groups = {Insert.class, Update.class})
    String serialNumber;

    /**
     * 中文别名
     */
    @Schema(description = "指标中文别名")
    @Length(min = 1, max = 256, message = "中文别名长度必须在1～256之间", groups = {Insert.class, Update.class})
    //@NotBlank(message = "中文别名不能为空", groups = {Insert.class, Update.class})
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\-\\._&~·|【】（）#/+￥$《》；：‘’“”]{1,256}$",
            message = "请勿使用不支持的字符，中文别名仅支持中文及特许符号 ._&~·|【】（）#/+￥$《》；：‘’“” ",
            groups = {Insert.class, Update.class})
    String cnAlias;

    /**
     * 英文别名
     */
    @Schema(description = "指标英文别名")
    @Length(min = 1, max = 256, message = "英文别名必须在1～256之间", groups = {Insert.class, Update.class})
    //@NotBlank(message = "英文别名不能为空", groups = {Insert.class, Update.class})
    @Pattern(regexp = "^[a-zA-z0-9_]{1,256}$",
            message = "英文别名仅支持字母，数字与_",
            groups = {Insert.class, Update.class})
    String enAlias;

    /**
     * 技术负责人
     */
    @Schema(description = "指标技术负责人")
    @NotNull(message = "缺少参数：技术负责人", groups = {Insert.class, Update.class})
    @NotEmpty(message = "缺少参数：技术负责人", groups = {Insert.class, Update.class})
    List<Integer> techOwners;

    /**
     * 业务负责人
     */
    @Schema(description = "指标业务负责人")
    @NotNull(message = "缺少参数：业务负责人", groups = {Insert.class, Update.class})
    @NotEmpty(message = "缺少参数：业务负责人", groups = {Insert.class, Update.class})
    List<Integer> businessOwners;

    /**
     * 描述
     */
    @Schema(description = "指标描述")
    @NotBlank(message = "非空参数：description", groups = {Insert.class, Update.class})
    String description;

//    /**
//     * 加工逻辑
//     */
//    @Schema(description = "")
//    @NotBlank(message = "非空参数：processLogic", groups = {Insert.class, Update.class})
//    String processLogic;

    /**
     * 指标等级
     */
    @Schema(description = "指标等级")
    @Min(value = 0L, message = "指标等级在0-2范围内", groups = {Insert.class, Update.class})
    @Max(value = 2L, message = "指标等级在0-2范围内", groups = {Insert.class, Update.class})
    @NotNull(message = "指标等级必须提供", groups = {Insert.class, Update.class})
    Integer metricLevel;

    @Schema(description = "指标认证信息")
    @Min(value = 0L, message = "数据安全等级在0-3范围内", groups = {Insert.class, Update.class})
    @Max(value = 3L, message = "数据安全等级在0-3范围内", groups = {Insert.class, Update.class})
    @NotNull(message = "数据安全等级必须提供", groups = {Insert.class, Update.class})
    Integer dataSecurityLevel;

    @Schema(description = "指标认证信息")
    @Length(min = 1, max = 24, message = "数据返回类型长度必须在1～24之间", groups = {Insert.class, Update.class})
    String dataType;

    /**
     * 统计周期
     * <p>
     * 年/月/日/周/季
     */
    @Schema(description = "指标统计周期")
    @NotNull(message = "数据安全等级必须提供", groups = {Insert.class, Update.class})
    @NotEmpty(message = "指标统计周期需要提供", groups = {Insert.class, Update.class})
    List<StatisticPeriod> statisticPeriods;


    @Schema(description = "指标主题域id")
    @NotNull(message = "非空参数：topicDomainId", groups = {Insert.class, Update.class})
    @Min(value = 1L, message = "不存在的主题域", groups = {Insert.class, Update.class})
    Integer topicDomainId;

    @Schema(description = "指标业务域id")
    @NotNull(message = "非空参数：businessDomainId", groups = {Insert.class, Update.class})
    @Min(value = 1L, message = "不存在的业务域", groups = {Insert.class, Update.class})
    Integer businessDomainId;

    /**
     * 指标所在数据集id
     */
    @Schema(description = "指标所在数据集id")
    @NotNull(message = "非空参数：datasetId", groups = {Insert.class, Update.class})
    @Min(value = 1L, message = "不存在的数据集id", groups = {Insert.class, Update.class})
    Integer datasetId;

    /**
     * 指标时效
     */
    @Schema(description = "指标时效")
    @NotNull(message = "非空参数：clusterType", groups = {Insert.class, Update.class})
    @Min(value = 0L, message = "指标时效在0-1范围内", groups = {Insert.class, Update.class})
    @Max(value = 1L, message = "指标时效在0-1范围内", groups = {Insert.class, Update.class})
    Integer clusterType;

    @Schema(description = "指标聚合类型(0:原子指标，1:衍生指标，2:复合指标)")
    @Min(value = 0L, message = "指标聚合类型在0-2范围内", groups = {Insert.class, Update.class})
    @Max(value = 2L, message = "指标聚合类型在0-2范围内", groups = {Insert.class, Update.class})
    @NotNull(message = "指标聚合类型必须提供", groups = {Insert.class, Update.class})
    Integer metricAggregationType;

    @Schema(description = "指标是否可累加(0:否，1：是)")
    @Min(value = 0L, message = "指标是否可累加信息在0-1范围内", groups = {Insert.class, Update.class})
    @Max(value = 1L, message = "指标是否可累加信息在0-1范围内", groups = {Insert.class, Update.class})
    @NotNull(message = "指标是否可累加信息必须提供", groups = {Insert.class, Update.class})
    Integer metricAccumulative;

    @Schema(description = "指标是否认证(0:否，1：是)")
    @Min(value = 0L, message = "指标认证信息在0-1范围内", groups = {Insert.class, Update.class})
    @Max(value = 1L, message = "指标认证信息在0-1范围内", groups = {Insert.class, Update.class})
    @NotNull(message = "指标认证信息必须提供", groups = {Insert.class, Update.class})
    Integer metricAuthentication;

}
