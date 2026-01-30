package com.constructflow.entity_service.repository;

import com.constructflow.entity_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository
        extends JpaRepository<Task, Long>
{
}
