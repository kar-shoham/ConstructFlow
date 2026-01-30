package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository
        extends JpaRepository<Customer, Long>
{
}
