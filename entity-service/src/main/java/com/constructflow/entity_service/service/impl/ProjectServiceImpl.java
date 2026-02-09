package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.entity.Project;
import com.constructflow.entity_service.enums.Status;
import com.constructflow.entity_service.repository.ProjectRepository;
import com.constructflow.entity_service.service.ProjectService;
import com.constructflow.entity_service.utils.AuthUtils;
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
public class ProjectServiceImpl
        extends AuthService
        implements ProjectService
{
    @Autowired
    private ProjectRepository repository;

    @Autowired
    private AuthUtils authUtils;

    @Override
    public List<Project> list(@NonNull Long customerId)
    {
        return repository.findByCustomerId(customerId);
    }

    @Override
    public Project get(
            @NonNull Long customerId,
            @NonNull Long projectId)
    {
        return repository.findByCustomerIdAndProjectId(customerId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("No Project found with id: " + projectId + " and customerId: " + customerId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Project create(
            @NonNull Long customerId,
            @NonNull Project project)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        if (repository.findByCode(project.getCode()).isPresent()) {
            throw new RuntimeException("Project with code: " + project.getCode() + " already exists!");
        }
        project.setId(null);
        if (Objects.isNull(project.getProjectStatus())) {
            project.setProjectStatus(Status.NOT_STARTED);
        }
        project.setCreatedBy(getLoggedInUserId());
        project.setModifiedBy(getLoggedInUserId());
        return repository.save(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Project update(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Project project)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        Project dbProject = get(customerId, projectId);
        if (!dbProject.getCode().equals(project.getCode())) {
            throw new RuntimeException("Cannot change Project Code!");
        }
        dbProject.setName(project.getName());
        if (Objects.nonNull(project.getProjectStatus())) {
            dbProject.setProjectStatus(project.getProjectStatus());
        }
        if (Objects.nonNull(project.getAddress())) {
            dbProject.setAddress(project.getAddress());
        }
        dbProject.setModifiedBy(getLoggedInUserId());
        return repository.save(dbProject);
    }

    @Override
    public void delete(
            @NonNull Long customerId,
            @NonNull Long projectId)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        Project project = get(customerId, projectId);
        repository.delete(project);
    }
}
