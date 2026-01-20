package com.constructflow.user_service.controller;

import com.constructflow.user_service.dto.AuthRequestDto;
import com.constructflow.user_service.dto.AuthResponseDto;
import com.constructflow.user_service.service.AuthService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController
{
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @NonNull AuthRequestDto requestDto)
    {
        AuthResponseDto responseDto = authService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody @NonNull AuthRequestDto requestDto)
    {
        AuthResponseDto responseDto = authService.signup(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
