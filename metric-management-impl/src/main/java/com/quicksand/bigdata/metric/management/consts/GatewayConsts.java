package com.quicksand.bigdata.metric.management.consts;// package com.quicksand.bigdata.metric.management.consts;
//
// import org.springframework.util.StringUtils;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import java.io.IOException;
//
// /**
//  * GatewayConsts
//  *
//  * @author page
//  * @date 2022/10/26
//  */
// public final class GatewayConsts {
//
//     public static final String FLAG_GATEWAY_HOST = "X-Metric-Gateway-Host";
//     public static final String FLAG_VISIT_HOST = "X-Visit-Host";
//
//     private GatewayConsts() {
//     }
//
//     /**
//      * 渲染跳转Url
//      *
//      * @param request     请求
//      * @param relativeUrl 相对url
//      * @return 渲染后的url / 原样的相对url
//      */
//     public static String renderRedirectUrl(HttpServletRequest request, String relativeUrl) {
//         if (relativeUrl.toLowerCase().contains("http")) {
//             return relativeUrl;
//         }
//         String gatewayHost = request.getHeader(FLAG_GATEWAY_HOST);
//         String visHost = request.getHeader(FLAG_VISIT_HOST);
//         return (StringUtils.hasText(gatewayHost) ? gatewayHost : (StringUtils.hasText(visHost) ? visHost : "")) + relativeUrl;
//     }
//
//     public static String resolveBaseHost(HttpServletRequest request) {
//         String url = request.getRequestURL().toString();
//         int index = url.replaceAll("http://", "")
//                 .indexOf("/");
//         String baseHost = url;
//         if (0 < index) {
//             baseHost = url.substring(0, "http://".length() + index);
//         }
//         return baseHost;
//     }
//
//     public static String resolveRelativeUrl(String completeUrl) {
//         if (completeUrl.toLowerCase().startsWith("http:")) {
//             int index = completeUrl.replaceAll("http://", "")
//                     .indexOf("/");
//             if (0 >= index) {
//                 return "/";
//             } else {
//                 return completeUrl.substring(7 + index);
//             }
//         }
//         return completeUrl.startsWith("/") ? completeUrl : ("/" + completeUrl);
//     }
//
//     /**
//      * 重定向
//      *
//      * @param request
//      * @param response
//      * @param relativeUrl
//      * @throws IOException
//      */
//     public static void redirectTo(HttpServletRequest request, HttpServletResponse response, String relativeUrl) throws IOException {
//         relativeUrl = resolveRelativeUrl(relativeUrl);
//         String redirectUrl = renderRedirectUrl(request, relativeUrl);
//         if (redirectUrl.equals(relativeUrl)) {
//             redirectUrl = resolveBaseHost(request) + relativeUrl;
//         }
//         response.sendRedirect(redirectUrl);
//     }
//
//
// }
