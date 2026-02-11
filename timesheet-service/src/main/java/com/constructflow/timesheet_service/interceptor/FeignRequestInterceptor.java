package com.constructflow.timesheet_service.interceptor;

import com.constructflow.timesheet_service.dto.JwtInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestInterceptor
        implements RequestInterceptor
{
    @Override
    public void apply(RequestTemplate template)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication != null && authentication.getPrincipal() instanceof JwtInfo) {
            JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
            template.header("X-User-Id", jwtInfo.getUserId().toString());
            template.header("X-User-Username", jwtInfo.getUsername());
            template.header("X-User-Role", jwtInfo.getUserRole());
        }

    }
}

