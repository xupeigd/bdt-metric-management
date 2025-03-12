package com.quicksand.bigdata.metric.management.configs;

import com.quicksand.bigdata.query.models.QueryReqModel;
import com.quicksand.bigdata.query.models.QueryRespModel;
import com.quicksand.bigdata.query.rests.QueryRestService;
import com.quicksand.bigdata.vars.http.model.Response;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * FeignClientMockConfiguration
 *
 * @author page
 * @date 2022/8/15
 */
@ConditionalOnProperty(value = "remote.query.enable", havingValue = "false", matchIfMissing = true)
@Configuration
public class FeignClientMockConfiguration {


    @Bean
    QueryRestService queryRestService() {
        return new QueryRestService() {
            @Override
            public Response<QueryRespModel> query(QueryReqModel queryReqModel) {
                return Response.response(HttpStatus.I_AM_A_TEAPOT);
            }

            @Override
            public Response<QueryRespModel> getResp(@Length(min = 4, max = 32, message = "不存在的作业！") String s) {
                return Response.response(HttpStatus.I_AM_A_TEAPOT);
            }
        };
    }

}
