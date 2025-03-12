package com.quicksand.bigdata.metric.management.draft.repos;

import com.quicksand.bigdata.metric.management.consts.DraftType;
import com.quicksand.bigdata.metric.management.draft.dbvos.DraftDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DrafAutoRepo
 *
 * @author page
 * @date 2022/8/4
 */
@Repository
public interface DraftAutoRepo
        extends JpaRepository<DraftDBVO, Integer> {

    /**
     * 根据flag，type，userId查询草稿实体
     *
     * @param flag   自定义标识
     * @param type   0 dataset 1 metric
     * @param userId 用户id
     * @return instance of DraftDBVO / null
     */
    DraftDBVO findOneByFlagAndTypeAndUserId(String flag, DraftType type, int userId);

    /**
     * id查找
     *
     * @param id 逻辑主键
     * @return instance of DraftDBVO / null
     */
    DraftDBVO findById(int id);


}
