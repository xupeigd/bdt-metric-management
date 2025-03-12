package com.quicksand.bigdata.metric.management.identify.models;

import com.quicksand.bigdata.vars.security.consts.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionModel {

    Integer id;

    String name;

    String code;

    Integer parentId;

    Integer serial;

    PermissionType type;

}
