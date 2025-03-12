package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DropDownListModel
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/11/15 17:37
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropDownListModel {

    /**
     * id
     */
    @Schema(description = "")
    Long id;

    /**
     * id
     */
    @Schema(description = "")
    String name;


    /**
     * id
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String desc;
}
