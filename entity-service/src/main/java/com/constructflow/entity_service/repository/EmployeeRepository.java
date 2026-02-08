package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long>
{
    @Query("FROM Employee e WHERE e.company.customer.id= :customerId")
    List<Employee> getEmployeesByCustomerId(@Param("customerId") Long customerId);

    @Query("FROM Employee e WHERE e.company.id= :companyId")
    List<Employee> getEmployeesByCompanyId(@Param("companyId") Long companyId);

    Optional<Employee> getEmployeesByUserId(Long userId);

    @Query("FROM Employee e WHERE e.company.id = :companyId AND e.id = :employeeId")
    Optional<Employee> getEmployeesByCompanyIdAndEmployeeId(
            @Param("companyId") Long companyId,
            @Param("employeeId") Long employeeId);

    @Query("FROM Employee e JOIN FETCH e.company c JOIN FETCH c.customer cu WHERE e.userId= :userId")
    Optional<Employee> getEmployeeByUserIdWithCompanyAndCustomerId(@Param("userId") Long userId);
}
