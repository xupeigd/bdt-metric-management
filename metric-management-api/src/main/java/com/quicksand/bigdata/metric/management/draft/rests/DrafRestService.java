package com.quicksand.bigdata.metric.management.draft.rests;

import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.draft.models.DraftModel;
import com.quicksand.bigdata.metric.management.draft.models.DraftModifyModel;
import com.quicksand.bigdata.vars.http.model.Response;
import org.hibernate.validator.constraints.Length;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * DrafRestService
 *
 * @author page
 * @date 2022-08-02
 */
@FeignClient(
        name = "${vars.name.ms.metric:METRIC-MANAGEMENT}",
        url = "${vars.url.ms.metric.DrafRestService:}",
        contextId = "DrafRestService")
public interface DrafRestService {

    /**
     * 定位草稿是否存在
     *
     * @param flag 草稿的标志 eg： {module}-{step}
     * @param type 类型 @see DraftType
     * @return instance of DraftModel / null
     */
    @GetMapping("/draft/drafts/{flag}")
    Response<DraftModel> discernDraft(@PathVariable("flag") @NotBlank(message = "flag不存在！")
                                      @Length(min = 4, max = 16, message = "flag长度必须在 4 ~ 16之间! ") String flag,
                                      @RequestParam("type") @Min(value = 0L, message = "不支持的草稿类型！")
                                      @Max(value = 1L, message = "不支持的草稿类型！") int type);

    /**
     * 创建草稿
     *
     * @param model 创建参数
     * @return instance of DraftModel
     */
    @PostMapping("/draft/drafts")
    Response<DraftModel> createDraft(@RequestBody @Validated({Insert.class}) DraftModifyModel model);


    /**
     * 保存草稿
     *
     * @param id    草稿id
     * @param model 修改参数
     * @return instance of DraftModel
     */
    @PutMapping("/draft/drafts/{id}")
    Response<DraftModel> modifyDraft(@PathVariable("id") @Min(value = 1L, message = "不存在的草稿！") int id,
                                     @RequestBody @Validated({Update.class}) DraftModifyModel model);

    /**
     * 删除草稿
     *
     * @param id 草稿实体id
     * @return void
     */
    @DeleteMapping("/draft/drafts/{id}")
    Response<Void> removeDraft(@PathVariable("id") @Min(value = 1L, message = "不存在的草稿！") int id);


}
