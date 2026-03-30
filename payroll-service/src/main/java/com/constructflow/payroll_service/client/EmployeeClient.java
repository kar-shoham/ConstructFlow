package com.constructflow.payroll_service.client;

import com.constructflow.payroll_service.config.SystemUserFeignConfig;
import com.constructflow.payroll_service.dto.EmployeeClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "entity-service",
    path = "/api/v1",
    configuration = SystemUserFeignConfig.class
)
public interface EmployeeClient {

    @GetMapping("/customers/{customerId}/employees/{employeeId}")
    EmployeeClientDto getEmployee(@PathVariable Long customerId, @PathVariable Long employeeId);
}
