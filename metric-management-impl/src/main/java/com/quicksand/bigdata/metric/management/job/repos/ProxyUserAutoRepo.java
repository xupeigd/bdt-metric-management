package com.quicksand.bigdata.metric.management.job.repos;

import com.quicksand.bigdata.metric.management.job.core.model.ProxyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author xuxueli 2019-05-04 16:44:59
 */
@Repository
public interface ProxyUserAutoRepo
        extends JpaRepository<ProxyUser, Integer> {

    List<ProxyUser> findByUserIdIn(Collection<Integer> ids);

    ProxyUser findByUserId(int userId);

}
