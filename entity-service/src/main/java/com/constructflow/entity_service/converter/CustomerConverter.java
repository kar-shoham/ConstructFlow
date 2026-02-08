package com.constructflow.entity_service.converter;

import com.constructflow.entity_service.dto.CustomerDto;
import com.constructflow.entity_service.entity.Customer;
import com.constructflow.entity_service.enums.ObjectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class CustomerConverter
        extends Converter<Customer, CustomerDto>
{
    public static CustomerConverter instance = new CustomerConverter();


    public CustomerConverter()
    {
        super(CustomerConverter::fromEntity, CustomerConverter::fromDto);
    }

    private static CustomerDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @NonNull Customer customer)
    {
        return CustomerDto.builder()
                .id(customer.getId())
                .code(customer.getCode())
                .name(customer.getName())
                .createdBy(customer.getCreatedBy())
                .modifiedBy(customer.getModifiedBy())
                .createdOn(customer.getCreatedOn())
                .modifiedOn(customer.getModifiedOn())
                .build();
    }

    private static Customer fromDto(
            @Nullable Map<ObjectType, Object> references,
            @NonNull CustomerDto customerDto)
    {
        return Customer.builder()
                .id(customerDto.getId())
                .code(customerDto.getCode())
                .name(customerDto.getName())
                .createdBy(customerDto.getCreatedBy())
                .modifiedBy(customerDto.getModifiedBy())
                .createdOn(customerDto.getCreatedOn())
                .modifiedOn(customerDto.getModifiedOn())
                .build();
    }
}
