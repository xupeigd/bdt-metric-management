package com.quicksand.bigdata.metric.management.datasource.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ColumnTypeModel
 *
 * @author page
 * @date 2022/8/24
 */
@Data
@Builder
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class ColumnTypeModel {

    /**
     * 类型
     * <p>
     * eg：varchar
     */
    @Schema(description = "")
    String type;

    /**
     * 详细类型
     * <p>
     * eg：varchar(255)
     */
    @Schema(description = "")
    String details;

}
