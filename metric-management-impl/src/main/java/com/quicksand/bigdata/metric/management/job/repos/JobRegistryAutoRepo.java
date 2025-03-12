package com.quicksand.bigdata.metric.management.job.repos;

import com.quicksand.bigdata.metric.management.job.core.model.JobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * JobRegistryAutoRepo
 *
 * @author page
 * @date 2023/2/3
 */
@Repository
public interface JobRegistryAutoRepo
        extends JpaRepository<JobRegistry, Integer> {

    List<JobRegistry> findByUpdateTimeBefore(Date nowTime);

    @Query(value = "SELECT * FROM tbl_metric_job_registry AS t WHERE t.update_time > DATE_ADD( :nowTime ,INTERVAL - :timeout SECOND) ",
            nativeQuery = true)
    List<JobRegistry> findAll(@Param("timeout") int timeout,
                              @Param("nowTime") Date nowTime);


    JobRegistry findTopByRegistryGroupAndRegistryKeyAndRegistryValueOrderByUpdateTimeDesc(String registryGroup,
                                                                                          String registryKey,
                                                                                          String registryValue);

    // @Transactional
    // @Modifying
    // @Query(value = "UPDATE xxl_job_registry SET `update_time` = :updateTime WHERE `registry_group` = :registryGroup " +
    //         "AND `registry_key` = :registryKey AND `registry_value` = :registryValue ",
    //         nativeQuery = true)
    // void registryUpdate(@Param("registryGroup") String registryGroup,
    //                     @Param("registryKey") String registryKey,
    //                     @Param("registryValue") String registryValue,
    //                     @Param("updateTime") Date updateTime);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM tbl_metric_job_registry WHERE registry_group = :registryGroup AND registry_key = :registryKey " +
            "AND registry_value = :registryValue ",
            nativeQuery = true)
    void registryDelete(@Param("registryGroup") String registryGroup,
                        @Param("registryKey") String registryKey,
                        @Param("registryValue") String registryValue);

}


 
