package com.constructflow.payroll_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetClientDto {
    private Long id;
    private Long customerId;
    private Long employeeId;
    private Long projectId;
    private Long taskId;
    private Long costCodeId;
    private String seconds;
    private String dateWorked;
    private String startTime;
    private String status;
}
