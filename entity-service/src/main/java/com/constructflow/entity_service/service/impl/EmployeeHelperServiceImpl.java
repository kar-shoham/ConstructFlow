package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.client.CFUserClient;
import com.constructflow.entity_service.converter.EmployeeConverter;
import com.constructflow.entity_service.dto.AuthRequestDto;
import com.constructflow.entity_service.dto.EmployeeDto;
import com.constructflow.entity_service.dto.UserDto;
import com.constructflow.entity_service.entity.Employee;
import com.constructflow.entity_service.enums.ObjectType;
import com.constructflow.entity_service.service.EmployeeHelperService;
import com.constructflow.entity_service.service.EmployeeService;
import com.constructflow.entity_service.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
public class EmployeeHelperServiceImpl
        extends AuthService
        implements EmployeeHelperService
{

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CFUserClient userClient;

    @Autowired
    private AuthUtils authUtils;

    @Override
    public EmployeeDto get(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId)
    {
        Employee entity = employeeService.get(customerId, companyId, employeeId);
        UserDto userDto = userClient.get(entity.getUserId());
        Map<ObjectType, Object> ref = Map.of(ObjectType.Username, userDto.getUsername(), ObjectType.Email, userDto.getEmail());
        return EmployeeConverter.instance.toDto(ref, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmployeeDto create(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull EmployeeDto employeeDto)
    {
        authUtils.validateAccessForCompanyAdmin(customerId, companyId);
        AuthRequestDto authDto = getAuthRequestDto(employeeDto);
        UserDto userDto = userClient.create(authDto);
        Employee entity = EmployeeConverter.instance.toEntity(Map.of(ObjectType.UserId, userDto.getId()), employeeDto);
        entity.setCreatedBy(getLoggedInUserId());
        entity.setModifiedBy(getLoggedInUserId());
        entity = employeeService.create(customerId, companyId, entity);
        return EmployeeConverter.instance.toDto(null, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmployeeDto update(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Long employeeId,
            @NonNull EmployeeDto employeeDto)
    {
        authUtils.validateAccessForCompanyAdmin(customerId, companyId);
        AuthRequestDto authDto = getAuthRequestDto(employeeDto);
        UserDto userDto = userClient.update(employeeDto.getUserId(), authDto);
        Employee entity = EmployeeConverter.instance.toEntity(Map.of(ObjectType.UserId, userDto.getId()), employeeDto);
        entity.setModifiedBy(getLoggedInUserId());
        entity = employeeService.update(customerId, companyId, employeeId, entity);
        return EmployeeConverter.instance.toDto(null, entity);
    }

    private AuthRequestDto getAuthRequestDto(@NonNull EmployeeDto employeeDto)
    {
        return AuthRequestDto.builder()
                .username(employeeDto.getUsername())
                .password(employeeDto.getPassword())
                .email(employeeDto.getEmail())
                .build();
    }
}
