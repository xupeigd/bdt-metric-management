package com.quicksand.bigdata.metric.management.identify.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionGatherModel
 *
 * @author page
 * @date 2020/8/18 14:53
 */
@Data
public class PermissionGatherModel {

    String gather;

    List<PermissionOverviewModel> children = new ArrayList<>();

}
