package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.converter.EmployeeConverter;
import com.constructflow.entity_service.dto.EmployeeDto;
import com.constructflow.entity_service.entity.Employee;
import com.constructflow.entity_service.service.EmployeeHelperService;
import com.constructflow.entity_service.service.EmployeeService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers/{customerId}")
public class EmployeeRestController
{
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeHelperService employeeHelperService;

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> listForCustomer(@PathVariable @NonNull Long customerId)
    {
        List<Employee> entities = employeeService.listForCustomer(customerId);
        return ResponseEntity.ok(entities.stream()
                .map(entity -> EmployeeConverter.instance.toDto(null, entity))
                .collect(Collectors.toUnmodifiableList()));
    }

    @GetMapping("/companies/{companyId}/employees")
    public ResponseEntity<List<EmployeeDto>> list(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long companyId)
    {
        List<Employee> entities = employeeService.list(customerId, companyId);
        return ResponseEntity.ok(entities.stream()
                .map(entity -> EmployeeConverter.instance.toDto(null, entity))
                .collect(Collectors.toUnmodifiableList()));
    }

    @GetMapping("/companies/{companyId}/employees/{employeeId}")
    public ResponseEntity<EmployeeDto> get(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long companyId,
            @PathVariable @NonNull Long employeeId)
    {
        EmployeeDto res = employeeHelperService.get(customerId, companyId, employeeId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/companies/{companyId}/employees")
    public ResponseEntity<EmployeeDto> create(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long companyId,
            @Valid @RequestBody @NonNull EmployeeDto requestDto)
    {
        EmployeeDto res = employeeHelperService.create(customerId, companyId, requestDto);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/companies/{companyId}/employees/{employeeId}")
    public ResponseEntity<EmployeeDto> update(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long companyId,
            @PathVariable @NonNull Long employeeId,
            @Valid @RequestBody @NonNull EmployeeDto requestDto)
    {
        EmployeeDto res = employeeHelperService.update(customerId, companyId, employeeId, requestDto);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/companies/{companyId}/employees/{employeeId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long companyId,
            @PathVariable @NonNull Long employeeId)
    {
        employeeService.delete(customerId, companyId, employeeId);
        return ResponseEntity.ok(true);
    }
}
