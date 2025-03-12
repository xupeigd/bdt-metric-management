package com.quicksand.bigdata.metric.management.job.services;

import org.springframework.context.annotation.Configuration;

/**
 * @author xuxueli 2019-05-04 22:13:264
 */
@Configuration
public class LoginService {

    public static final String LOGIN_IDENTITY_KEY = "MJS_LOGIN_IDENTITY";

    // @Resource
    // JobUserAutoRepo jobUserAutoRepo;
    // @Resource
    // IdentifyPasswordEncoder identifyPasswordEncoder;

    // public static String makeToken(JobUser jobUser) {
    //     String tokenJson = JacksonUtil.writeValueAsString(jobUser);
    //     String tokenHex = new BigInteger(tokenJson.getBytes()).toString(16);
    //     return tokenHex;
    // }

    // private JobUser parseToken(String tokenHex) {
    //     JobUser jobUser = null;
    //     if (tokenHex != null) {
    //         String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());      // username_password(md5)
    //         jobUser = JacksonUtil.readValue(tokenJson, JobUser.class);
    //     }
    //     return jobUser;
    // }

    // public ReturnT<String> login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean ifRemember) {
    //     // param
    //     if (username == null || username.trim().length() == 0 || password == null || password.trim().length() == 0) {
    //         return new ReturnT<String>(500, I18nUtil.getString("login_param_empty"));
    //     }
    //     // valid passowrd
    //     // XxlJobUser xxlJobUser = xxlJobUserDao.loadByUserName(username);
    //     JobUser jobUser = jobUserAutoRepo.findByUsername(username);
    //     if (jobUser == null) {
    //         return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
    //     }
    //     // String passwordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
    //     String passwordMd5 = identifyPasswordEncoder.encode(password);
    //     if (!passwordMd5.equals(jobUser.getPassword())) {
    //         return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
    //     }
    //     String loginToken = makeToken(jobUser);
    //     // do login
    //     CookieUtil.set(response, LOGIN_IDENTITY_KEY, loginToken, ifRemember);
    //     return ReturnT.SUCCESS;
    // }

    /**
     * logout
     *
     * @param request
     * @param response
     */
    // public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
    //     CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
    //     return ReturnT.SUCCESS;
    // }

    /**
     * logout
     *
     * @param request
     * @return
     */
    // public JobUser ifLogin(HttpServletRequest request, HttpServletResponse response) {
    //     String cookieToken = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
    //     if (cookieToken != null) {
    //         JobUser cookieUser = null;
    //         try {
    //             cookieUser = parseToken(cookieToken);
    //         } catch (Exception e) {
    //             logout(request, response);
    //         }
    //         if (cookieUser != null) {
    //             JobUser dbUser = jobUserAutoRepo.findByUsername(cookieUser.getUsername());
    //             if (dbUser != null) {
    //                 // 0 表示由SS框架带入
    //                 if (0 == cookieUser.getRole()) {
    //                     return cookieUser;
    //                 }
    //                 if (Objects.equals(cookieUser.getPassword(), dbUser.getPassword())) {
    //                     return dbUser;
    //                 }
    //             }
    //         }
    //     }
    //     return null;
    // }

}
