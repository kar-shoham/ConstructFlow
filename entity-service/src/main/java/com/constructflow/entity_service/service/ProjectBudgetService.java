package com.constructflow.entity_service.service;

import com.constructflow.entity_service.entity.ProjectBudget;
import lombok.NonNull;

import java.util.List;

public interface ProjectBudgetService
{
    List<ProjectBudget> listAll(
            @NonNull Long customerId,
            @NonNull Long projectId);

    List<ProjectBudget> list(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId);

    ProjectBudget add(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId,
            @NonNull Long costCodeId);

    ProjectBudget remove(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId,
            @NonNull Long costCodeId);
}
