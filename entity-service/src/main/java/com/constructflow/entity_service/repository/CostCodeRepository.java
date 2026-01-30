package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.CostCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostCodeRepository
        extends JpaRepository<CostCode, Long>
{
}
