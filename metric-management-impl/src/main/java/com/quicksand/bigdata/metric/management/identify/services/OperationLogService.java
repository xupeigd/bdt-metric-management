package com.quicksand.bigdata.metric.management.identify.services;

import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.identify.dbvos.OperationLogDBVO;
import com.quicksand.bigdata.metric.management.identify.vos.OperationLogVO;
import com.quicksand.bigdata.vars.util.PageImpl;

import java.util.Collection;
import java.util.List;

/**
 * OperationLogService
 *
 * @author page
 * @date 2020/8/25 23:37
 */
public interface OperationLogService {

    /**
     * 记录操作日志
     *
     * @param operationLogDBVO 日志对象
     */
    void log(OperationLogDBVO operationLogDBVO);

    void log(OperationLogType type, String logInfo);

    PageImpl<OperationLogVO> findByPage(int pageNo, int pageSize, Integer operationUserId);

    List<OperationLogVO> queryLastLoginByUserIds(Collection<Integer> keySet);

}
