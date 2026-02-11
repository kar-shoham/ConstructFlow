package com.constructflow.timesheet_service.client;

import com.constructflow.timesheet_service.dto.TimesheetValidationDto;
import com.constructflow.timesheet_service.dto.TimesheetValidationResponseDto;
import lombok.NonNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "entity-service", path = "/api/v1/timesheet-data-validation")
public interface TimesheetValidationClient
{
    public TimesheetValidationResponseDto validateData(
            @RequestBody @NonNull TimesheetValidationDto validationDto);
}
