package com.constructflow.timesheet_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Timesheet
        extends BaseEntity
{
    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Long projectId;

    private Long taskId;

    private Long costCodeId;

    @Column(nullable = false)
    private Duration seconds;

    @Column(nullable = false)
    private LocalDateTime startTime;
}
