package com.constructflow.timesheet_service.entity;

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

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@SQLRestriction("active = true")
public class Timesheet
        extends BaseEntity
{
    @Column(nullable = false, updatable = false)
    private Long customerId;

    @Column(nullable = false, updatable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Long projectId;

    private Long taskId;

    private Long costCodeId;

    @Column(nullable = false)
    private Duration seconds;

    @Column(nullable = false)
    private Date dateWorked;

    @Column(nullable = false)
    private Time startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'SUBMITTED'")
    private TimesheetStatus status = TimesheetStatus.SUBMITTED;
}
