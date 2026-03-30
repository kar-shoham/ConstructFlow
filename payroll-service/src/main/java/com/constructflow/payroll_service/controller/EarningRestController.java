package com.constructflow.payroll_service.controller;

import com.constructflow.payroll_service.converter.EarningConverter;
import com.constructflow.payroll_service.dto.EarningDto;
import com.constructflow.payroll_service.entity.Earning;
import com.constructflow.payroll_service.service.EarningService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers/{customerId}/earnings")
public class EarningRestController {

    @Autowired
    private EarningService earningService;

    @Autowired
    private EarningConverter earningConverter;

    @GetMapping
    public ResponseEntity<List<EarningDto>> listByCustomer(
            @PathVariable @NonNull Long customerId) {
        List<Earning> earnings = earningService.findByCustomerId(customerId);
        return ResponseEntity.ok(earnings.stream()
                .map(earningConverter::toDto)
                .collect(Collectors.toUnmodifiableList()));
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<List<EarningDto>> listByEmployeeAndCustomer(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long employeeId) {
        List<Earning> earnings = earningService.findByCustomerIdAndEmployeeId(customerId, employeeId);
        return ResponseEntity.ok(earnings.stream()
                .map(earningConverter::toDto)
                .collect(Collectors.toUnmodifiableList()));
    }
}
