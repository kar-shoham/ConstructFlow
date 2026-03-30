package com.constructflow.payroll_service.client;

import com.constructflow.payroll_service.config.SystemUserFeignConfig;
import com.constructflow.payroll_service.dto.TimesheetClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "timesheet-service",
    url = "${timesheet.service.url:http://localhost:8583/api/v1/timesheet}",
    configuration = SystemUserFeignConfig.class
)
public interface TimesheetClient {

    @GetMapping("/customers/{customerId}/timesheets/{timesheetId}")
    TimesheetClientDto getTimesheet(@PathVariable Long customerId, @PathVariable Long timesheetId);

    @PostMapping("/customers/{customerId}/timesheets/{timesheetId}/mark-paid")
    TimesheetClientDto markPaid(@PathVariable Long customerId, @PathVariable Long timesheetId);
}
