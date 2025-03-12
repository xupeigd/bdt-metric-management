package com.quicksand.bigdata.metric.management.identify.dms;

import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import org.springframework.data.domain.Page;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * OperationLogDataManager
 *
 * @author page
 * @date 2020/8/25 22:52
 */
public interface OperationLogDataManager {

    void saveAll(List<OperationLogDBVO> operaLogs);

    Page<OperationLogDBVO> findWithPage(int curPage, int pageSize, @Nullable Integer operationUserId);

    List<OperationLogDBVO> queryLastLogsByUserIdsAndType(Collection<Integer> userIds, OperationLogType login);

}
