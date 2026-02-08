package com.constructflow.entity_service.utils;

import com.constructflow.entity_service.entity.Employee;
import com.constructflow.entity_service.enums.EmployeeRole;
import com.constructflow.entity_service.exception.InsufficientPermissionException;
import com.constructflow.entity_service.repository.EmployeeRepository;
import com.constructflow.entity_service.service.impl.AuthService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils
        extends AuthService
{
    @Autowired
    private EmployeeRepository employeeRepository;

    public void validateAccessForCustomerAdmin(@NonNull Long customerId)
    {
        if (isLoggedUserAdmin()) {
            return;
        }
        Long loggedInUserId = getLoggedInUserId();
        Employee loggedEmployee = employeeRepository.getEmployeeByUserIdWithCompanyAndCustomerId(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("Invalid logged in user!"));
        if (!loggedEmployee.getEmployeeRole().equals(EmployeeRole.CUSTOMER_ADMIN)) {
            throw new InsufficientPermissionException("Employee needs to be Customer Admin to Access this!");
        }
        if (!loggedEmployee.getCompany().getCustomer().getId().equals(customerId)) {
            throw new InsufficientPermissionException("Cannot Access Other Customer's Data");
        }
    }

    public void validateAccessForCompanyAdmin(
            @NonNull Long customerId,
            @NonNull Long companyId)
    {
        if (isLoggedUserAdmin()) {
            return;
        }
        Long loggedInUserId = getLoggedInUserId();
        Employee loggedEmployee = employeeRepository.getEmployeeByUserIdWithCompanyAndCustomerId(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("Invalid logged in user!"));
        if (loggedEmployee.getEmployeeRole().equals(EmployeeRole.WORKER)) {
            throw new InsufficientPermissionException("Employee needs to be Customer Admin to Access this!");
        }
        if (loggedEmployee.getEmployeeRole().equals(EmployeeRole.CUSTOMER_ADMIN) &&
                !loggedEmployee.getCompany().getCustomer().getId().equals(customerId)) {
            throw new InsufficientPermissionException("Cannot Access Other Customer's Data");
        }
        if (loggedEmployee.getEmployeeRole().equals(EmployeeRole.COMPANY_ADMIN) &&
                !loggedEmployee.getCompany().getId().equals(companyId)) {
            throw new InsufficientPermissionException("Cannot Access Other Company's Data");
        }
    }
}
