package com.constructflow.entity_service.converter;

import com.constructflow.entity_service.dto.AddressDto;
import com.constructflow.entity_service.entity.Address;
import com.constructflow.entity_service.enums.ObjectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class AddressConverter
        extends Converter<Address, AddressDto>
{
    public static AddressConverter instance = new AddressConverter();

    public AddressConverter()
    {
        super(AddressConverter::fromEntity, AddressConverter::fromDto);
    }

    private static AddressDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @NonNull Address entity)
    {
        if(Objects.isNull(entity)) return null;
        return AddressDto.builder()
                .id(entity.getId())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdOn(entity.getCreatedOn())
                .modifiedOn(entity.getModifiedOn())
                .build();
    }

    private static Address fromDto(
            @Nullable Map<ObjectType, Object> references,
            @NonNull AddressDto dto)
    {
        if(Objects.isNull(dto)) return null;
        return Address.builder()
                .id(dto.getId())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .postalCode(dto.getPostalCode())
                .createdBy(dto.getCreatedBy())
                .modifiedBy(dto.getModifiedBy())
                .createdOn(dto.getCreatedOn())
                .modifiedOn(dto.getModifiedOn())
                .build();
    }
}



