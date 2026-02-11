package com.constructflow.timesheet_service.repository;


import com.constructflow.timesheet_service.entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

public interface TimesheetRepository
        extends JpaRepository<Timesheet, Long>
{
    Optional<Timesheet> getTimesheetByCustomerIdAndId(
            Long customerId,
            Long id);

    @Query("SELECT t FROM Timesheet t " +
            "WHERE t.customerId = :customerId " +
            "AND (:employeeId IS NULL OR t.employeeId = :employeeId) " +
            "AND (:startDate IS NULL OR t.dateWorked >= :startDate) " +
            "AND (:endDate IS NULL OR t.dateWorked <= :endDate)")
    List<Timesheet> getTimesheetsList(
            @Param("customerId") Long customerId,
            @Param("employeeId") Long employeeId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
