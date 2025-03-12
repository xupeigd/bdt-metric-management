package com.quicksand.bigdata.metric.management.identify.utils;


import com.quicksand.bigdata.metric.management.identify.models.MenuModel;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;

import javax.validation.constraints.NotNull;

public final class MenuModelAdapter {

    public static MenuModel coverToOverviewModel(@NotNull PermissionVO vo) {
        MenuModel pm = new MenuModel();
        pm.setId(vo.getId());
        pm.setName(vo.getName());
        pm.setPath(vo.getPath());
        pm.setParentId(vo.getParentId());
        pm.setSerial(vo.getSerial());
        return pm;
    }

}
