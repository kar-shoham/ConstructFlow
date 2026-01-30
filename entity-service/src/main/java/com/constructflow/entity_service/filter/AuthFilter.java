package com.constructflow.entity_service.filter;

import com.constructflow.entity_service.dto.JwtInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
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
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        String username = request.getHeader("X-User-Username");
        String userRole = request.getHeader("X-User-Role");

        if (Objects.isNull(userId) || !StringUtils.hasText(username) || !StringUtils.hasText(userRole)) {
            handleError(response, "Authentication Failed!", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JwtInfo jwtInfo = JwtInfo.builder()
                .userId(userId)
                .username(username)
                .userRole(userRole)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(jwtInfo, null));
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
