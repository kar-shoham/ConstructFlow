package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository
        extends JpaRepository<Company, Long>
{
    @Query("FROM Company c WHERE c.customer.id= :customerId")
    List<Company> getCompaniesByCustomerId(@Param("customerId") Long customerId);

    @Query("FROM Company c WHERE c.customer.id= :customerId and c.id= :companyId")
    Optional<Company> getCompanyByCustomerIdAndCompanyId(
            @Param("customerId") Long customerId,
            @Param("companyId") Long companyId);

    Optional<Company> getCompanyByCode(String code);
}
