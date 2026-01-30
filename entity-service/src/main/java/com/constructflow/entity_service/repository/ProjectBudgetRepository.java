package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.ProjectBudget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectBudgetRepository
        extends JpaRepository<ProjectBudget, Long>
{
}
