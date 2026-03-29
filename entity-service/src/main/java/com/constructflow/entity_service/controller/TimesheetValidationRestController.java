package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.dto.TimesheetValidationDto;
import com.constructflow.entity_service.dto.TimesheetValidationResponseDto;
import com.constructflow.entity_service.service.TimesheetValidationService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timesheet-data-validation")
public class TimesheetValidationRestController
{
    @Autowired
    private TimesheetValidationService timesheetValidationService;

    @PostMapping
    public ResponseEntity<TimesheetValidationResponseDto> validateData(
            @RequestBody @NonNull TimesheetValidationDto validationDto)
    {
        return ResponseEntity.ok(timesheetValidationService.validate(validationDto));
    }

    @GetMapping("/customers/{customerId}/companies/{companyId}/employee-ids")
    public ResponseEntity<java.util.List<Long>> getEmployeeIdsByCompany(
            @PathVariable("customerId") @NonNull Long customerId,
            @PathVariable("companyId") @NonNull Long companyId)
    {
        return ResponseEntity.ok(timesheetValidationService.getEmployeeIdsByCompany(customerId, companyId));
    }
}
