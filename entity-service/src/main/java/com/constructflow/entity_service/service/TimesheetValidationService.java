package com.constructflow.entity_service.service;

import com.constructflow.entity_service.dto.TimesheetValidationDto;
import com.constructflow.entity_service.dto.TimesheetValidationResponseDto;
import lombok.NonNull;

public interface TimesheetValidationService
{
    TimesheetValidationResponseDto validate(@NonNull TimesheetValidationDto validationDto);
}
