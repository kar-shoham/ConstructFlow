package com.constructflow.entity_service.service;

import com.constructflow.entity_service.entity.Task;
import lombok.NonNull;

import java.util.List;

public interface TaskService
{
    List<Task> listAll(@NonNull Long customerId);

    List<Task> list(
            @NonNull Long customerId,
            @NonNull Long projectId);

    Task get(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId);

    Task create(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Task entity);

    Task update(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId,
            @NonNull Task entity);

    void delete(
            @NonNull Long customerId,
            @NonNull Long projectId,
            @NonNull Long taskId);
}