// package com.quicksand.bigdata.metric.management.datasource.repos;
//
// import com.quicksand.bigdata.metric.management.consts.DataStatus;
// import com.quicksand.bigdata.metric.management.datasource.dbvos.IdentifierDBVO;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
//
// import java.util.List;
//
// /**
//  * IdentifierAutoRepo
//  *
//  * @author page
//  * @date 2022/8/15
//  */
// @Repository
// public interface IdentifierAutoRepo
//         extends JpaRepository<IdentifierDBVO, Integer> {
//
//     List<IdentifierDBVO> findByStatusAndDatasetId(DataStatus status, int datasetId);
//
// }
