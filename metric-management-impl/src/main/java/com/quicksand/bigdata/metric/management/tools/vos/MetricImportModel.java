package com.quicksand.bigdata.metric.management.tools.vos;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * MetricImport
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/26 17:57
 * @description
 */
@Data
public class MetricImportModel implements IExcelVerifyModel {
    @Excel(name = "指标编码", orderNum = "1", width = 25)
    @ExcelIgnore
    String serialNumber;
    @Excel(name = "指标名称", orderNum = "2", width = 25)
    @NotBlank(message = "列值为空")
    String enName;
    @Excel(name = "指标别名", orderNum = "3", width = 25)
    String enAlias;
    @Excel(name = "指标中文名称", orderNum = "4", width = 25)
    @NotBlank(message = "列值为空")
    String cnName;
    @Excel(name = "指标中文别名", orderNum = "5", width = 25)
    String cnAlias;
    @Excel(name = "所在集群", orderNum = "6", width = 25)
    @ExcelIgnore
    String clusterName;
    @Excel(name = "所在模型", orderNum = "7", width = 25)
    @NotBlank(message = "列值为空")
    String tableName;
    @Excel(name = "度量字段名称", orderNum = "8", width = 25)
    @NotBlank(message = "列值为空")
    String measures;
    @Excel(name = "维度", orderNum = "9", width = 25)
    @NotBlank(message = "列值为空")
    String dimensions;
    //    @Excel(name = "指标加工逻辑", orderNum = "10", width = 25)
//    @NotBlank(message = "列值为空")
//    String processLogic;
    @Excel(name = "统计周期", orderNum = "11", width = 25)
    @NotBlank(message = "统计周期列值为空")
    String statisticPeriods;
    @Excel(name = "数据类型", orderNum = "12", width = 25)
    @NotBlank(message = "数据类型列值为空")
    String dataType;
    @Excel(name = "指标描述", orderNum = "13", width = 25)
    @NotBlank(message = "指标描述列值为空")
    String description;
    @Excel(name = "主题域", orderNum = "14", width = 25)
    @NotBlank(message = "主题域列值为空")
    String topicDomainName;
    @Excel(name = "业务域", orderNum = "15", width = 25)
    @NotBlank(message = "业务域列值为空")
    String businessDomainName;
    @Excel(name = "技术负责人", orderNum = "16", width = 25)
    @NotBlank(message = "技术负责人列值为空")
    String techOwners;
    @Excel(name = "业务负责人", orderNum = "17", width = 25)
    @NotBlank(message = "业务负责人列值为空")
    String businessOwners;
    @Excel(name = "指标等级", orderNum = "18", width = 25)
    @NotBlank(message = "指标类型列值为空")
    String metricLevel;
    @Excel(name = "指标安全等级", orderNum = "19", width = 25)
    @NotBlank(message = "指标安全等级列值为空")
    String dataSecurityLevel;
    @Excel(name = "指标时效", orderNum = "20", width = 25)
    @NotNull(message = "指标时效列值为空")
    String clusterType;
    @Excel(name = "指标类型", orderNum = "20", width = 25)
    @NotNull(message = "指标类型列值为空")
    String metricAggregationType;
    @Excel(name = "指标可累加", orderNum = "20", width = 25)
    @NotNull(message = "可累加列值为空")
    String metricAccumulative;
    @Excel(name = "指标已认证", orderNum = "20", width = 25)
    @NotNull(message = "已认证列值为空")
    String metricAuthentication;
    private String errorMsg;
    private Integer rowNum;

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public Integer getRowNum() {
        return this.rowNum;
    }

    @Override
    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}