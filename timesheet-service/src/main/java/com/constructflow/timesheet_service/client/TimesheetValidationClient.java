package com.constructflow.timesheet_service.client;

import com.constructflow.timesheet_service.dto.TimesheetValidationDto;
import com.constructflow.timesheet_service.dto.TimesheetValidationResponseDto;
import lombok.NonNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "entity-service", path = "/api/v1/timesheet-data-validation")
public interface TimesheetValidationClient
{
    @PostMapping
    ResponseEntity<TimesheetValidationResponseDto> validateData(
            @RequestBody @NonNull TimesheetValidationDto validationDto);

    @GetMapping("/customers/{customerId}/companies/{companyId}/employee-ids")
    ResponseEntity<java.util.List<Long>> getEmployeeIdsByCompany(
            @PathVariable("customerId") @NonNull Long customerId,
            @PathVariable("companyId") @NonNull Long companyId);
}
