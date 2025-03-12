package com.quicksand.bigdata.metric.management.yaml.services.impls;

import com.quicksand.bigdata.metric.management.datasource.vos.ClusterInfoVO;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class MetricFileService {

    static final String FILE_PATH_TEMPLATE = "%s/%s/%s_%s.yaml";
    static final String FAIL_FILE_PATH_TEMPLATE = "%s/%s/%s_%s.yaml.%s.ecf";
    static final String CONFIG_PATH = "%s/config.yml";
    static final String CONFIG_TEMPLATE = "dwh_schema: '%s'\n" +
            "dwh_dialect: redshift\n" +
            "dwh_host: '%s'\n" +
            "dwh_port: '%s'\n" +
            "dwh_user: '%s'\n" +
            "dwh_password: '%s'\n" +
            "dwh_database: '%s'\n" +
            "model_path: %s\n";

    @Value("${bdt.management.mf.conf.path:/root/.metricflow}")
    String mfConfigPath;
    @Value("${bdt.management.mf.conf.path:bdt}")
    String mfModulePath;

    @Value("${bdt.log.path:/home/logs}")
    String metricFlowErrorPath;


    public boolean replaceConfigFile(ClusterInfoVO clusterInfoVO) {
        File configYml = new File(String.format(CONFIG_PATH, mfConfigPath));
        configYml.deleteOnExit();
        FileOutputStream fileOutputStream = null;
        //覆盖
        try {
            fileOutputStream = new FileOutputStream(configYml);
            String[] addressInfos = clusterInfoVO.getAddress().split(":");
            fileOutputStream.write(String.format(CONFIG_TEMPLATE, clusterInfoVO.getDefaultDatabase(),
                    addressInfos[0], addressInfos[1], clusterInfoVO.getUserName(), clusterInfoVO.getPassword(),
                    clusterInfoVO.getDefaultDatabase(), mfConfigPath + "/" + mfModulePath).getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
            fileOutputStream.close();
            log.info("replace mf config ! clusterId:{}", clusterInfoVO.getId());
            return true;
        } catch (Exception ex) {
            log.warn(String.format("replace mf config fail ! path:%s, clusterInfo:%s", configYml.getAbsolutePath(), JsonUtils.toJsonString(clusterInfoVO)), ex);
        } finally {
            if (null != fileOutputStream) {
                Try.run(fileOutputStream::close);
            }
        }
        return false;
    }

    public boolean createYamlFiles(String userName, String metricName, String yamlContent) {
        File yamlFile = new File(String.format(FILE_PATH_TEMPLATE, mfConfigPath, mfModulePath, userName, metricName));
        return Try.of(() -> {
                    if (yamlFile.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        yamlFile.delete();
                    }
                    FileUtil.writeAsString(yamlFile, yamlContent);
                    return true;
                })
                .onFailure(ex -> log.error(String.format("createYamlFiles fail ! path:%s", yamlFile.getAbsolutePath()), ex))
                .getOrElse(false);
    }

    public boolean deleteYamlFiles(String userName, String metricName) {
        File yamlFile = new File(String.format(FILE_PATH_TEMPLATE, mfConfigPath, mfModulePath, userName, metricName));
        return yamlFile.delete();
    }

    public void keepYamlFiles(String username, String metricName) {
        String dateFlag = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File yamlFile = new File(String.format(FILE_PATH_TEMPLATE, mfConfigPath, mfModulePath, username, metricName));
        if (yamlFile.exists()) {
            Try.run(() -> {
                        File newFile = new File(String.format(FAIL_FILE_PATH_TEMPLATE, metricFlowErrorPath, mfModulePath, username, metricName, dateFlag));
                        FileUtils.moveFile(yamlFile, newFile);
                    })
                    .onFailure(ex -> {
                        log.error("keepYamlFiles:yamlFile文件备份失败：ex:{}", ex);
                    });
        }
        File logFile = new File(String.format("%s/logs/metricflow.log", mfConfigPath));
        if (logFile.exists()) {
            Try.run(() -> {
                File newFile = new File(String.format("%s/logs/%s.%s.%s.metricflow.log", metricFlowErrorPath, username, metricName, dateFlag));
                FileUtils.moveFile(logFile, newFile);
            }).onFailure(ex -> {
                log.error("keepYamlFiles:logFile文件备份失败：ex:{}", ex);
            });
        }
    }
}
