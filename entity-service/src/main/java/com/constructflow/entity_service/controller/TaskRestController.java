package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.converter.TaskConverter;
import com.constructflow.entity_service.dto.TaskDto;
import com.constructflow.entity_service.entity.Task;
import com.constructflow.entity_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers/{customerId}")
public class TaskRestController
{
    @Autowired
    private TaskService taskService;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> listAll(@PathVariable @NonNull Long customerId)
    {
        List<Task> entities = taskService.listAll(customerId);
        List<TaskDto> dtos = entities.stream()
                .map(x -> TaskConverter.instance.toDto(null, x))
                .collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskDto>> list(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId)
    {
        List<Task> entities = taskService.list(customerId, projectId);
        List<TaskDto> dtos = entities.stream()
                .map(x -> TaskConverter.instance.toDto(null, x))
                .collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskDto> get(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId,
            @PathVariable @NonNull Long taskId)
    {
        Task entity = taskService.get(customerId, projectId, taskId);
        return ResponseEntity.ok(TaskConverter.instance.toDto(null, entity));
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskDto> create(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId,
            @Valid @RequestBody @NonNull TaskDto dto)
    {
        Task entity = TaskConverter.instance.toEntity(null, dto);
        entity = taskService.create(customerId, projectId, entity);
        return ResponseEntity.ok(TaskConverter.instance.toDto(null, entity));
    }

    @PutMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<TaskDto> update(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId,
            @PathVariable @NonNull Long taskId,
            @Valid @RequestBody @NonNull TaskDto dto)
    {
        Task entity = TaskConverter.instance.toEntity(null, dto);
        entity = taskService.update(customerId, projectId, taskId, entity);
        return ResponseEntity.ok(TaskConverter.instance.toDto(null, entity));
    }

    @DeleteMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId,
            @PathVariable @NonNull Long taskId)
    {
        taskService.delete(customerId, projectId, taskId);
        return ResponseEntity.ok(true);
    }
}
