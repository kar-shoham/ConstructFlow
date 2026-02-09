package com.constructflow.entity_service.service;

import com.constructflow.entity_service.entity.Project;
import lombok.NonNull;

import java.util.List;

public interface ProjectService
{
    List<Project> list(@NonNull Long customerId);

    Project get(
            @NonNull Long customerId,
            @NonNull Long projectId);

    Project create(
            @NonNull Long customerId,
            @NonNull Project project);

    Project update(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Project project);

    void delete(
            @NonNull Long customerId,
            @NonNull Long projectId);
}
