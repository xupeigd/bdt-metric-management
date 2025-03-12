package com.quicksand.bigdata.metric.management.draft.models;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DraftModifyModel
 *
 * @author page
 * @date 2022-08-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftModifyModel {

    @NotNull(groups = {Update.class})
    Integer id;

    /**
     * 识别flag
     * (模块自定义)
     */
    @Length(min = 4, max = 16, message = "flag长度必须在 4 ~ 16之间! ", groups = {Insert.class})
    @NotBlank(message = "非法参数 flag! ", groups = {Insert.class})
    String flag;

    /**
     * 草稿类型
     * <p>
     * 0 dataset 1 metric
     */
    @NotNull(message = "", groups = {Insert.class, Update.class})
    @Min(value = 0L, message = "", groups = {Insert.class, Update.class})
    @Max(value = 1L, message = "", groups = {Insert.class, Update.class})
    Integer type;

    /**
     * 草稿内容
     */
    @NotNull(groups = {Insert.class, Update.class})
    @Length(max = 10240, message = "", groups = {Insert.class, Update.class})
    String content;

}
