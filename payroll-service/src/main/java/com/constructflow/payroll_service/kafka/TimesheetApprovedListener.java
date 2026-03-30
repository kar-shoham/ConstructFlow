package com.constructflow.payroll_service.kafka;

import com.constructflow.payroll_service.client.EmployeeClient;
import com.constructflow.payroll_service.client.TimesheetClient;
import com.constructflow.payroll_service.dto.EmployeeClientDto;
import com.constructflow.payroll_service.dto.TimesheetClientDto;
import com.constructflow.payroll_service.entity.Earning;
import com.constructflow.payroll_service.entity.EarningStatus;
import com.constructflow.payroll_service.service.EarningService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TimesheetApprovedListener {

    @Autowired
    private EarningService earningService;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private TimesheetClient timesheetClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "timesheet-approved", groupId = "payroll-service-group")
    public void consumeTimesheetApproved(String message) {
        try {
            log.info("Received timesheet-approved message: {}", message);

            Map<String, Object> payload = objectMapper.readValue(message, HashMap.class);
            Long timesheetId = ((Number) payload.get("timesheetId")).longValue();
            Long employeeId = ((Number) payload.get("employeeId")).longValue();
            Long customerId = ((Number) payload.get("customerId")).longValue();

            // Get timesheet details to extract hours worked
            TimesheetClientDto timesheet = timesheetClient.getTimesheet(customerId, timesheetId);
            if (timesheet == null || timesheet.getSeconds() == null) {
                log.error("Failed to get timesheet details for timesheetId: {}", timesheetId);
                return;
            }

            BigDecimal hoursWorked = parseDurationToHours(timesheet.getSeconds());

            // Get employee payRate from entity-service
            EmployeeClientDto employee = employeeClient.getEmployee(customerId, employeeId);
            if (employee == null) {
                log.error("Failed to get employee details for employeeId: {}", employeeId);
                return;
            }

            Double payRateValue = employee.getPayRate();
            if (payRateValue == null || payRateValue == 0) {
                log.warn("Employee {} has no payRate set, using default of $40", employeeId);
                payRateValue = 40.0;
            }
            BigDecimal payRate = BigDecimal.valueOf(payRateValue);

            // Calculate gross amount
            BigDecimal grossAmount = payRate.multiply(hoursWorked);

            // Create earning entry
            Earning earning = Earning.builder()
                    .timesheetId(timesheetId)
                    .employeeId(employeeId)
                    .customerId(customerId)
                    .payRate(payRate)
                    .hoursWorked(hoursWorked)
                    .grossAmount(grossAmount)
                    .status(EarningStatus.COMPLETED)
                    .build();

            earningService.create(earning);
            log.info("Successfully created earning for timesheetId: {} with hoursWorked: {} and grossAmount: {}",
                    timesheetId, hoursWorked, grossAmount);

            // Mark timesheet as PAID
            timesheetClient.markPaid(customerId, timesheetId);
            log.info("Marked timesheet {} as PAID", timesheetId);

        } catch (Exception e) {
            log.error("Error processing timesheet-approved message: {}", message, e);
        }
    }

    /**
     * Parse ISO-8601 duration format (e.g., PT8H30M) to hours as BigDecimal
     */
    private BigDecimal parseDurationToHours(String isoDuration) {
        if (isoDuration == null || !isoDuration.startsWith("PT")) {
            return BigDecimal.ZERO;
        }

        String duration = isoDuration.substring(2).toUpperCase();
        double totalSeconds = 0;

        // Extract hours
        int hIndex = duration.indexOf('H');
        if (hIndex > 0) {
            totalSeconds += Integer.parseInt(duration.substring(0, hIndex)) * 3600;
            duration = duration.substring(hIndex + 1);
        }

        // Extract minutes
        int mIndex = duration.indexOf('M');
        if (mIndex > 0) {
            totalSeconds += Integer.parseInt(duration.substring(0, mIndex)) * 60;
            duration = duration.substring(mIndex + 1);
        }

        // Extract seconds
        int sIndex = duration.indexOf('S');
        if (sIndex > 0) {
            totalSeconds += Integer.parseInt(duration.substring(0, sIndex));
        }

        // Convert to hours
        return BigDecimal.valueOf(totalSeconds / 3600.0).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
