package com.constructflow.payroll_service.repository;

import com.constructflow.payroll_service.entity.Earning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EarningRepository
        extends JpaRepository<Earning, Long>
{
    List<Earning> findByCustomerId(Long customerId);

    List<Earning> findByEmployeeId(Long employeeId);

    List<Earning> findByCustomerIdAndEmployeeId(
            Long customerId,
            Long employeeId);

    Optional<Earning> findByTimesheetId(Long timesheetId);
}
