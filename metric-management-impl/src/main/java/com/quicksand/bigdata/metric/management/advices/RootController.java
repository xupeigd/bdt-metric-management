package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.metric.management.identify.securities.filters.ReferUrlFilter;
import com.quicksand.bigdata.vars.http.TraceId;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.Eulogy;
import com.quicksand.bigdata.vars.util.GatewayConsts;
import io.vavr.control.Try;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RootController
 *
 * @author page
 * @date 2022/8/5
 */
@RestController
public class RootController
        implements ErrorController {

    @Resource
    HttpServletResponse httpResponse;
    @Resource
    HttpServletRequest httpRequest;

    @GetMapping
    public Response<Void> index() {
        return Eulogy.eulogy();
    }

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> error() {
        String referUrl = (String) httpRequest.getSession().getAttribute(ReferUrlFilter.REFER_URL);
        if (StringUtils.hasText(referUrl)) {
            String curUri = httpRequest.getRequestURI();
            String baseUrl = httpRequest.getRequestURL().toString().replace(curUri, "");
            String lastUri = referUrl.replace(baseUrl, "");
            if (!StringUtils.hasText(lastUri) || "/".equals(lastUri)) {
                httpResponse.setStatus(HttpStatus.OK.value());
                return Eulogy.eulogy();
            }
        }
        if (httpResponse.getStatus() == HttpStatus.NOT_FOUND.value()) {
            Response<Void> response = Eulogy.eulogy();
            response.setCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
            return response;
        }
        if (HttpStatus.UNAUTHORIZED.value() <= httpResponse.getStatus()
                && HttpStatus.FORBIDDEN.value() >= httpResponse.getStatus()) {
            if (!StringUtils.hasText(httpRequest.getHeader(TraceId.FLAG))) {
                Try.run(() -> GatewayConsts.redirectTo(httpRequest, httpResponse, "/login"));
                return null;
            } else {
                Response<Void> response = Eulogy.eulogy();
                response.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
                response.setMsg("未授权的访问！");
                return response;
            }
        }
        return Response.response(String.valueOf(httpResponse.getStatus()), "some thing wrong ! ");
    }

}
