package com.quicksand.bigdata.metric.management.identify.utils;

import com.quicksand.bigdata.metric.management.identify.models.PermissionGatherModel;
import com.quicksand.bigdata.metric.management.identify.models.PermissionOverviewModel;
import com.quicksand.bigdata.metric.management.identify.vos.PermissionVO;
import com.quicksand.bigdata.vars.security.consts.PermissionType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PermissionModelAdapter
 *
 * @author page
 * @date 2020/8/18 17:55
 */
public final class PermissionModelAdapter {

    public static PermissionOverviewModel coverToOverviewModel(@NotNull PermissionVO vo) {
        PermissionOverviewModel pm = new PermissionOverviewModel();
        pm.setId(vo.getId());
        pm.setName(vo.getName());
        pm.setCode(vo.getCode());
        pm.setType(vo.getType().getName());
        pm.setPath(vo.getPath());
        pm.setModule(vo.getModule());
        pm.setSerial(vo.getSerial());
        boolean isTop = null == vo.getParentId() || 0 >= vo.getParentId();
        pm.setTop(isTop);
        return pm;
    }

    public static List<PermissionOverviewModel> coverPermissionChains(Collection<PermissionVO> permissionVos) {
        Map<Integer, PermissionOverviewModel> topPms = new HashMap<>();
        if (!CollectionUtils.isEmpty(permissionVos)) {
            // Map<Integer, Integer> id2ParentId = new HashMap<>();
            Map<Integer, PermissionOverviewModel> id2PM = new HashMap<>();
            for (PermissionVO vo : permissionVos) {
                PermissionOverviewModel pom = coverToOverviewModel(vo);
                //判断是否顶级权限
                if (pom.getTop()) {
                    if (!topPms.containsKey(vo.getId())) {
                        //儿子还没出现
                        topPms.put(pom.getId(), pom);
                    } else {
                        //儿子已经有个假的爸爸,找回儿子，再加入top中
                        PermissionOverviewModel curModel = topPms.get(vo.getId());
                        pom.getChildren().addAll(curModel.getChildren());
                        topPms.put(pom.getId(), pom);
                    }
                } else {
                    //不是顶级，当然是找爸爸啦
                    PermissionOverviewModel parentModel = topPms.getOrDefault(vo.getParentId(), new PermissionOverviewModel());
                    parentModel.setId(vo.getParentId());
                    topPms.put(vo.getParentId(), parentModel);
                    //加入爸爸的怀抱
                    parentModel.getChildren().add(pom);
                }
                id2PM.put(pom.getId(), pom);
            }
            //重新连接父子
            Iterator<Map.Entry<Integer, PermissionOverviewModel>> iterator = topPms.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, PermissionOverviewModel> next = iterator.next();
                if (StringUtils.isEmpty(next.getValue().getCode())) {
                    //假爸爸
                    PermissionOverviewModel cur = id2PM.get(next.getValue().getId());
                    if (null != cur) {
                        cur.getChildren().addAll(next.getValue().getChildren());
                        iterator.remove();
                    }
                    //无论如何都会干掉无法连接的节点数据
                    // iterator.remove();
                }
            }
        }
        return new ArrayList<>(topPms.values());
    }

    public static List<PermissionGatherModel> coverPermissionGatherModel(Collection<PermissionVO> permissionVOS) {
        List<PermissionVO> operationPermissions = permissionVOS.stream()
                .filter(k -> PermissionType.OPERATION.equals(k.getType()))
                .collect(Collectors.toList());
        Map<Integer, PermissionVO> id2Permission = permissionVOS.stream()
                .collect(Collectors.toMap(PermissionVO::getId, k -> k));
        List<PermissionGatherModel> gatherModels = new ArrayList<>();
        List<PermissionOverviewModel> tops = coverPermissionChains(operationPermissions);
        if (!CollectionUtils.isEmpty(tops)) {
            tops.forEach(k -> {
                PermissionGatherModel pm = new PermissionGatherModel();
                PermissionVO permissionVO = id2Permission.get(k.getId());
                if (null != permissionVO) {
                    pm.setGather(permissionVO.getCode());
                } else {
                    pm.setGather(String.valueOf(k.getId()));
                }
                pm.getChildren().addAll(k.getChildren());
                gatherModels.add(pm);
            });
        }
        return gatherModels;
    }

}
