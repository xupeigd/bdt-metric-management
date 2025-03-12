package com.quicksand.bigdata.metric.management.draft.dms;

import com.quicksand.bigdata.metric.management.consts.DraftType;
import com.quicksand.bigdata.metric.management.draft.dbvos.DraftDBVO;

/**
 * DraftDataManager
 *
 * @author page
 * @date 2022/8/4
 */
public interface DraftDataManager {

    /**
     * 根据flag，type，userId查询草稿实体
     *
     * @param flag   自定义标识
     * @param type   DraftType
     * @param userId 用户id
     * @return instance of DraftDBVO / null
     */
    DraftDBVO findDraft(String flag, DraftType type, int userId);

    /**
     * 查找草稿实体
     *
     * @param id 草稿id
     * @return instance of DraftDBVO / null
     */
    DraftDBVO findDraft(int id);

    /**
     * 保存草稿实体
     *
     * @param draft instance of DraftDBVO
     * @return DraftDBVO
     */
    DraftDBVO saveDraft(DraftDBVO draft);

}
