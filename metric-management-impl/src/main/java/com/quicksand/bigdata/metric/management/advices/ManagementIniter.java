package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.metric.management.identify.repos.PermissionAutoRepo;
import com.quicksand.bigdata.metric.management.identify.repos.RoleAutoRepo;
import com.quicksand.bigdata.metric.management.identify.repos.UserAutoRepo;
import com.quicksand.bigdata.metric.management.metric.dms.MetricDataManager;
import com.quicksand.bigdata.metric.management.utils.MetricCatalogInitData;
import com.quicksand.bigdata.metric.management.utils.PermissionInitData;
import com.quicksand.bigdata.metric.management.utils.RoleInitData;
import com.quicksand.bigdata.metric.management.utils.UserInitData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/**
 * ManagementIniter
 *
 * @author page
 * @date 2022/8/19
 */
@Slf4j
@Component
public class ManagementIniter
        implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${sys.runtime.inited:false}")
    boolean initEnable;

    @Resource
    DataSource dataSource;
    @Resource
    UserAutoRepo userAutoRepo;
    @Resource
    RoleAutoRepo roleAutoRepo;
    @Resource
    PermissionAutoRepo permissionAutoRepo;
    @Resource
    MetricDataManager metricDataManager;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (initEnable) {
            checkAndInitPermissions();
            checkAndInitRoles();
            checkAndInitUsers();
            checkAndInitCatalogs();
        }
    }

    private void checkAndInitCatalogs() {
        if (null == metricDataManager.findByMetricId(0)) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<String> sqls = MetricCatalogInitData.initSqls();
            for (String sql : sqls) {
                jdbcTemplate.execute(sql);
                log.info("--init datacatalogs -- {}", sql);
            }
        }
    }

    private void checkAndInitPermissions() {
        if (CollectionUtils.isEmpty(permissionAutoRepo.findAll())) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<String> sqls = PermissionInitData.initSqls();
            for (String sql : sqls) {
                jdbcTemplate.execute(sql);
                log.info("--init permissions -- {}", sql);
            }
        }
    }

    private void checkAndInitRoles() {
        if (CollectionUtils.isEmpty(roleAutoRepo.findAll())) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<String> sqls = RoleInitData.initSqls();
            for (String sql : sqls) {
                jdbcTemplate.execute(sql);
                log.info("--init roles -- {}", sql);
            }
        }
    }

    private void checkAndInitUsers() {
        if (CollectionUtils.isEmpty(userAutoRepo.findAll())) {
            List<String> sqls = UserInitData.initSqls();
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            for (String sql : sqls) {
                jdbcTemplate.execute(sql);
                log.info("--init users -- {}", sql);
            }

        }
    }


}
