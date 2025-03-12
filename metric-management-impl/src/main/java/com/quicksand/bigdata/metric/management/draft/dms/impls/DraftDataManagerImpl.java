package com.quicksand.bigdata.metric.management.draft.dms.impls;

import com.quicksand.bigdata.metric.management.consts.DraftType;
import com.quicksand.bigdata.metric.management.draft.dbvos.DraftDBVO;
import com.quicksand.bigdata.metric.management.draft.dms.DraftDataManager;
import com.quicksand.bigdata.metric.management.draft.repos.DraftAutoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DraftDataManagerImpl
 *
 * @author page
 * @date 2022/8/4
 */
@Slf4j
@Component
public class DraftDataManagerImpl
        implements DraftDataManager {

    @Resource
    DraftAutoRepo draftAutoRepo;

    @Override
    public DraftDBVO findDraft(String flag, DraftType type, int userId) {
        return draftAutoRepo.findOneByFlagAndTypeAndUserId(flag, type, userId);
    }

    @Override
    public DraftDBVO findDraft(int id) {
        return draftAutoRepo.findById(id);
    }

    @Override
    public DraftDBVO saveDraft(DraftDBVO draft) {
        return draftAutoRepo.save(draft);
    }


}
