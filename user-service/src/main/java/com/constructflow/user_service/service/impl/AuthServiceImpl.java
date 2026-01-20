package com.constructflow.user_service.service.impl;

import com.constructflow.user_service.dto.AuthRequestDto;
import com.constructflow.user_service.dto.AuthResponseDto;
import com.constructflow.user_service.entity.CFUser;
import com.constructflow.user_service.enums.UserRole;
import com.constructflow.user_service.service.AuthService;
import com.constructflow.user_service.service.UserService;
import com.constructflow.user_service.utils.JwtUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl
        implements AuthService
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Override
    public AuthResponseDto login(@NonNull AuthRequestDto requestDto)
    {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        CFUser user = (CFUser) authentication.getPrincipal();
        String token = jwtUtils.getJwtToken(user);
        return AuthResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .userRole(user.getUserRole())
                .token(token)
                .build();
    }

    @Override
    public AuthResponseDto signup(@NonNull AuthRequestDto requestDto)
    {
        CFUser user = CFUser.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .userRole(UserRole.USER)
                .build();

        user = userService.create(user);

        String token = jwtUtils.getJwtToken(user);
        return AuthResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .userRole(user.getUserRole())
                .token(token)
                .build();
    }
}
