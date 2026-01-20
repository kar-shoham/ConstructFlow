package com.constructflow.user_service.dto;

import com.constructflow.user_service.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthResponseDto
{
    private Long userId;

    private String username;

    private String token;

    private UserRole userRole;
}
