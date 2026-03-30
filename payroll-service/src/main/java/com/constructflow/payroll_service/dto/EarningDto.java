package com.constructflow.payroll_service.dto;

import com.constructflow.payroll_service.entity.EarningStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EarningDto extends BaseEntityDto {
    private Long timesheetId;
    private Long employeeId;
    private Long customerId;
    private BigDecimal payRate;
    private BigDecimal hoursWorked;
    private BigDecimal grossAmount;
    private EarningStatus status;
}
