package com.constructflow.entity_service.converter;

import com.constructflow.entity_service.dto.ProjectBudgetDto;
import com.constructflow.entity_service.entity.CostCode;
import com.constructflow.entity_service.entity.ProjectBudget;
import com.constructflow.entity_service.entity.Task;
import com.constructflow.entity_service.enums.ObjectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class ProjectBudgetConverter
        extends Converter<ProjectBudget, ProjectBudgetDto>
{
    public static ProjectBudgetConverter instance = new ProjectBudgetConverter();


    public ProjectBudgetConverter()
    {
        super(ProjectBudgetConverter::fromEntity, ProjectBudgetConverter::fromDto);
    }

    private static ProjectBudgetDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @NonNull ProjectBudget entity)
    {
        return ProjectBudgetDto.builder()
                .id(entity.getId())
                .taskId(entity.getTask().getId())
                .costCodeId(entity.getCostCode().getId())
                .costCode(entity.getCostCode().getCode())
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdOn(entity.getCreatedOn())
                .modifiedOn(entity.getModifiedOn())
                .build();
    }

    private static ProjectBudget fromDto(
            @Nullable Map<ObjectType, Object> references,
            @NonNull ProjectBudgetDto dto)
    {
        return ProjectBudget.builder()
                .id(dto.getId())
                .task(Task.builder().id(dto.getTaskId()).build())
                .costCode(CostCode.builder().id(dto.getCostCodeId()).build())
                .createdBy(dto.getCreatedBy())
                .modifiedBy(dto.getModifiedBy())
                .createdOn(dto.getCreatedOn())
                .modifiedOn(dto.getModifiedOn())
                .build();

    }
}