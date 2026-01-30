package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long>
{
}
