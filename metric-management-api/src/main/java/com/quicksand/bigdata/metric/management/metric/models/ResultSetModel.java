package com.quicksand.bigdata.metric.management.metric.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ResultSetModel
 * (结果集对象)
 *
 * @author page
 * @date 2022/8/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "结果对象")
public class ResultSetModel {

    /**
     * 结果输出模式
     * <p>
     * 0 列 1 行
     */
    @Schema(description = "")
    Integer resultMode;

    /**
     * 任务状态
     * 0就绪 1执行中 2取消 3成功 4失败
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer state;

    /**
     * 执行提示
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String msg;

    /**
     * metaData
     * key String(Name of Column)
     * value Class of column
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ColumnMetaModel> columnMetas;

    /**
     * 数据set
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<List<?>> rows;

    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<List<?>> columns;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ColumnMetaModel {

        int index;

        String name;

        int sqlType;

        @JsonIgnore
        Class<?> javaType;

    }

}
