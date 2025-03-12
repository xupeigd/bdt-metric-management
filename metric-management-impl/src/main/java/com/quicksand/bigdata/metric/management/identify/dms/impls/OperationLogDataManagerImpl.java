package com.quicksand.bigdata.metric.management.identify.dms.impls;

import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.dms.OperationLogDataManager;
import com.quicksand.bigdata.metric.management.identify.repos.OperationLogAutoRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * OperationLogDataManagerImpl
 *
 * @author page
 * @date 2020/8/25 22:53
 */
@Component
public class OperationLogDataManagerImpl
        implements OperationLogDataManager {

    @Resource
    OperationLogAutoRepo operationLogAutoRepo;

    @Override
    public void saveAll(List<OperationLogDBVO> operaLogs) {
        operationLogAutoRepo.saveAll(operaLogs);
    }

    @Override
    public Page<OperationLogDBVO> findWithPage(int curPage, int pageSize, Integer operationUserId) {
        if (null != operationUserId) {
            return operationLogAutoRepo.findByUserId(operationUserId, PageRequest.of(curPage, pageSize, Sort.by(Sort.Order.desc("operationTime"))));
        } else {
            return operationLogAutoRepo.findAll(PageRequest.of(curPage, pageSize, Sort.by(Sort.Order.desc("operationTime"))));
        }
    }

    @Override
    public List<OperationLogDBVO> queryLastLogsByUserIdsAndType(Collection<Integer> userIds, OperationLogType type) {
        return operationLogAutoRepo.queryLastLogsByUserIdsAndType(userIds, type.getCode());
    }

}
