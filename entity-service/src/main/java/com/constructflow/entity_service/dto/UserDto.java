package com.constructflow.entity_service.dto;

import com.constructflow.entity_service.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDto
        extends BaseEntityDto
{
    @NonNull
    private String username;

    @NonNull
    private String email;

    @Enumerated(value = EnumType.STRING)
    private UserRole userRole;
}
