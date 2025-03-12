package com.quicksand.bigdata.metric.management.job.repos;

import com.quicksand.bigdata.metric.management.job.core.model.JobGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Repository
public interface JobGroupAutoRepo
        extends JpaRepository<JobGroup, Integer> {

    List<JobGroup> findByAddressType(int addressType);

    Page<JobGroup> findByAppnameLikeAndTitleLike(String appKeyword, String titleKeywoord, Pageable pageable);

}
