package com.constructflow.user_service.service;

import com.constructflow.user_service.dto.AuthRequestDto;
import com.constructflow.user_service.dto.AuthResponseDto;
import lombok.NonNull;

public interface AuthService
{
    AuthResponseDto login(@NonNull AuthRequestDto requestDto);

    AuthResponseDto signup(@NonNull AuthRequestDto requestDto);
}
