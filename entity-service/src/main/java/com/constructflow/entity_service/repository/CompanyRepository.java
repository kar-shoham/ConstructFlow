package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository
        extends JpaRepository<Company, Long>
{
}
