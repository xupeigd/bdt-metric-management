package com.quicksand.bigdata.metric.management.datasource.services.impls;

import com.quicksand.bigdata.metric.management.consts.DataStatus;
import com.quicksand.bigdata.metric.management.datasource.dbvos.ClusterInfoDBVO;
import com.quicksand.bigdata.metric.management.datasource.dms.ClusterInfoDataManager;
import com.quicksand.bigdata.metric.management.datasource.models.ClusterInfoModel;
import com.quicksand.bigdata.metric.management.datasource.services.ClusterInfoService;
import com.quicksand.bigdata.metric.management.datasource.vos.ClusterInfoVO;
import com.quicksand.bigdata.vars.util.JsonUtils;
import com.quicksand.bigdata.vars.util.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class ClusterInfoServiceImpl
 *
 * @Author: page
 * @Date: 2025/2/27
 * @Description:
 */
@Service
public class ClusterInfoServiceImpl
        implements ClusterInfoService {

    @Resource
    ClusterInfoDataManager clusterInfoDataManager;

    @Override
    public PageImpl<ClusterInfoVO> queryClusterInfos(int pageNo, int pageSize) {
        Page<ClusterInfoDBVO> page = clusterInfoDataManager.queryClusterInfos(PageRequest.of(pageNo - 1, pageSize));
        return null == page || CollectionUtils.isEmpty(page.getContent())
                ? PageImpl.buildEmptyPage(pageNo, pageSize)
                : PageImpl.build(pageNo, pageSize, page.getTotalPages(), page.getTotalElements(),
                page.getContent().stream()
                        .map(v -> JsonUtils.transfrom(v, ClusterInfoVO.class))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public ClusterInfoVO saveClusterInfo(ClusterInfoModel model) {
        ClusterInfoDBVO dbvo = null;
        if (null != model.getId()) {
            dbvo = clusterInfoDataManager.findClusterInfo(model.getId());
            if (null == dbvo) {
                throw new ValidationException("集群数据不存在，无法修改！");
            }
        } else {
            dbvo = new ClusterInfoDBVO();
            dbvo.setStatus(DataStatus.ENABLE);
            dbvo.setCreateTime(new Date());
            dbvo.setType(model.getType());
        }
        dbvo.setName(model.getName());
        dbvo.setAddress(model.getAddress());
        dbvo.setComment(null == model.getComment() ? "" : model.getComment());
        dbvo.setAddress(model.getAddress());
        dbvo.setDefaultSchema(StringUtils.hasText(model.getDefaultSchema()) ? model.getDefaultSchema() : "");
        dbvo.setDefaultDatabase(StringUtils.hasText(model.getDefaultDatabase()) ? model.getDefaultDatabase() : "");
        dbvo.setUserName(StringUtils.hasText(model.getUserName()) ? model.getUserName() : "");
        dbvo.setPassword(StringUtils.hasText(model.getPassword()) ? model.getPassword() : "");
        dbvo.setUpdateTime(new Date());
        return JsonUtils.transfrom(clusterInfoDataManager.saveClusterInfo(dbvo), ClusterInfoVO.class);
    }

    @Override
    public ClusterInfoVO queryClusterInfo(Integer clusterId) {
        ClusterInfoDBVO dbvo = clusterInfoDataManager.findClusterInfo(clusterId);
        return null == dbvo ? null : JsonUtils.transfrom(dbvo, ClusterInfoVO.class);
    }

    @Transactional
    @Override
    public void deleteClusterInfos(Collection<Integer> clusterIds) {
        List<ClusterInfoDBVO> infos = clusterInfoDataManager.findClusterInfosById(clusterIds);
        if (!CollectionUtils.isEmpty(infos)) {
            for (ClusterInfoDBVO info : infos) {
                info.setStatus(DataStatus.DISABLE);
                info.setUpdateTime(new Date());
            }
            clusterInfoDataManager.saveClusterInfos(infos);
        }
    }

}
