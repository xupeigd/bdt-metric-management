package com.quicksand.bigdata.metric.management.apis.repos;

import com.quicksand.bigdata.metric.management.apis.dbvos.AppDBVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * AppAutoRepo
 *
 * @author page
 * @date 2022/9/27
 */
@Repository
public interface AppAutoRepo
        extends JpaRepository<AppDBVO, Integer> {

    Page<AppDBVO> findByNameLike(String keyword, Pageable pageable);

    Page<AppDBVO> findByOwnerIdIn(Collection<Integer> ids, Pageable pageable);

    Page<AppDBVO> findByNameLikeAndOwnerIdIn(String keyword, Collection<Integer> ids, Pageable pageable);

    AppDBVO findByName(String name);

    List<AppDBVO> findByOwnerIdOrderByName(Integer id);

    @Query(value = "SELECT id,name  from t_apis_apps where status =1 AND `type` =1 ", nativeQuery = true)
    List<Object[]> findAllApps();

    @Query(value = "SELECT id,name  from t_apis_apps where status =1 AND `type` =1 and owner_id =:ownerId", nativeQuery = true)
    List<Object[]> findUserApps(Integer ownerId);

}
