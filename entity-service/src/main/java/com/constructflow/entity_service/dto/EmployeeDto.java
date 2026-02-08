package com.constructflow.entity_service.dto;

import com.constructflow.entity_service.enums.EmployeeRole;
import com.constructflow.entity_service.enums.EmployeeType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class EmployeeDto extends BaseEntityDto {
    @NotBlank(message = "Employee First Name is Mandatory!")
    private String firstName;
    @NotBlank(message = "Employee Last Name is Mandatory!")
    private String lastName;
    private Double payRate;
    private EmployeeType employeeType;
    private EmployeeRole employeeRole;
    private Long companyId;
    private AddressDto address;
    private Long userId;
    @Builder.Default
    private Boolean active = true;

    // user fields
    @NotBlank(message = "Username is Mandatory!")
    private String username;
    @NotBlank(message = "Email is Mandatory!")
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
