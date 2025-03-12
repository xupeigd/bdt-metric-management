package com.quicksand.bigdata.metric.management.identify.services.impls;

import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.OperationLogDataManager;
import com.quicksand.bigdata.metric.management.identify.services.OperationLogService;
import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.metric.management.identify.vos.OperationLogVO;
import com.quicksand.bigdata.vars.concurrents.TraceFuture;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.PageImpl;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * OperationLogServiceImpl
 *
 * @author page
 * @date 2020/8/25 23:37
 */
@Slf4j
@Component
public class OperationLogServiceImpl
        implements OperationLogService {

    private final LinkedBlockingQueue<OperationLogDBVO> logs = new LinkedBlockingQueue<>();

    @Resource
    OperationLogDataManager operationLogDataManager;

    @PostConstruct
    protected void init() {
        TraceFuture.run(() -> {
            while (true) {
                try {
                    batchOrmTrigger();
                    TimeUnit.SECONDS.sleep(10L);
                } catch (Exception e) {
                    log.error("OperationLogService schedule thread execute error!", e);
                }
            }
        });
    }

    private void batchOrmTrigger() throws InterruptedException {
        List<OperationLogDBVO> operaLogs = new ArrayList<>();
        int curSize = 0;
        if (!logs.isEmpty()) {
            OperationLogDBVO dbvo;
            while (null != (dbvo = logs.poll(1, TimeUnit.SECONDS)) && 50 > curSize) {
                operaLogs.add(dbvo);
                curSize++;
            }
        }
        if (!operaLogs.isEmpty()) {
            TraceFuture.run(() -> Try.run(() -> operationLogDataManager.saveAll(operaLogs))
                    .onFailure(ex -> log.error("batchOrmTrigger async exeute error !", ex)));
        }
    }

    @Override
    public void log(OperationLogDBVO operationLogDBVO) {
        logs.add(operationLogDBVO);
    }

    @Override
    public void log(OperationLogType type, String detail) {
        UserSecurityDetails userDetail = AuthUtil.getUserDetail();
        OperationLogDBVO operationLogDBVO = OperationLogDBVO.builder()
                .operationTime(new Date())
                .ip("0.0.0.0")
                .address("未知")
                .userId(null == userDetail ? 0 : userDetail.getId())
                .detail(detail)
                .type(type)
                .build();
        logs.add(operationLogDBVO);
    }

    @Override
    public PageImpl<OperationLogVO> findByPage(int pageNo, int pageSize, Integer operationUserId) {
        Page<OperationLogDBVO> olds = operationLogDataManager.findWithPage(pageNo - 1, pageSize, operationUserId);
        PageImpl<OperationLogVO> pageVO = new PageImpl<>();
        pageVO.setPageNo(pageNo + 1);
        pageVO.setPageSize(pageSize);
        if (!CollectionUtils.isEmpty(olds.getContent())) {
            List<OperationLogVO> collect = olds.getContent().stream().map(k -> {
                OperationLogVO opl = new OperationLogVO();
                BeanUtils.copyProperties(k, opl);
                return opl;
            }).collect(Collectors.toList());
            pageVO.setItems(collect);
            pageVO.setTotal(olds.getTotalElements());
            int pageTotal = ((int) olds.getTotalElements() / pageSize) + (0 == olds.getTotalElements() % pageSize ? 0 : 1);
            pageVO.setTotalPage(pageTotal);
        } else {
            pageVO.setTotalPage(0);
            pageVO.setTotal(0L);
        }
        return pageVO;
    }

    @Override
    public List<OperationLogVO> queryLastLoginByUserIds(Collection<Integer> userIds) {
        List<OperationLogDBVO> dbvos = operationLogDataManager.queryLastLogsByUserIdsAndType(userIds, OperationLogType.LOGIN);
        if (CollectionUtils.isEmpty(dbvos)) {
            return Collections.emptyList();
        }
        return dbvos.stream().map(e -> {
            OperationLogVO oplv = new OperationLogVO();
            BeanUtils.copyProperties(e, oplv);
            return oplv;
        }).collect(Collectors.toList());
    }

}
