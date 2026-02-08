package com.constructflow.entity_service.service;

import com.constructflow.entity_service.dto.EmployeeDto;
import org.jspecify.annotations.NonNull;

public interface EmployeeHelperService
{
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
