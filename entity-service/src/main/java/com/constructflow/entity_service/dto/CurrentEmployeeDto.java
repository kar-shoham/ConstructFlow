package com.constructflow.entity_service.dto;

import com.constructflow.entity_service.enums.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentEmployeeDto {

    private Long id;
    private Long customerId;
    private Long companyId;
    private EmployeeRole employeeRole;
    private String firstName;
    private String lastName;
}
