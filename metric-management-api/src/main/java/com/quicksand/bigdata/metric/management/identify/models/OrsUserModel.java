package com.quicksand.bigdata.metric.management.identify.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.vars.jwt.JwtToken;
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
public class OrsUserModel {

    /**
     * 签发系统的用户主键
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer id;

    /**
     * 名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 建议过期时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long expired;

    /**
     * 建议识别主键
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String pk;

    /**
     * 手机号码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String mobile;

    /**
     * 邮箱
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String email;

    /**
     * 真实名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String userName;

    /**
     * 员工Id
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long accountId;

    /**
     * 更多
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<JwtToken.SignKeyPair.DefaultSignKeyPair> mores;

}