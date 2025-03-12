package com.quicksand.bigdata.metric.management.identify.rests;

import com.quicksand.bigdata.metric.management.identify.models.UserOperationLogModel;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.services.UserService;
import com.quicksand.bigdata.metric.management.identify.utils.OperationLogAdapter;
import com.quicksand.bigdata.metric.management.identify.vos.OperationLogVO;
import com.quicksand.bigdata.metric.management.identify.vos.UserVO;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * LogController
 *
 * @author page
 * @date 2020/8/26 12:20
 */
@RestController
@Tag(name = "日志服务apis")
public class LogController
        implements LogRestService {

    @Resource
    OperationLogService operationLogService;
    @Resource
    UserService userService;

    @PreAuthorize("hasAuthority('OP_OPERATION_LOGS') || @varsSecurityUtilService.isAnonymousUser(authentication)")
    @Override
    public Response<PageImpl<UserOperationLogModel>> queryLogs(@RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                               @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                                                               @RequestParam(name = "operationUserId", required = false) Integer operationUserId) {
        int correctPageNo = null == pageNo || 0 >= pageNo ? 1 : pageNo;
        int correctPageSize = null == pageSize || 0 >= pageSize ? 20 : pageSize;
        PageImpl<OperationLogVO> pageVO = operationLogService.findByPage(correctPageNo, correctPageSize, operationUserId);
        PageImpl<UserOperationLogModel> pageModel = PageImpl.buildEmptyPage(correctPageNo, correctPageSize);
        if (CollectionUtils.isEmpty(pageVO.getItems())) {
            return Response.ok(pageModel);
        }
        Set<Integer> userIds = pageVO.getItems().stream()
                .map(OperationLogVO::getUserId)
                .collect(Collectors.toSet());
        Set<UserVO> userSet = userService.queryUsers(userIds);
        Map<Integer, UserVO> id2UserMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(userSet)) {
            userSet.forEach(e -> id2UserMap.put(e.getId(), e));
        }
        pageModel.setTotalPage(pageVO.getTotalPage());
        pageModel.setTotal(pageVO.getTotal());
        pageModel.setItems(pageVO.getItems().stream()
                .map(OperationLogAdapter::cover2Model)
                .collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(id2UserMap)) {
            pageModel.getItems().forEach(e -> {
                if (null != id2UserMap.get(e.getUserId())) {
                    e.setUserName(id2UserMap.get(e.getUserId()).getName());
                }
            });
        }
        return Response.ok(pageModel);
    }

}
