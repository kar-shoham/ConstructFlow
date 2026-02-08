package com.constructflow.entity_service.service;

import com.constructflow.entity_service.dto.EmployeeDto;
import com.constructflow.entity_service.entity.Employee;
import org.jspecify.annotations.NonNull;

public interface EmployeeHelperService
{
    EmployeeDto get(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId);

    EmployeeDto create(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull EmployeeDto employeeDto);

    EmployeeDto update(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId,
            @NonNull EmployeeDto employeeDto);
}
