package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.ProjectBudget;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectBudgetRepository
        extends JpaRepository<ProjectBudget, Long>
{
    @Query("SELECT pb FROM ProjectBudget pb " +
            "JOIN FETCH pb.costCode " +
            "INNER JOIN pb.task t " +
            "INNER JOIN t.project p " +
            "WHERE p.customer.id = :customerId " +
            "AND p.id = :projectId ")
    List<ProjectBudget> getProjectBudgetByCustomerAndProject(
            @Param("customerId") @NonNull Long customerId,
            @Param("projectId") @NonNull Long projectId);

    @Query("SELECT pb FROM ProjectBudget pb " +
            "JOIN FETCH pb.costCode " +
            "INNER JOIN pb.task t " +
            "INNER JOIN t.project p " +
            "WHERE p.customer.id = :customerId " +
            "AND p.id = :projectId " +
            "AND t.id = :taskId ")
    List<ProjectBudget> getProjectBudgetByCustomerAndProjectAndTask(
            @Param("customerId") @NonNull Long customerId,
            @Param("projectId") @NonNull Long projectId,
            @Param("taskId") @NonNull Long taskId);

    @Query(value = "SELECT pb.* FROM project_budget pb " +
            "INNER JOIN cost_code cc ON pb.cost_code_id = cc.id " +
            "INNER JOIN task t ON pb.task_id = t.id " +
            "INNER JOIN project p ON t.project_id = p.id " +
            "WHERE p.customer_id = :customerId " +
            "AND p.id = :projectId " +
            "AND t.id = :taskId " +
            "AND cc.id = :costCodeId ", nativeQuery = true)
    Optional<ProjectBudget> getActiveInactiveBudget(
            @Param("customerId") @NonNull Long customerId,
            @Param("projectId") @NonNull Long projectId,
            @Param("taskId") @NonNull Long taskId,
            @Param("costCodeId") @NonNull Long costCodeId);

    @Query("SELECT pb FROM ProjectBudget pb " +
            "JOIN FETCH pb.costCode " +
            "INNER JOIN pb.task t " +
            "INNER JOIN t.project p " +
            "WHERE p.customer.id = :customerId " +
            "AND p.id = :projectId " +
            "AND t.id = :taskId " +
            "AND pb.costCode.id = :costCodeId ")
    Optional<ProjectBudget> getBudget(
            @Param("customerId") @NonNull Long customerId,
            @Param("projectId") @NonNull Long projectId,
            @Param("taskId") @NonNull Long taskId,
            @Param("costCodeId") @NonNull Long costCodeId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM project_budget pb " +
            "WHERE pb.task_id = :taskId AND pb.cost_code_id = :costCodeId " +
            "AND pb.active)",
    nativeQuery = true)
    boolean isValidBudget(
            @Param("taskId") Long taskId,
            @Param("costCodeId") Long costCodeId);
}
