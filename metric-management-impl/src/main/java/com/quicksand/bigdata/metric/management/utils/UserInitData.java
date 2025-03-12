package com.quicksand.bigdata.metric.management.utils;

import com.quicksand.bigdata.metric.management.identify.dbvos.UserDBVO;

import java.util.ArrayList;
import java.util.List;

/**
 * UserInitData
 *
 * @author page
 * @date 2022/8/19
 */
public interface UserInitData {

    static List<UserDBVO> initDatas() {
        // List<UserDBVO> initDatas = new ArrayList<>();
        // Date initDate = new Date();
        // initDatas.add(new UserDBVO(1, "admin", "a00cc0907e748703a05371a3e2611ba5", "admin@m3.com",
        //         "", 0, initDate, 0, initDate, RoleInitData.initDatas(), new ArrayList<>(),
        //         new ArrayList<>(), UserStatus.ACTIVE));
        // return initDatas;
        return null;
    }

    static List<String> initSqls() {
        List<String> sqls = new ArrayList<>();
        //users
        sqls.add("INSERT INTO `t_identify_users`(`id`, `create_time`, `create_user_id`, `email`, `mobile`, `name`, `password`, `status`,`update_time`, `update_user_id`) " +
                "VALUES (1, '2022-07-30 22:05:00', 0, 'admin.quicksand.com', '', 'admin', 'a00cc0907e748703a05371a3e2611ba5', 2,'2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_users`(`id`, `create_time`, `create_user_id`, `email`, `mobile`, `name`, `password`, `status`,`update_time`, `update_user_id`) " +
                "VALUES (2, '2022-07-30 22:05:00', 0, 'dev.quicksand.com', '', 'dev', 'aee0fe800b042bf4d4495c6ce470a7f1', 2,'2022-07-30 22:05:00', 0);");
        // relations of user and role
        sqls.add(" INSERT INTO `t_identify_rel_user_role`(`id`, `role_id`, `user_id`, `create_time`, `create_user_id`, `status`,`update_time`,`update_user_id`) " +
                " VALUES(1, 1, 1,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        sqls.add("INSERT INTO `t_identify_rel_user_role`(`id`, `role_id`, `user_id`, `create_time`, `create_user_id`, `status`,`update_time`,`update_user_id`) " +
                " VALUES(2, 1, 2,  '2022-07-30 22:05:00', 0, 1,  '2022-07-30 22:05:00', 0);");
        return sqls;
    }

}
