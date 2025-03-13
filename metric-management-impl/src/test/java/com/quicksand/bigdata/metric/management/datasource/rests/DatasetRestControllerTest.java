package com.quicksand.bigdata.metric.management.datasource.rests;

import com.quicksand.bigdata.metric.management.datasource.models.DatasetModifyModel;
import com.quicksand.bigdata.vars.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * DatasetRestControllerTest
 *
 * @author page
 * @date 2022/8/15
 */
public class DatasetRestControllerTest {

    @Test
    public void genernateJson4CreateDataset() {
        DatasetModifyModel dmm = new DatasetModifyModel();
        dmm.setCluster(1);
        dmm.setName("Mock_Dataset");
        dmm.setDescription("虚拟的Dataset");
//        dmm.setOwners(Collections.singletonList(1));
        dmm.setTableName("t_identify_users");
        dmm.setPrimaryKey("id");
        System.out.println(JsonUtils.toJsonString(dmm));
    }

}
