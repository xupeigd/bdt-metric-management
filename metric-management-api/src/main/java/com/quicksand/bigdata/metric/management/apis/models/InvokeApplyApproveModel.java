package com.quicksand.bigdata.metric.management.apis.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * InvokeApplyApproveModel
 *
 * @author page
 * @date 2022/10/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema
@Validated
public class InvokeApplyApproveModel {

    /**
     * 审核状态
     * <p>
     * 1核准 2拒绝
     */
    @Schema(description = "")
    @NotNull
    @Min(value = 1, message = "审批状态  1核准 2拒绝")
    @Max(value = 2, message = "审批状态  1核准 2拒绝")
    Integer state;

    /**
     * 审核意见
     */
    @Schema(description = "")
    @Length(max = 1024, message = "审核意见最长不能超过1024！")
    String approveComment;

}
