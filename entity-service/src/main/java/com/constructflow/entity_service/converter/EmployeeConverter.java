package com.constructflow.entity_service.converter;

import com.constructflow.entity_service.dto.EmployeeDto;
import com.constructflow.entity_service.entity.Company;
import com.constructflow.entity_service.entity.Employee;
import com.constructflow.entity_service.enums.ObjectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class EmployeeConverter
        extends Converter<Employee, EmployeeDto>
{
    public static EmployeeConverter instance = new EmployeeConverter();

    EmployeeConverter()
    {
        super(EmployeeConverter::fromEntity, EmployeeConverter::fromDto);
    }

    private static EmployeeDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @Nullable Employee entity)
    {
        if(Objects.isNull(entity)) return null;

        return EmployeeDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .payRate(entity.getPayRate())
                .employeeType(entity.getEmployeeType())
                .employeeRole(entity.getEmployeeRole())
                .companyId(entity.getCompany().getId())
                .address(AddressConverter.instance.toDto(null, entity.getAddress()))
                .active(entity.getActive())
                .userId(entity.getUserId())
                .build();
    }

    private static Employee fromDto(
            @NonNull Map<ObjectType, Object> references,
            @Nullable EmployeeDto dto)
    {
        if(Objects.isNull(dto)) return null;

        if (!references.containsKey(ObjectType.UserId)) {
            throw new RuntimeException("Missing UserId for Employee!");
        }

        Long userId = (Long) references.get(ObjectType.UserId);

        return Employee.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .payRate(dto.getPayRate())
                .employeeType(dto.getEmployeeType())
                .employeeRole(dto.getEmployeeRole())
                .company(Company.builder().id(dto.getCompanyId()).build())
                .address(AddressConverter.instance.toEntity(null, dto.getAddress()))
                .active(dto.getActive())
                .userId(userId)
                .build();
    }
}
