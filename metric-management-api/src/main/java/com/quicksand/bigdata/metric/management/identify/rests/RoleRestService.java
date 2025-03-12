package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.RoleOverviewModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * RoleRestService
 *
 * @author page
 * @date 2020/8/18
 */
@FeignClient(
        name = "${vars.name.ms.identify:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.RoleRestService:}",
        contextId = "RoleRestService")
public interface RoleRestService {

    @GetMapping("/identify/roles")
    Response<List<RoleOverviewModel>> getRoles();

}
