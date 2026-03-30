package com.constructflow.payroll_service.service;

import com.constructflow.payroll_service.entity.Earning;
import lombok.NonNull;

import java.util.List;

public interface EarningService {
    Earning create(@NonNull Earning entity);

    List<Earning> findByCustomerId(@NonNull Long customerId);

    List<Earning> findByEmployeeId(@NonNull Long employeeId);

    List<Earning> findByCustomerIdAndEmployeeId(@NonNull Long customerId, @NonNull Long employeeId);
}
