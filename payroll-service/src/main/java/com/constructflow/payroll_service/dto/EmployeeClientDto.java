package com.constructflow.payroll_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeClientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Double payRate;
    private String employeeType;
    private Long companyId;
}
