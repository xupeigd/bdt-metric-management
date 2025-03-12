package com.quicksand.bigdata.metric.management.identify.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * UserPermissionModifyModel
 *
 * @author page
 * @date 2020/8/18 14:33
 */
@Data
public class UserPermissionModifyModel {

    /**
     * 授予权限的全量权限Id
     * <p>
     * + 表示明确授予
     * - 表示明确撤销
     */
    private List<Integer> permissions = new ArrayList<>();

}
