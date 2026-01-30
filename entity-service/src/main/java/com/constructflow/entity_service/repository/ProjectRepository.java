package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository
        extends JpaRepository<Project, Long>
{
}
