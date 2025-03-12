package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.UserOperationLogModel;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * LogRestService
 *
 * @author page
 * @date 2020/8/18 14:56
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.LogRestService:}",
        contextId = "LogRestService")
public interface LogRestService {

    /**
     * 分页查询日志
     *
     * @param pageNo          页码 基于1
     * @param pageSize        页容量 默认20
     * @param operationUserId 操作的用户Id
     * @return PageModel of UserOperationLogModel
     */
    @GetMapping("/identify/maintain/logs")
    Response<PageImpl<UserOperationLogModel>> queryLogs(@RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                        @RequestParam(name = "operationUserId", required = false) Integer operationUserId);

}
