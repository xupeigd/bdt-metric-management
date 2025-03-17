package com.quicksand.bigdata.metric.management.datasource.repos;// package com.quicksand.bigdata.metric.management.datasource.repos;
//
// import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetColumnDBVO;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
//
// import java.util.List;
//
// /**
//  * DatasetColumnAutoRepo
//  *
//  * @author page
//  * @date 2022/7/27
//  */
// @Repository
// public interface DatasetColumnAutoRepo
//         extends JpaRepository<DatasetColumnDBVO, Integer> {
//
//     /**
//      * 按数据集id与数据集的tableName查询字段信息
//      *
//      * @param datasetId 数据集id
//      * @param tableName 数据集表名
//      * @return list of DatasetColumnDBVO
//      */
//     List<DatasetColumnDBVO> findAllByDatasetIdAndDatasetTableNameOrderBySerial(int datasetId, String tableName);
//
// }
