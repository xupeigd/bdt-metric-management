package com.quicksand.bigdata.metric.management.identify.repos;

import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;

import java.util.List;

/**
 * UserRepo
 *
 * @author page
 * @date 2022/8/30
 */
public interface UserRepo {

    /**
     * 按照特定的列检索用户
     * （精确）
     *
     * @param col   列名
     * @param value 值
     * @return list of UserDBVO / empty List
     */
    List<UserDBVO> findUserByOtherPk(String col, String value);

}
