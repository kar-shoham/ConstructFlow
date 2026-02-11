package com.constructflow.timesheet_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TimesheetDto
        extends BaseEntityDto
{
    @NotNull(message = "Customer ID cannot be null!")
    private Long customerId;

    @NotNull(message = "Employee ID cannot be null!")
    private Long employeeId;

    @NotNull(message = "Project ID cannot be null!")
    private Long projectId;

    private Long taskId;

    private Long costCodeId;

    @NotNull(message = "Seconds cannot be null!")
    private Duration seconds;

    @NotNull(message = "Date Worked cannot be null!")
    private Date dateWorked;

    @NotNull(message = "Start Time cannot be null!")
    private Time startTime;
}
