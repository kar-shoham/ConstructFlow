package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.converter.ProjectConverter;
import com.constructflow.entity_service.dto.ProjectDto;
import com.constructflow.entity_service.entity.Project;
import com.constructflow.entity_service.service.ProjectService;
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
@RequestMapping("/customers/{customerId}/projects")
public class ProjectRestController
{
    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> list(@PathVariable @NonNull Long customerId)
    {
        List<Project> entities = projectService.list(customerId);
        List<ProjectDto> dtos = entities.stream()
                .map(x -> ProjectConverter.instance.toDto(null, x))
                .collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> get(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId)
    {
        Project entity = projectService.get(customerId, projectId);
        return ResponseEntity.ok(ProjectConverter.instance.toDto(null, entity));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> create(
            @PathVariable @NonNull Long customerId,
            @Valid @RequestBody @NonNull ProjectDto requestDto)
    {
        Project entity = ProjectConverter.instance.toEntity(null, requestDto);
        entity = projectService.create(customerId, entity);
        return ResponseEntity.ok(ProjectConverter.instance.toDto(null, entity));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> update(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId,
            @Valid @RequestBody @NonNull ProjectDto requestDto)
    {
        Project entity = ProjectConverter.instance.toEntity(null, requestDto);
        entity = projectService.update(customerId, projectId, entity);
        return ResponseEntity.ok(ProjectConverter.instance.toDto(null, entity));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId)
    {
        projectService.delete(customerId, projectId);
        return ResponseEntity.ok(true);
    }
}
