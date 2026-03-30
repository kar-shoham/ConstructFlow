package com.constructflow.payroll_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@SQLRestriction("active = true")
public class Earning extends BaseEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private Long timesheetId;

    @Column(nullable = false, updatable = false)
    private Long employeeId;

    @Column(nullable = false, updatable = false)
    private Long customerId;

    @Column(nullable = false, updatable = false)
    private BigDecimal payRate;

    @Column(nullable = false, updatable = false)
    private BigDecimal hoursWorked;

    @Column(nullable = false)
    private BigDecimal grossAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EarningStatus status = EarningStatus.COMPLETED;
}
