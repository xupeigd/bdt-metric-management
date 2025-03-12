package com.quicksand.bigdata.metric.management.identify.rests;


import com.quicksand.bigdata.metric.management.consts.RoleType;
import com.quicksand.bigdata.metric.management.identify.models.RoleOverviewModel;
import com.quicksand.bigdata.metric.management.identify.services.RoleService;
import com.quicksand.bigdata.metric.management.identify.utils.RoleModelAdapter;
import com.quicksand.bigdata.vars.http.model.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RoleController
 *
 * @Author page
 * @Date 2020/8/18 16:33
 */
@RestController
@Tag(name = "角色Apis")
public class RoleController
        implements RoleRestService {

    @Resource
    RoleService roleService;

    @PreAuthorize("hasAuthority('OP_ROLE_LIST')")
    @Override
    public Response<List<RoleOverviewModel>> getRoles() {
        return Response.ok(roleService.queryAllRoles()
                .stream()
                .filter(k -> 0 < k.getId())
                .filter(v -> !Objects.equals(RoleType.PERSON, v.getType()))
                .map(RoleModelAdapter::cover2OverviewModel)
                .collect(Collectors.toList())
        );
    }

}
