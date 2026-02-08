package com.constructflow.entity_service.service;

import com.constructflow.entity_service.entity.Employee;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface EmployeeService
{
    List<Employee> listForCustomer(@NonNull Long customerId);

    List<Employee> list(
            @NonNull Long customerId,
            @NonNull Long companyId);

    Employee get(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId);

    Employee create(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Employee employee);

    Employee update(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId,
            @NonNull Employee employee);

    void delete(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId);
}
