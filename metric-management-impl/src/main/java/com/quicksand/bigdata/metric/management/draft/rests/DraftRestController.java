package com.quicksand.bigdata.metric.management.draft.rests;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.consts.DraftType;
import com.quicksand.bigdata.metric.management.consts.Insert;
import com.quicksand.bigdata.metric.management.consts.Update;
import com.quicksand.bigdata.metric.management.draft.dbvos.DraftDBVO;
import com.quicksand.bigdata.metric.management.draft.dms.DraftDataManager;
import com.quicksand.bigdata.metric.management.draft.models.DraftModel;
import com.quicksand.bigdata.metric.management.draft.models.DraftModifyModel;
import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.UserDataManager;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Objects;

/**
 * DraftRestController
 *
 * @author page
 * @date 2022/8/4
 */
@Slf4j
@CrossOrigin
// @Api("草稿Apis")
@Tag(name = "草稿Apis")
@Validated
@RestController
public class DraftRestController
        implements DrafRestService {

    @Resource
    UserDataManager userDataManager;
    @Resource
    DraftDataManager draftDataManager;

    @Operation(description = "检索草稿")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
    })
    @Override
    public Response<DraftModel> discernDraft(@PathVariable("flag") @NotBlank(message = "flag不存在！")
                                             @Length(min = 4, max = 16, message = "flag长度必须在 4 ~ 16之间! ") String flag,
                                             @RequestParam("type") @Min(value = 0L, message = "不支持的草稿类型！")
                                             @Max(value = 1L, message = "不支持的草稿类型！") int type) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        if (null == userDetail) {
            return Response.response(HttpStatus.FORBIDDEN);
        }
        DraftDBVO draft = draftDataManager.findDraft(flag, DraftType.find(type), userDetail.getId());
        return Response.ok(null == draft ? null : JsonUtils.transfrom(draft, DraftModel.class));
    }

    @Operation(description = "创建草稿对象")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @Override
    public Response<DraftModel> createDraft(@RequestBody @Validated({Insert.class}) DraftModifyModel model) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        if (null == userDetail) {
            return Response.response(HttpStatus.FORBIDDEN);
        }
        UserDBVO user = userDataManager.findUser(userDetail.getId());
        Date operationTime = new Date();
        DraftDBVO insatnce = DraftDBVO.builder()
                .flag(model.getFlag())
                .content(model.getContent())
                .user(user)
                .type(DraftType.find(model.getType()))
                .createTime(operationTime)
                .updateTime(operationTime)
                .status(DataStatus.ENABLE)
                .build();
        return Response.ok(JsonUtils.transfrom(draftDataManager.saveDraft(insatnce), DraftModel.class));
    }

    @Operation(description = "保存草稿")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "not found ! "),
    })
    @Override
    public Response<DraftModel> modifyDraft(@PathVariable("id") @Min(value = 1L, message = "不存在的草稿！") int id,
                                            @RequestBody @Validated({Update.class}) DraftModifyModel model) {
        if (null == AuthUtil.getUserDetail()) {
            return Response.response(HttpStatus.FORBIDDEN);
        }
        if (!Objects.equals(id, model.getId())) {
            return Response.response(HttpStatus.BAD_REQUEST, "不存在的草稿！");
        }
        DraftDBVO draft = draftDataManager.findDraft(id);
        if (null == draft) {
            return Response.notfound();
        }
        if (!Objects.equals(draft.getUser().getId(), AuthUtil.getUserDetail().getId())) {
            return Response.response(HttpStatus.FORBIDDEN);
        }
        //只允许更新content
        draft.setContent(model.getContent());
        draft.setUpdateTime(new Date());
        return Response.ok(JsonUtils.transfrom(draftDataManager.saveDraft(draft), DraftModel.class));
    }

    @Operation(description = "删除草稿")
    @CrossOrigin
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "not found ! "),
    })
    @Override
    public Response<Void> removeDraft(@PathVariable("id") @Min(value = 1L, message = "不存在的草稿！") int id) {
        if (null == AuthUtil.getUserDetail()) {
            return Response.response(HttpStatus.FORBIDDEN);
        }
        DraftDBVO draft = draftDataManager.findDraft(id);
        if (null == draft) {
            return Response.notfound();
        }
        if (!Objects.equals(draft.getUser().getId(), AuthUtil.getUserDetail().getId())) {
            return Response.response(HttpStatus.FORBIDDEN);
        }
        draft.setFlag(String.format("%s-%d", draft.getFlag(), draft.getId()));
        draft.setStatus(DataStatus.DISABLE);
        draft.setUpdateTime(new Date());
        draftDataManager.saveDraft(draft);
        return Response.ok();
    }

}
