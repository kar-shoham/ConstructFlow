package com.constructflow.entity_service.converter;

import com.constructflow.entity_service.dto.ProjectDto;
import com.constructflow.entity_service.entity.Customer;
import com.constructflow.entity_service.entity.Project;
import com.constructflow.entity_service.enums.ObjectType;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class ProjectConverter
        extends Converter<Project, ProjectDto>
{
    public static ProjectConverter instance = new ProjectConverter();

    ProjectConverter()
    {
        super(ProjectConverter::fromEntity, ProjectConverter::fromDto);
    }

    private static ProjectDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @Nullable Project entity)
    {
        if (Objects.isNull(entity)) {
            return null;
        }

        return ProjectDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .projectStatus(entity.getProjectStatus())
                .customerId(entity.getCustomer().getId())
                .address(AddressConverter.instance.toDto(null, entity.getAddress()))
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdOn(entity.getCreatedOn())
                .modifiedOn(entity.getModifiedOn())
                .build();
    }

    private static Project fromDto(
            @Nullable Map<ObjectType, Object> references,
            @Nullable ProjectDto dto)
    {
        if (Objects.isNull(dto)) {
            return null;
        }

        return Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .projectStatus(dto.getProjectStatus())
                .customer(Customer.builder().id(dto.getCustomerId()).build())
                .address(AddressConverter.instance.toEntity(null, dto.getAddress()))
                .createdBy(dto.getCreatedBy())
                .modifiedBy(dto.getModifiedBy())
                .createdOn(dto.getCreatedOn())
                .modifiedOn(dto.getModifiedOn())
                .build();
    }
}
