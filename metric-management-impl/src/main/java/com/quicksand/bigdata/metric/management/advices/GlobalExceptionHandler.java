package com.quicksand.bigdata.metric.management.advices;

import com.quicksand.bigdata.metric.management.identify.utils.AuthUtil;
import com.quicksand.bigdata.vars.http.TraceId;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.security.consts.VarsSecurityConsts;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.GatewayConsts;
import com.quicksand.bigdata.vars.util.JsonUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.net.BindException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

/**
 * GolbalExceptionHandler
 *
 * @author page
 * @date 2022/7/28
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Resource
    HttpServletResponse httpServletResponse;
    @Resource
    HttpServletRequest httpServletRequest;


    /**
     * 参数绑定异常全局处理器
     *
     * @param ex  异常
     * @param <T> 泛型
     * @return instance of ResponseModel
     */
    private <T> Response<T> handleValidationException(Exception ex) {
        Response<T> response = Response.response(HttpStatus.BAD_REQUEST);
        httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        if (StringUtils.hasText(ex.getMessage())) {
            response.setDebugMessage(ex.getMessage());
            response.setMsg(ex.getMessage());
        }
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;
            if (StringUtils.hasText(constraintViolationException.getMessage())) {
                response.setMsg(constraintViolationException.getMessage());
            }
        } else if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            if (!CollectionUtils.isEmpty(fieldErrors)) {
                StringBuilder errorBuilder = new StringBuilder(100);
                errorBuilder.append("参数验证异常:");
                for (FieldError fieldError : fieldErrors) {
                    errorBuilder.append(fieldError.getField()).append("=").append(fieldError.getDefaultMessage()).append(";");
                }
                response.setMsg(errorBuilder.toString());
            }
        } else if (ex instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException httpMessageNotReadableException = (HttpMessageNotReadableException) ex;
            response.setMsg("请求参数转换异常");
        }
        return response;
    }

    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    public <T> Response<T> handleException(Exception ex) {
        log.warn("GlobalExceptionHandler ： some thing wrong !", ex);
        Response<T> response = null;
        if (ex instanceof AccessDeniedException) {
            UserSecurityDetails userDetail = AuthUtil.getUserDetail();
            boolean redirctLogin = null == userDetail || !userDetail.isValidation() || !userDetail.isConsensus();
            if (StringUtils.hasText(httpServletRequest.getHeader(VarsSecurityConsts.KEY_HEADER_AUTH))) {
                httpServletResponse.setHeader(VarsSecurityConsts.KEY_HEADER_BLOCK_AUTH, new Date().toString());
            }
            if (redirctLogin) {
                if (!StringUtils.hasText(httpServletRequest.getHeader(TraceId.FLAG))) {
                    Try.run(() -> GatewayConsts.redirectTo(httpServletRequest, httpServletResponse, "/login"));
                } else {
                    httpServletResponse.setStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
                    response = Response.response(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "未登陆/授权信息已过期，请重新登陆！");
                }
            } else {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                response = Response.response(HttpStatus.FORBIDDEN, "未授权的访问！");
            }
        } else if (ex instanceof BindException
                || ex instanceof MethodArgumentNotValidException
                || ex instanceof ValidationException
                || ex instanceof IllegalArgumentException
                || ex instanceof HttpMessageNotReadableException) {
            response = handleValidationException(ex);
        } else if (ex instanceof SQLIntegrityConstraintViolationException) {
            httpServletResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response = Response.response(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
            response.setDebugMessage(ex.getMessage());
        } else {
            httpServletResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response = Response.response(HttpStatus.SERVICE_UNAVAILABLE, "错误的请求！");
        }
        if (null != response) {
            response.setDebugMessage(StringUtils.hasText(ex.getMessage())
                    ? ex.getMessage()
                    : Try.of(() -> JsonUtils.toJsonString(ex)).getOrElse("ex toJson err! "));
        }
        return response;
    }

}
