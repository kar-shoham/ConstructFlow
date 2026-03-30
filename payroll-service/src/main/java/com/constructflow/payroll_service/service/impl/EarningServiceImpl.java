package com.constructflow.payroll_service.service.impl;

import com.constructflow.payroll_service.entity.Earning;
import com.constructflow.payroll_service.repository.EarningRepository;
import com.constructflow.payroll_service.service.EarningService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EarningServiceImpl implements EarningService {

    @Autowired
    private EarningRepository earningRepository;

    @Override
    public Earning create(@NonNull Earning entity) {
        entity.setId(null);
        entity.setCreatedOn(LocalDateTime.now());
        entity.setModifiedOn(LocalDateTime.now());
        entity.setCreatedBy(0L); // System user for Kafka consumer
        entity.setModifiedBy(0L);
        entity.setActive(true);
        return earningRepository.save(entity);
    }

    @Override
    public List<Earning> findByCustomerId(@NonNull Long customerId) {
        return earningRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Earning> findByEmployeeId(@NonNull Long employeeId) {
        return earningRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<Earning> findByCustomerIdAndEmployeeId(@NonNull Long customerId, @NonNull Long employeeId) {
        return earningRepository.findByCustomerIdAndEmployeeId(customerId, employeeId);
    }
}
