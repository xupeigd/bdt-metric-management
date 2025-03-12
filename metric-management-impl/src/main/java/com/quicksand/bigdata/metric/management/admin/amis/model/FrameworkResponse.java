package com.quicksand.bigdata.metric.management.admin.amis.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quicksand.bigdata.vars.http.model.Response;
import com.quicksand.bigdata.vars.util.JsonUtils;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * Class FrameworkResponse
 *
 * @Author: ap100
 * @Date: 2024/9/3
 * @Description: FrameworkResponse
 */
@Data
public class FrameworkResponse<T, D>
        extends Response<T> {

    /**
     * 状态
     * <p>
     * 0 成功 1失败
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer status;

    @JsonAlias("_debug")
    @JsonProperty("_debug")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    D debug;

    /**
     * 不展示toast
     * <p>
     * 0 是 1 否
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer doNotDisplayToast;

    public static <T, D> FrameworkResponse<T, D> frameworkResponse(@Nullable T data, @Nullable D debug, int status, String msg) {
        FrameworkResponse<T, D> response = new FrameworkResponse<>();
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        response.setStatus(status);
        response.setData(data);
        response.setDebug(debug);
        response.setMsg(msg);
        return response;
    }

    public static final <T, D> FrameworkResponse<T, D> extend(Response<T> restResponse) {
        @SuppressWarnings("unchecked") FrameworkResponse<T, D> response = (FrameworkResponse<T, D>) JsonUtils.transfrom(restResponse, FrameworkResponse.class);
        assert null != response;
        response.setCode(restResponse.getCode());
        response.setStatus(Objects.equals(String.valueOf(HttpStatus.OK.value()), restResponse.getCode()) ? 0 : 1);
        return response;
    }

    public static <T, D> FrameworkResponse<T, D> frameworkResponse(int status, String msg) {
        return frameworkResponse(null, null, status, msg);
    }

}
