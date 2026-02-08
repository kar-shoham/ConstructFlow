package com.constructflow.entity_service.converter;

import com.constructflow.entity_service.dto.CompanyDto;
import com.constructflow.entity_service.entity.Company;
import com.constructflow.entity_service.entity.Customer;
import com.constructflow.entity_service.enums.ObjectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class CompanyConverter
        extends Converter<Company, CompanyDto>
{
    public static CompanyConverter instance = new CompanyConverter();


    public CompanyConverter()
    {
        super(CompanyConverter::fromEntity, CompanyConverter::fromDto);
    }

    private static CompanyDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @NonNull Company company)
    {
        return CompanyDto.builder()
                .id(company.getId())
                .code(company.getCode())
                .name(company.getName())
                .address(AddressConverter.instance.toDto(null, company.getAddress()))
                .customerId(company.getCustomer().getId())
                .createdBy(company.getCreatedBy())
                .modifiedBy(company.getModifiedBy())
                .createdOn(company.getCreatedOn())
                .modifiedOn(company.getModifiedOn())
                .build();
    }

    private static Company fromDto(
            @Nullable Map<ObjectType, Object> references,
            @NonNull CompanyDto companyDto)
    {
        return Company.builder()
                .id(companyDto.getId())
                .code(companyDto.getCode())
                .name(companyDto.getName())
                .address(AddressConverter.instance.toEntity(null, companyDto.getAddress()))
                .customer(Customer.builder().id(companyDto.getCustomerId()).build())
                .createdBy(companyDto.getCreatedBy())
                .modifiedBy(companyDto.getModifiedBy())
                .createdOn(companyDto.getCreatedOn())
                .modifiedOn(companyDto.getModifiedOn())
                .build();

    }
}