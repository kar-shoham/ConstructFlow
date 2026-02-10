package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.entity.Employee;
import com.constructflow.entity_service.enums.EmployeeRole;
import com.constructflow.entity_service.enums.EmployeeType;
import com.constructflow.entity_service.repository.EmployeeRepository;
import com.constructflow.entity_service.service.EmployeeService;
import com.constructflow.entity_service.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl
        extends AuthService
        implements EmployeeService
{
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private AuthUtils authUtils;

    @Override
    public List<Employee> listForCustomer(@NonNull Long customerId)
    {
        authUtils.validateAccessForCustomerAdmin(customerId);
        return repository.getEmployeesByCustomerId(customerId);
    }

    @Override
    public List<Employee> list(
            @NonNull Long customerId,
            @NonNull Long companyId)
    {
        authUtils.validateAccessForCompanyAdmin(customerId, companyId);
        return repository.getEmployeesByCompanyId(companyId);
    }

    @Override
    public Employee get(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId)
    {
        return repository.getEmployeesByCompanyIdAndEmployeeId(
                companyId, employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found!"));
    }

    @Override
    public Employee create(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Employee employee)
    {
        authUtils.validateAccessForCompanyAdmin(customerId, companyId);

        if (employee.getEmployeeType() == null) {
            employee.setEmployeeType(EmployeeType.HOURLY);
        }
        if (employee.getEmployeeRole() == null) {
            employee.setEmployeeRole(EmployeeRole.WORKER);
        }
        employee.setId(null);
        employee.setCreatedBy(getLoggedInUserId());
        employee.setCreatedBy(getLoggedInUserId());
        return repository.save(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Employee update(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId,
            @NonNull Employee employee)
    {
        authUtils.validateAccessForCompanyAdmin(customerId, companyId);
        Employee dbEmployee = repository.getEmployeesByCompanyIdAndEmployeeId(
                companyId, employeeId).orElseThrow(() -> new EntityNotFoundException("Employee not found!"));
        modelMapper.map(employee, dbEmployee);
        dbEmployee.setId(employeeId);
        dbEmployee.setCreatedBy(getLoggedInUserId());
        return repository.save(dbEmployee);
    }

    @Override
    public void delete(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId)
    {
        Employee employee = get(customerId, companyId, employeeId);
        employee.setActive(false);
        employee.setModifiedBy(getLoggedInUserId());
        repository.save(employee);
    }
}
