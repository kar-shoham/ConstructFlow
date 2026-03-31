package com.constructflow.user_service.filter;

import com.constructflow.user_service.dto.JwtInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class AuthFilter
        extends OncePerRequestFilter
{
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException
    {
        String requestUrl = request.getRequestURI();
        log.info("AuthFilter - Request URL: {}", requestUrl);

        if(requestUrl.contains("/auth") || requestUrl.contains("/login") || requestUrl.contains("/signup")) {
            log.info("AuthFilter - Public endpoint, skipping auth");
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        String username = request.getHeader("X-User-Username");
        String userRole = request.getHeader("X-User-Role");

        log.info("AuthFilter - Headers - userId: {}, username: {}, role: {}", userId, username, userRole);

        if (Objects.isNull(userId) || !StringUtils.hasText(username) || !StringUtils.hasText(userRole)) {
            log.error("AuthFilter - Auth Failed: userId={}, username={}, userRole={}", userId, username, userRole);
            handleError(response, "Authentication Failed!", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JwtInfo jwtInfo = JwtInfo.builder()
                .userId(userId)
                .username(username)
                .userRole(userRole)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(jwtInfo, null, List.of()));
        filterChain.doFilter(request, response);
    }

    private void handleError(
            HttpServletResponse response,
            String message,
            int status)
            throws IOException
    {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}
