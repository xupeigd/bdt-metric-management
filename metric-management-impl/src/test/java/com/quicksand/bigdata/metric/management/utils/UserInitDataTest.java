package com.quicksand.bigdata.metric.management.utils;

import com.quicksand.bigdata.metric.management.identify.securities.assemblies.IdentifyPasswordEncoder;
import com.quicksand.bigdata.metric.management.identify.securities.assemblies.impls.IdentifyPasswordEncoderImpl;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author page
 * @date 2022/9/19
 */
public class UserInitDataTest {

    static String SQL_USERS_TEMPLATES = "INSERT INTO `t_identify_users`(`id`, `create_time`, `create_user_id`, `email`, `mobile`, `name`, `password`, `status`,`update_time`, `update_user_id`)  values %s ;";

    static String SQL_USER_ROLE_TEMPLATES = "INSERT INTO `t_identify_rel_user_role`( `role_id`, `user_id`, `create_time`, `create_user_id`, `status`,`update_time`,`update_user_id`) values %s ;";

    @Test
    public void createUsers() {
        int startId = 69;
        AtomicInteger userStartId = new AtomicInteger(startId);
        String[] createUsers = {"majinan"};
        IdentifyPasswordEncoder ipe = new IdentifyPasswordEncoderImpl();
        @SuppressWarnings("unchecked") List<String> userNames = (List<String>) CollectionUtils.arrayToList(createUsers);
        List<String> userValues = userNames.stream()
                .map(v -> String.format("(%d,CURRENT_TIMESTAMP,0,'%s.quicksand.com','+86','%s.quicksand.com','%s',2,CURRENT_TIMESTAMP,0)", userStartId.incrementAndGet(), v, v, ipe.encode(v)))
                .collect(Collectors.toList());
        String executeUsersSql = String.format(SQL_USERS_TEMPLATES, StringUtils.collectionToCommaDelimitedString(userValues));
        System.out.println(executeUsersSql);
        //角色数据
        AtomicInteger urStartId = new AtomicInteger(startId);
        List<String> urSqls = userNames.stream()
                .map(v -> String.format("(2,%d,CURRENT_TIMESTAMP,0,1,CURRENT_TIMESTAMP,0)", urStartId.incrementAndGet()))
                .collect(Collectors.toList());
        String executeUrSql = String.format(SQL_USER_ROLE_TEMPLATES, StringUtils.collectionToCommaDelimitedString(urSqls));
        System.out.println(executeUrSql);
    }

    @Test
    public void testChengePasswd() {
        System.out.println(new IdentifyPasswordEncoderImpl().encode("metrics@2022MvpADM"));
        System.out.println(new IdentifyPasswordEncoderImpl().encode("metrics@2022MvpDEV"));
    }

    @Test
    public void testHex() {
        System.out.println(Integer.toBinaryString(191));
        System.out.println(Integer.toString(981, 32));
        System.out.println(Integer.toString(32 * 32, 32));
        System.out.println(Integer.parseInt("1", 16));
        System.out.println(Integer.parseInt("ul", 32) ^ Integer.parseInt("100", 32));
    }


}