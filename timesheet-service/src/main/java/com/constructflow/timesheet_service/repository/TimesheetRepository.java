package com.constructflow.timesheet_service.repository;


import com.constructflow.timesheet_service.entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimesheetRepository
        extends JpaRepository<Timesheet, Long>
{
    Optional<Timesheet> getTimesheetByCustomerIdAndId(
            Long customerId,
            Long id);

    List<Timesheet> findByCustomerId(Long customerId);
}
