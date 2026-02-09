package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository
        extends JpaRepository<Project, Long>
{
    @Query("FROM Project p WHERE p.customer.id= :customerId")
    List<Project> findByCustomerId(@Param("customerId") Long customerId);

    @Query("FROM Project p WHERE p.customer.id= :customerId AND p.id= :projectId")
    Optional<Project> findByCustomerIdAndProjectId(
            @Param("customerId") Long customerId,
            @Param("projectId") Long projectId);

    Optional<Project> findByCode(String code);
}
