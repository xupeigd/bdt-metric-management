package com.quicksand.bigdata.metric.management.identify.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuModel {

    Integer id;

    String name;

    String icon;

    String path;

    Integer serial;

    Integer parentId;

    List<MenuModel> children;

}
