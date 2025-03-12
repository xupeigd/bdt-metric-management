package com.quicksand.bigdata.metric.management.identify.utils;

import com.quicksand.bigdata.metric.management.identify.models.UserOperationLogModel;
import com.quicksand.bigdata.metric.management.identify.vos.OperationLogVO;

/**
 * OperationLogAdapter
 *
 * @author page
 * @date 2020/8/26 13:14
 */
public final class OperationLogAdapter {

    public static UserOperationLogModel cover2Model(OperationLogVO vo) {
        UserOperationLogModel uoplm = new UserOperationLogModel();
        uoplm.setId(vo.getId());
        uoplm.setAddress(vo.getAddress());
        uoplm.setDetail(vo.getDetail());
        uoplm.setIp(vo.getIp());
        uoplm.setOperateTime(vo.getOperationTime());
        uoplm.setUserName("");
        uoplm.setUserId(vo.getUserId());
        return uoplm;
    }

}
