package com.quicksand.bigdata.metric.management.job.repos;// package com.quicksand.bigdata.metric.management.jobs.repo;
//
// import com.quicksand.bigdata.metric.management.jobs.core.model.JobUser;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
//
// /**
//  * @author xuxueli 2019-05-04 16:44:59
//  */
// @Repository
// public interface JobUserAutoRepo
//         extends JpaRepository<JobUser, Integer> {
//
//     Page<JobUser> findByUsernameAndRole(String username, int role, Pageable pageable);
//
//     JobUser findByUsername(String userName);
//
//     Page<JobUser> findByUsername(String username, Pageable pageable);
//
//     Page<JobUser> findByRole(int role, Pageable pageable);
//
// }
