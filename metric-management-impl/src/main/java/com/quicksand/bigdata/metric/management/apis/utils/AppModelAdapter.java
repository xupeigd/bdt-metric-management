package com.quicksand.bigdata.metric.management.apis.utils;

import com.quicksand.bigdata.metric.management.apis.models.AppDetailModel;
import com.quicksand.bigdata.metric.management.apis.models.AppModel;
import com.quicksand.bigdata.metric.management.apis.vos.AppVO;
import com.quicksand.bigdata.metric.management.identify.utils.UserModelAdapter;

/**
 * AppModelAdapter
 *
 * @author page
 * @date 2022/10/12
 */
public final class AppModelAdapter {

    private AppModelAdapter() {
    }

    public static AppDetailModel cover2DetailModel(AppVO vo) {
        return AppDetailModel.builder()
                .id(vo.getId())
                .name(vo.getName())
                .description(vo.getDescription())
                .owner(null == vo.getOwner() ? null : UserModelAdapter.cover2OverviewModel(vo.getOwner()))
                // .metrics(CollectionUtils.isEmpty(vo.getMetrics())
                //         ? Collections.emptyList()
                //         : vo.getMetrics().stream()
                //         .filter(Objects::nonNull)
                //         .filter(v -> null != v.getMetric())
                //         .map(v -> JsonUtils.transfrom(v.getMetric(), MetricOverviewModel.class))
                //         .collect(Collectors.toList()))
                .appType(vo.getAppType())
                .createTime(vo.getCreateTime())
                .updateTime(vo.getUpdateTime())
                .token(null == vo.getToken() ? "" : vo.getToken().getToken())
                .build();
    }

    public static AppModel cover2Model(AppVO vo) {
        return AppModel.builder()
                .id(vo.getId())
                .name(vo.getName())
                .description(vo.getDescription())
                .createTime(vo.getCreateTime())
                .owner(UserModelAdapter.cover2OverviewModel(vo.getOwner()))
                .appType(vo.getAppType())
                // .metrics(CollectionUtils.isEmpty(vo.getMetrics())
                //         ? Collections.emptyList()
                //         : vo.getMetrics().stream()
                //         .filter(Objects::nonNull)
                //         .filter(v -> null != v.getMetric())
                //         .map(v -> JsonUtils.transfrom(v.getMetric(), MetricOverviewModel.class))
                //         .collect(Collectors.toList()))
                .build();
    }

}
