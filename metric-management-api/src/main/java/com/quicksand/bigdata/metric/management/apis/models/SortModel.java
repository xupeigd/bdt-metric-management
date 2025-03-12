package com.quicksand.bigdata.metric.management.apis.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SortModel
 *
 * @author page
 * @date 2022/10/19
 */
@Schema(name = "排序Model")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortModel {

    /**
     * 是否升序
     */
    @Schema(description = "是否升序")
    Boolean asc;

    /**
     * 名称
     */
    @Schema(description = "排序字段名称")
    String name;

}
