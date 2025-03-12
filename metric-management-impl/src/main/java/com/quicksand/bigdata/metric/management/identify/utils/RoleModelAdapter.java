package com.quicksand.bigdata.metric.management.identify.utils;

import com.quicksand.bigdata.metric.management.identify.models.RoleOverviewModel;
import com.quicksand.bigdata.metric.management.identify.vos.RoleVO;

import javax.validation.constraints.NotNull;

/**
 * RoleModelAdapter
 *
 * @author page
 * @date 2020/8/20 11:09
 */
public final class RoleModelAdapter {

    public static RoleOverviewModel cover2OverviewModel(@NotNull RoleVO roleVO) {
        RoleOverviewModel rom = new RoleOverviewModel();
        rom.setId(roleVO.getId());
        rom.setName(roleVO.getName());
        rom.setCode(roleVO.getCode());
        rom.setType(roleVO.getType());
        return rom;
    }

}
