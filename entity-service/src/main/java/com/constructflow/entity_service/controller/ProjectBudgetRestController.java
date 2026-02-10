package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.converter.ProjectBudgetConverter;
import com.constructflow.entity_service.dto.ProjectBudgetDto;
import com.constructflow.entity_service.entity.ProjectBudget;
import com.constructflow.entity_service.service.ProjectBudgetService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers/{customerId}/projects/{projectId}")
public class ProjectBudgetRestController
{
    @Autowired
    private ProjectBudgetService projectBudgetService;

    @GetMapping("/project-budgets")
    public ResponseEntity<List<ProjectBudgetDto>> listAll(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId)
    {
        List<ProjectBudget> entities = projectBudgetService.listAll(customerId, projectId);
        return ResponseEntity.ok(entities.stream()
                .map(x -> ProjectBudgetConverter.instance.toDto(null, x))
                .collect(Collectors.toUnmodifiableList()));
    }

    @GetMapping("/tasks/{taskId}/project-budgets")
    public ResponseEntity<List<ProjectBudgetDto>> list(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId,
            @PathVariable @NonNull Long taskId)
    {
        List<ProjectBudget> entities = projectBudgetService.list(customerId, projectId, taskId);
        return ResponseEntity.ok(entities.stream()
                .map(x -> ProjectBudgetConverter.instance.toDto(null, x))
                .collect(Collectors.toUnmodifiableList()));
    }

    @PostMapping("/tasks/{taskId}/cost-code/{costCodeId}/project-budgets/add")
    public ResponseEntity<ProjectBudgetDto> add(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId,
            @PathVariable @NonNull Long taskId,
            @PathVariable @NonNull Long costCodeId)
    {
        ProjectBudget entity = projectBudgetService.add(customerId, projectId, taskId, costCodeId);
        return ResponseEntity.ok(ProjectBudgetConverter.instance.toDto(null, entity));
    }

    @PostMapping("/tasks/{taskId}/cost-code/{costCodeId}/project-budgets/remove")
    public ResponseEntity<ProjectBudgetDto> remove(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long projectId,
            @PathVariable @NonNull Long taskId,
            @PathVariable @NonNull Long costCodeId)
    {
        ProjectBudget entity = projectBudgetService.remove(customerId, projectId, taskId, costCodeId);
        return ResponseEntity.ok(ProjectBudgetConverter.instance.toDto(null, entity));
    }
}
