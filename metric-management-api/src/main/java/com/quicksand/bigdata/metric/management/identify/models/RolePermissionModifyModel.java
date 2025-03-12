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
public class RolePermissionModifyModel {

    @Data
    public static final class PermissionSelected {
        Integer id;
    }

    Integer type;
    Integer id;
    String name;
    String code;
    List<PermissionSelected> permissions;

}
