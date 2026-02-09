package com.constructflow.entity_service.converter;

import com.constructflow.entity_service.dto.CostCodeDto;
import com.constructflow.entity_service.entity.CostCode;
import com.constructflow.entity_service.entity.Customer;
import com.constructflow.entity_service.enums.ObjectType;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class CostCodeConverter
        extends Converter<CostCode, CostCodeDto>
{
    public static CostCodeConverter instance = new CostCodeConverter();

    CostCodeConverter()
    {
        super(CostCodeConverter::fromEntity, CostCodeConverter::fromDto);
    }

    private static CostCodeDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @Nullable CostCode entity)
    {
        if (Objects.isNull(entity)) {
            return null;
        }

        return CostCodeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .costCodeStatus(entity.getCostCodeStatus())
                .parentId(Objects.nonNull(entity.getParent()) ? entity.getParent().getId() : null)
                .customerId(entity.getCustomer().getId())
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdOn(entity.getCreatedOn())
                .modifiedOn(entity.getModifiedOn())
                .build();
    }

    private static CostCode fromDto(
            @Nullable Map<ObjectType, Object> references,
            @Nullable CostCodeDto dto)
    {
        if (Objects.isNull(dto)) {
            return null;
        }

        return CostCode.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .costCodeStatus(dto.getCostCodeStatus())
                .parent(Objects.nonNull(dto.getParentId()) ? CostCode.builder().id(dto.getParentId()).build() : null)
                .customer(Customer.builder().id(dto.getCustomerId()).build())
                .createdBy(dto.getCreatedBy())
                .modifiedBy(dto.getModifiedBy())
                .createdOn(dto.getCreatedOn())
                .modifiedOn(dto.getModifiedOn())
                .build();
    }
}
