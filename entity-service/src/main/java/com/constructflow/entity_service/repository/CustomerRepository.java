package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long>
{
    Optional<Customer> findCustomerByCode(String code);
}
