package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.dto.CurrentEmployeeDto;
import com.constructflow.entity_service.entity.Employee;
import com.constructflow.entity_service.repository.EmployeeRepository;
import com.constructflow.entity_service.service.impl.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Returns the current user's employee record when the user is not an ADMIN.
 * ADMIN users have no employee; 404 is returned so the frontend can treat them as admin.
 */
@RestController
@RequestMapping("/me")
public class MeController extends AuthService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<CurrentEmployeeDto> getCurrentEmployee() {
        if (isLoggedUserAdmin()) {
            return ResponseEntity.notFound().build();
        }
        Long userId = getLoggedInUserId();
        Employee employee = employeeRepository.getEmployeeByUserIdWithCompanyAndCustomerId(userId)
                .orElse(null);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        Long customerId = employee.getCompany().getCustomer().getId();
        Long companyId = employee.getCompany().getId();
        CurrentEmployeeDto dto = CurrentEmployeeDto.builder()
                .id(employee.getId())
                .customerId(customerId)
                .companyId(companyId)
                .employeeRole(employee.getEmployeeRole())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .build();
        return ResponseEntity.ok(dto);
    }
}
