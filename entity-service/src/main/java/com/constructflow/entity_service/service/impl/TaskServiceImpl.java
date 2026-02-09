package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.entity.Project;
import com.constructflow.entity_service.entity.Task;
import com.constructflow.entity_service.enums.Status;
import com.constructflow.entity_service.repository.ProjectRepository;
import com.constructflow.entity_service.repository.TaskRepository;
import com.constructflow.entity_service.service.TaskService;
import com.constructflow.entity_service.utils.AuthUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class TaskServiceImpl
        extends AuthService
        implements TaskService
{
    @Autowired
    private TaskRepository repository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AuthUtils authUtils;

    @Override
    public List<Task> listAll(@NonNull Long customerId)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        return repository.getTaskByCustomerId(customerId);
    }

    @Override
    public List<Task> list(
            @NonNull Long customerId,
            @NonNull Long projectId)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        return repository.getTaskByCustomerIdAndProjectId(customerId, projectId);
    }

    @Override
    public Task get(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId)
    {
        return repository.getTaskByCustomerIdAndProjectIdAndTaskId(customerId, projectId, taskId).orElseThrow(() ->
                new EntityNotFoundException("Task not found!"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task create(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Task entity)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        if (repository.getTaskByCode(entity.getCode()).isPresent()) {
            throw new EntityExistsException("Task with Code: " + entity.getCode() + " already exists!");
        }
        Project project = projectRepository.findByCustomerIdAndProjectId(customerId, projectId).orElseThrow(() ->
                new EntityNotFoundException("No project exists with projectId: " + projectId + " and customerId: " + customerId)
        );
        entity.setId(null);
        if (Objects.isNull(entity.getTaskStatus())) {
            entity.setTaskStatus(Status.NOT_STARTED);
        }
        entity.setProject(project);
        entity.setCreatedBy(getLoggedInUserId());
        entity.setModifiedBy(getLoggedInUserId());
        return repository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task update(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId,
            @NonNull Task task)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        Task dbTask = repository.getTaskByCustomerIdAndProjectIdAndTaskId(customerId, projectId, taskId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Task not found!"));

        if (!dbTask.getCode().equals(task.getCode())) {
            throw new RuntimeException("Cannot change Task Code!");
        }
        dbTask.setName(task.getName());
        if (Objects.nonNull(task.getTaskStatus())) {
            dbTask.setTaskStatus(task.getTaskStatus());
        }
        dbTask.setModifiedBy(getLoggedInUserId());
        return repository.save(dbTask);
    }

    @Override
    public void delete(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);

    }
}
