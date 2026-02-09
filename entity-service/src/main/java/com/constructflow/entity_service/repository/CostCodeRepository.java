package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.CostCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CostCodeRepository
        extends JpaRepository<CostCode, Long>
{
    @Query("FROM CostCode c WHERE c.customer.id= :customerId")
    List<CostCode> findByCustomerId(@Param("customerId") Long customerId);

    @Query("FROM CostCode c WHERE c.customer.id= :customerId AND c.id= :costCodeId")
    Optional<CostCode> findByCustomerIdAndCostCodeId(
            @Param("customerId") Long customerId,
            @Param("costCodeId") Long costCodeId);

    Optional<CostCode> findByCode(String code);
}
