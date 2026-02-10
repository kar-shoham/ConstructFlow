package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.entity.CostCode;
import com.constructflow.entity_service.entity.ProjectBudget;
import com.constructflow.entity_service.entity.Task;
import com.constructflow.entity_service.repository.CostCodeRepository;
import com.constructflow.entity_service.repository.ProjectBudgetRepository;
import com.constructflow.entity_service.repository.TaskRepository;
import com.constructflow.entity_service.service.ProjectBudgetService;
import com.constructflow.entity_service.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectBudgetServiceImpl
        extends AuthService
        implements ProjectBudgetService
{

    private final TaskRepository taskRepository;
    private final CostCodeRepository costCodeRepository;
    private final ProjectBudgetRepository repository;

    private final AuthUtils authUtils;

    @Override
    public List<ProjectBudget> listAll(
            @NonNull Long customerId,
            @NonNull Long projectId)
    {
        return repository.getProjectBudgetByCustomerAndProject(customerId, projectId);
    }

    @Override
    public List<ProjectBudget> list(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId)
    {
        return repository.getProjectBudgetByCustomerAndProjectAndTask(customerId, projectId, taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectBudget add(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId,
            @NonNull Long costCodeId)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        ProjectBudget pb = repository.getActiveInactiveBudget(
                customerId, projectId, taskId, costCodeId).orElse(null);
        if (Objects.nonNull(pb)) {
            pb.setActive(true);
            pb.getCostCode().getCode();
            return repository.save(pb);
        }
        Task task = taskRepository.getTaskByCustomerIdAndProjectIdAndTaskId(customerId, projectId, taskId)
                .orElseThrow(() -> new EntityNotFoundException("No task with id: " + taskId + " found!"));
        CostCode costCode = costCodeRepository.findByCustomerIdAndCostCodeId(customerId, costCodeId)
                .orElseThrow(() -> new EntityNotFoundException("No cost code with id: " + costCodeId + " found!"));
        pb = ProjectBudget.builder()
                .task(task)
                .costCode(costCode)
                .active(true)
                .build();
        return repository.save(pb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectBudget remove(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId,
            @NonNull Long costCodeId)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        ProjectBudget dbProjectBudget = repository.getBudget(customerId, projectId, taskId, costCodeId)
                .orElseThrow(() -> new EntityNotFoundException("No Project Budget to Delete"));
        dbProjectBudget.setActive(false);
        return repository.save(dbProjectBudget);
    }
}
