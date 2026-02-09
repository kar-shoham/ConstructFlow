package com.constructflow.entity_service.converter;

import com.constructflow.entity_service.dto.TaskDto;
import com.constructflow.entity_service.entity.Project;
import com.constructflow.entity_service.entity.Task;
import com.constructflow.entity_service.enums.ObjectType;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class TaskConverter
        extends Converter<Task, TaskDto>
{
    public static TaskConverter instance = new TaskConverter();

    TaskConverter()
    {
        super(TaskConverter::fromEntity, TaskConverter::fromDto);
    }

    private static TaskDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @Nullable Task entity)
    {
        if (Objects.isNull(entity)) {
            return null;
        }

        return TaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .taskStatus(entity.getTaskStatus())
                .projectId(entity.getProject().getId())
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdOn(entity.getCreatedOn())
                .modifiedOn(entity.getModifiedOn())
                .build();
    }

    private static Task fromDto(
            @Nullable Map<ObjectType, Object> references,
            @Nullable TaskDto dto)
    {
        if (Objects.isNull(dto)) {
            return null;
        }

        return Task.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .taskStatus(dto.getTaskStatus())
                .project(Project.builder().id(dto.getProjectId()).build())
                .createdBy(dto.getCreatedBy())
                .modifiedBy(dto.getModifiedBy())
                .createdOn(dto.getCreatedOn())
                .modifiedOn(dto.getModifiedOn())
                .build();
    }
}
