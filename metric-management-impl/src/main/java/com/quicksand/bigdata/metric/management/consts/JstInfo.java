// package com.quicksand.bigdata.metric.management.consts;
//
// import com.quicksand.bigdata.vars.security.consts.VarsSecurityConsts;
// import org.slf4j.MDC;
// import org.springframework.util.CollectionUtils;
//
// import javax.servlet.http.Cookie;
// import javax.servlet.http.HttpServletRequest;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Objects;
// import java.util.stream.Collectors;
//
// /**
//  * JstInfo
//  * (Jwt Share token)
//  *
//  * @author page
//  * @date 2022/9/6
//  */
// public interface JstInfo {
//
//     String FLAG = VarsSecurityConsts.KEY_HEADER_AUTH;
//
//     /**
//      * 判断字符串是否为空
//      *
//      * @param str 字符串
//      * @return true/false
//      */
//     static boolean isEmpty(String str) {
//         return null == str || Objects.equals("", str.trim());
//     }
//
//     /**
//      * 继承/创建jstInfo
//      *
//      * @param request HttpServletRequest
//      * @return 返回jstInfo的字符串
//      */
//     static String extendFromRequest(HttpServletRequest request) {
//         String jstInfo = request.getHeader(JstInfo.FLAG);
//         if (isEmpty(jstInfo)
//                 && null != request.getCookies()
//                 && 0 < request.getCookies().length) {
//             //取cookie
//             List<Cookie> hitCookies = Arrays.stream(request.getCookies())
//                     .filter(v -> FLAG.equals(v.getName()))
//                     .collect(Collectors.toList());
//             if (!CollectionUtils.isEmpty(hitCookies)) {
//                 jstInfo = hitCookies.get(0).getValue();
//             }
//         }
//         if (!isEmpty(jstInfo)) {
//             make(jstInfo);
//         }
//         return jstInfo;
//     }
//
//     /**
//      * 传递jstInfo
//      *
//      * @param jstInfo 上游的jstInfo对象
//      */
//     static void transform(JstInfo jstInfo) {
//         if (!isEmpty(jstInfo.get())) {
//             MDC.put(FLAG, jstInfo.get());
//         }
//     }
//
//     /**
//      * 继承jstInfo
//      * （将本线程的jstInfo转移至下一线程）
//      *
//      * @param jstInfo 下一线程的jstInfo对象
//      * @return 当前线程的jstInfo串
//      */
//     static String extend(JstInfo jstInfo) {
//         String curJstInfo = MDC.get(FLAG);
//         if (!isEmpty(curJstInfo)) {
//             if (null != jstInfo) {
//                 jstInfo.set(curJstInfo);
//             }
//             return curJstInfo;
//         }
//         return "";
//     }
//
//     /**
//      * 获取当前线程的jstInfo
//      *
//      * @return 当前线程的jstInfo/空字符串
//      */
//     static String excavate() {
//         String jstInfo = MDC.get(FLAG);
//         return isEmpty(jstInfo) ? "" : jstInfo;
//     }
//
//     /**
//      * 移除对应的jstInfo
//      */
//     static void destory() {
//         MDC.remove(FLAG);
//     }
//
//     /**
//      * 接受jstInfo
//      *
//      * @param jstInfo 字符串
//      */
//     static void make(String jstInfo) {
//         MDC.put(FLAG, jstInfo);
//     }
//
//     /**
//      * 获取jstInfo
//      *
//      * @return 字符串
//      */
//     String get();
//
//     /**
//      * 设置jstInfo
//      *
//      * @param jstInfo 字符串
//      */
//     void set(String jstInfo);
//
// }
