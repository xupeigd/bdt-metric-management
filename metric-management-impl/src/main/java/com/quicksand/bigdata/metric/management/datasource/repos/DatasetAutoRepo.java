package com.quicksand.bigdata.metric.management.datasource.repos;

import com.quicksand.bigdata.metric.management.datasource.dbvos.DatasetDBVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * DatasetAutoRepo
 *
 * @author page
 * @date 2022/7/27
 */
@Repository
public interface DatasetAutoRepo
        extends JpaRepository<DatasetDBVO, Integer> {

    DatasetDBVO findById(int id);

    DatasetDBVO findByName(String name);

    @Query(value = "SELECT d.* FROM t_datasource_datasets d LEFT JOIN t_datasource_dataset_owners o ON d.id = o.dataset_id LEFT JOIN t_identify_users u ON u.id = o.user_id " +
            "where d.status = 1 and d.name like :keyword and d.cluster_id in (:clusterIds) and u.id in (:ownerIds) \n-- #pageReq\n",
            countQuery = "SELECT count(distinct d.id) FROM t_datasource_datasets d LEFT JOIN t_datasource_dataset_owners o ON d.id = o.dataset_id LEFT JOIN t_identify_users u ON u.id = o.user_id " +
                    "where d.status = 1 and d.name like :keyword and d.cluster_id in (:clusterIds) and u.id in (:ownerIds)",
            nativeQuery = true)
    Page<DatasetDBVO> findByNameLikeAndClusterIdAndOwner(String keyword, String clusterIds, String ownerIds, Pageable pageReq);

    @Query(value = "SELECT d.* FROM t_datasource_datasets d LEFT JOIN t_datasource_dataset_owners o ON d.id = o.dataset_id LEFT JOIN t_identify_users u ON u.id = o.user_id " +
            "where d.status = 1 and u.id in :ownerIds \n-- #pageReq\n",
            countQuery = "SELECT count(distinct d.id) FROM t_datasource_datasets d LEFT JOIN t_datasource_dataset_owners o ON d.id = o.dataset_id LEFT JOIN t_identify_users u ON u.id = o.user_id " +
                    "where d.status = 1 and u.id in :ownerIds",
            nativeQuery = true)
    Page<DatasetDBVO> findByOwner(Collection<Integer> ownerIds, Pageable pageReq);

    Page<DatasetDBVO> findByClusterIdIn(List<Integer> clusterIds, Pageable pageReq);

    Page<DatasetDBVO> findByNameLikeAndClusterIdIn(String keyword, List<Integer> clusterIds, Pageable pageReq);

    @Query(value = "SELECT d.* FROM t_datasource_datasets d LEFT JOIN t_datasource_dataset_owners o ON d.id = o.dataset_id LEFT JOIN t_identify_users u ON u.id = o.user_id " +
            "where d.status = 1 and d.name like :keyword and u.id in (:ownerIds) \n-- #pageReq\n",
            countQuery = "SELECT count(distinct d.id) FROM t_datasource_datasets d LEFT JOIN t_datasource_dataset_owners o ON d.id = o.dataset_id LEFT JOIN t_identify_users u ON u.id = o.user_id " +
                    "where d.status = 1 and d.name like :keyword and u.id in (:ownerIds) ",
            nativeQuery = true)
    Page<DatasetDBVO> findByNameLikeAndOwner(String keyword, String ownerIds, Pageable pageReq);

    Page<DatasetDBVO> findByNameLike(String nameKeyword, Pageable pageReq);

    List<DatasetDBVO> findByNameAndClusterId(String name, Integer clusterId);

    List<DatasetDBVO> findByNameIn(Collection<String> dsNames);

}
