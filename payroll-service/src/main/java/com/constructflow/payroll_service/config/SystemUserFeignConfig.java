package com.constructflow.payroll_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemUserFeignConfig {

    private static final Long SYSTEM_USER_ID = 0L;
    private static final String SYSTEM_USER_USERNAME = "payroll-service";
    private static final String SYSTEM_USER_ROLE = "ADMIN";

    @Bean
    public RequestInterceptor systemUserFeignRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(feign.RequestTemplate template) {
                template.header("X-User-Id", SYSTEM_USER_ID.toString());
                template.header("X-User-Username", SYSTEM_USER_USERNAME);
                template.header("X-User-Role", SYSTEM_USER_ROLE);
            }
        };
    }
}
