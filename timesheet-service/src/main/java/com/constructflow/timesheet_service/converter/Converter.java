package com.constructflow.timesheet_service.converter;

import org.aspectj.apache.bcel.generic.ObjectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;

// ideally X is the entity and Y is the DTO
public abstract class Converter<X, Y>
{
    private BiFunction<Map<ObjectType, Object>, X, Y> fromEntityToDto;
    private BiFunction<Map<ObjectType, Object>, Y, X> fromDtoToEntity;

    Converter(
            @NonNull BiFunction<Map<ObjectType, Object>, X, Y> fromEntityToDto,
            @NonNull BiFunction<Map<ObjectType, Object>, Y, X> fromDtoToEntity)
    {
        this.fromEntityToDto = fromEntityToDto;
        this.fromDtoToEntity = fromDtoToEntity;
    }

    public Y toDto(
            @Nullable Map<ObjectType, Object> references,
            @NonNull X x)
    {
        return fromEntityToDto.apply(references, x);
    }

    public X toEntity(
            @Nullable Map<ObjectType, Object> references,
            @NonNull Y y)
    {
        return fromDtoToEntity.apply(references, y);
    }
}
