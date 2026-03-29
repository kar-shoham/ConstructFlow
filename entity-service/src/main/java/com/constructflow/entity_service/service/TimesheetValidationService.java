package com.constructflow.entity_service.service;

import com.constructflow.entity_service.dto.TimesheetValidationDto;
import com.constructflow.entity_service.dto.TimesheetValidationResponseDto;
import lombok.NonNull;

import java.util.List;

public interface TimesheetValidationService
{
    TimesheetValidationResponseDto validate(@NonNull TimesheetValidationDto validationDto);

    List<Long> getEmployeeIdsByCompany(@NonNull Long customerId, @NonNull Long companyId);
}
