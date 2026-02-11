package com.constructflow.entity_service.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetValidationDto
{
    private Long customerId;
    private Long employeeId;
    private Long projectId;
    private Long taskId;
    private Long costCodeId;
}
