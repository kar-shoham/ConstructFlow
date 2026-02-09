package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository
        extends JpaRepository<Task, Long>
{
    @Query("FROM Task t WHERE t.project.customer.id= :customerId")
    List<Task> getTaskByCustomerId(@Param("customerId") Long customerId);

    @Query("FROM Task t WHERE t.project.customer.id= :customerId AND t.project.id= :projectId")
    List<Task> getTaskByCustomerIdAndProjectId(
            @Param("customerId") Long customerId,
            @Param("projectId") Long projectId);

    @Query("FROM Task t WHERE t.project.customer.id= :customerId AND t.project.id= :projectId and t.id= :taskId")
    Optional<Task> getTaskByCustomerIdAndProjectIdAndTaskId(
            @Param("customerId") Long customerId,
            @Param("projectId") Long projectId,
            @Param("taskId") Long taskId);

    Optional<Task> getTaskByCode(String code);
}
