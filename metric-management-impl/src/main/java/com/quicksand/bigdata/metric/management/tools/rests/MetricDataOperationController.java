package com.quicksand.bigdata.metric.management.tools.rests;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.quicksand.bigdata.metric.management.metric.services.MetricService;
import com.quicksand.bigdata.metric.management.tools.services.AutoImportService;
import com.quicksand.bigdata.metric.management.tools.vos.FailedModel;
import com.quicksand.bigdata.metric.management.tools.vos.ImportResult;
import com.quicksand.bigdata.metric.management.tools.vos.MetricImportModel;
import com.quicksand.bigdata.metric.management.yaml.services.ExplainService;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MetricDataOperaterController
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/8/30 15:22
 * @description
 */
@Slf4j
@RestController
@Tag(name = "指标批量操作Apis")
public class MetricDataOperationController {
    @Resource
    AutoImportService autoImportService;
    @Resource(name = "ExplainSpliceServiceImpl")
    ExplainService explainService;
    @Resource
    MetricService metricService;

    @PostMapping("/metric/import")
    @Operation(description = "批量导入指标")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE')")

    public Response<ImportResult> dataImport(@RequestBody @Validated MultipartFile file, HttpServletRequest request) throws Exception {
        if (ObjectUtils.isEmpty(file) || file.getSize() == 0) {
            return Response.ok("传入文件异常");
        }
        log.info("接收到文件：{}", file.getOriginalFilename());
        // 参数1：文件流
        InputStream stream = null;
        try {
            stream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 参数2：导入类型
        ImportParams params = new ImportParams();

        // 标题占用多少行
        //params.setTitleRows(1);

        // 头部属性占用多少行
        //params.setHeadRows(1);

        // 从指定的sheet的下标开始读取
        // params.setStartSheetIndex(1);

        // 读取sheet的数量，需要和上面的配合
        // params.setSheetNum(1);

        // 对Excel进行必要合法列名进行校验
        params.setNeedVerify(true);
        params.setImportFields(new String[]{"指标名称", "指标别名", "指标中文名称", "指标中文别名", "所在模型", "度量字段名称", "维度", "统计周期", "数据类型", "指标描述", "主题域", "业务域", "技术负责人", "业务负责人", "指标等级", "指标安全等级", "指标时效", "指标类型", "指标可累加", "指标已认证"});
        ExcelImportResult<MetricImportModel> excelImportResult;
        try {
            excelImportResult = ExcelImportUtil.importExcelMore(stream, MetricImportModel.class, params);
        } catch (Exception e) {
            throw new ValidationException("excel格式不正确，请确认必要列名存在：" + Arrays.toString(params.getImportFields()));
        }

        List<FailedModel> failedModelList = new ArrayList<>();
        ImportResult importResult = ImportResult.builder()
                .failedCount(0)
                .successCount(0)
                .failedList(failedModelList)
                .build();
        if (excelImportResult.isVerifyFail()) {
            for (MetricImportModel failedMetricImportModel : excelImportResult.getFailList()) {
                FailedModel failedModel = FailedModel.builder()
                        .rowNumber(failedMetricImportModel.getRowNum())
                        .cnName(failedMetricImportModel.getCnName())
                        .tableName(failedMetricImportModel.getTableName())
                        .reason(failedMetricImportModel.getErrorMsg())
                        .build();
                failedModelList.add(failedModel);
            }
            importResult.setFailedList(failedModelList);
            importResult.setFailedCount(failedModelList.size());
        }
        // 遍历结果，插入到数据库
        log.info("正确导入数据：{}", excelImportResult.getList());
        if (CollectionUtils.isEmpty(excelImportResult.getList())) {
            return Response.ok(importResult);
        }
        ImportResult appendImportResult = autoImportService.ImportMetricDataByList(excelImportResult.getList());
        if (appendImportResult != null) {
            importResult.setSuccessCount(appendImportResult.getSuccessCount());
            importResult.setFailedCount(importResult.getFailedCount() + appendImportResult.getFailedList().size());
            importResult.getFailedList().addAll(appendImportResult.getFailedList());
        }
        return Response.ok(importResult);
    }

    @GetMapping("/metric/download")
    @Operation(description = "批量导入模版下载")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "未授权的访问！"),
            @ApiResponse(responseCode = "404", description = "not found "),
            @ApiResponse(responseCode = "425", description = "未实现! "),
    })
    @PreAuthorize("hasAuthority('OP_METRICS_CREATE')")

    public void templateDownLoad(ModelMap map, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        List<MetricImportModel> list = new ArrayList<MetricImportModel>();

        MetricImportModel example = new MetricImportModel();
        example.setCnAlias("指标中文别名");
        example.setCnName("指标有效中文名");
        example.setMeasures("指标度量字段名称(取表中字段或者字段组合)");
        example.setEnName("指标有效英文名");
        example.setEnAlias("指标英文别名");
        example.setTechOwners("指标技术负责人完整邮箱");
        example.setBusinessOwners("指标业务负责人完整邮箱 ");
        example.setDescription("指标的描述");
//        client.setProcessLogic("select* from t1");
        example.setMetricLevel("指标等级,取值范围(T1、T2、T3)");
        example.setDataSecurityLevel("数据安全等级,取值范围(L1、L2、L3、L4)");
        example.setDataType("指标值类型(如bigint、string等)");
        example.setStatisticPeriods("指标类型，取值范围(日、月、年、周、季)");
        example.setTopicDomainName("指标主题域名称,必须是已存在的主题域,没有的话需要先添加");
        example.setBusinessDomainName("指标业务域名称,必须是已存在的业务域,且在主题域下,没有的话需要先添加");
        example.setDimensions("指标维度字段，多个字段用逗号(,)分割");
        example.setTableName("指标数据模型表名");
        example.setClusterType("指标实效性(离线、实时)");
        example.setMetricAggregationType("指标类型，取值范围(原子指标、衍生指标、复合指标)");
        example.setMetricAccumulative("指标是否可累加,取值范围(是,否)");
        example.setMetricAuthentication("指标是已认证,取值范围(是,否)");
        list.add(example);
        MetricImportModel range = new MetricImportModel();
        range.setCnAlias("团膳有效订单量");
        range.setCnName("团膳有效订单量");
        range.setMeasures("supply_groupmeal_order_cnt");
        range.setEnName("supply_shopmall_groupmeal_order_cnt");
        range.setEnAlias("supply_shopmall_groupmeal_order_cnt");
        range.setTechOwners("lipeng.quicksand.com");
        range.setBusinessOwners("lipeng.quicksand.com");
        range.setDescription("团膳有效订单量，限制订单状态为2,3,6的状态");
//        client.setProcessLogic("select* from t1");
        range.setMetricLevel("T2");
        range.setDataSecurityLevel("L2");
        range.setDataType("bigint");
        range.setStatisticPeriods("日");
        range.setTopicDomainName("供应链");
        range.setBusinessDomainName("商城线-团膳");
        range.setDimensions("group_id,org_id,shop_id");
        range.setTableName("aggr_supply_shopmall_order_shop_source_day");
        range.setClusterType("离线");
        range.setMetricAggregationType("原子指标");
        range.setMetricAccumulative("是");
        range.setMetricAuthentication("否");
        list.add(range);
//        ExportParams params = new ExportParams();
//        params.setCreateHeadRows(true);
//        params.setFreezeCol(2);
//        map.put(NormalExcelConstants.DATA_LIST, list);
//        map.put(NormalExcelConstants.CLASS, MetricImportModel.class);
//        map.put(NormalExcelConstants.PARAMS, params);
//        response.setHeader("Content-Disposition", "attachment;filename=指标导入模版.xls");
//        PoiBaseView.render(map, request, response, NormalExcelConstants.EASYPOI_EXCEL_VIEW);
        //指定列表标题和工作表名称
        ExportParams params = new ExportParams();
        Workbook workbook = ExcelExportUtil.exportExcel(params, MetricImportModel.class, list);
        response.setHeader("content-Type", "application/vnd.ms-excel");
        String filename = java.net.URLEncoder.encode("指标导入模版.xls", "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);
        response.setCharacterEncoding("UTF-8");
        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
