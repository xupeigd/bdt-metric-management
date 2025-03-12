package com.quicksand.bigdata.metric.management.ors.models;

import com.quicksand.bigdata.metric.management.ors.jwt.Jwt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ResignUserModel
 *
 * @author page
 * @date 2022/8/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResignUserModel {

    /**
     * 签发系统的用户主键
     */
    Integer id;

    /**
     * 名称
     */
    String name;

    /**
     * 建议过期时间
     */
    Long expired;

    /**
     * 建议识别主键
     */
    String pk;

    /**
     * 手机号码
     */
    String mobile;

    /**
     * 邮箱
     */
    String email;

    /**
     * 真实名称
     */
    String userName;

    /**
     * 员工Id
     */
    Long accountId;

    /**
     * 更多
     */
    List<Jwt.SignKeyPair.DefaultSignKeyPair> mores;

}
