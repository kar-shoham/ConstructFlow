package com.constructflow.timesheet_service.service.impl;

import com.constructflow.timesheet_service.dto.JwtInfo;
import com.constructflow.timesheet_service.exception.InsufficientPermissionException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthService
{
    protected void onlyAdmin()
    {
        if (!isLoggedUserAdmin()) {
            throw new InsufficientPermissionException();
        }
    }

    protected boolean isLoggedUserAdmin()
    {
        JwtInfo info = getAuthInfo();
        return info.getUserRole().equals("ADMIN");
    }

    protected Long getLoggedInUserId()
    {
        JwtInfo info = getAuthInfo();
        return info.getUserId();
    }

    private JwtInfo getAuthInfo()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (JwtInfo) authentication.getPrincipal();
    }
}
