package com.constructflow.timesheet_service.service.impl;

import com.constructflow.timesheet_service.client.TimesheetValidationClient;
import com.constructflow.timesheet_service.dto.TimesheetValidationDto;
import com.constructflow.timesheet_service.dto.TimesheetValidationResponseDto;
import com.constructflow.timesheet_service.entity.Timesheet;
import com.constructflow.timesheet_service.repository.TimesheetRepository;
import com.constructflow.timesheet_service.service.TimesheetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class TimesheetServiceImpl
        extends AuthService
        implements TimesheetService
{
    @Autowired
    private TimesheetRepository repository;

    @Autowired
    private TimesheetValidationClient validationClient;

    private Timesheet get(
            @NonNull Long customerId,
            @NonNull Long timesheetId)
    {
        return repository.getTimesheetByCustomerIdAndId(customerId, timesheetId)
                .orElseThrow(() ->
                        new EntityNotFoundException("No timesheet with id: " + timesheetId + " found!"));
    }

    @Override
    public List<Timesheet> list(
            @NonNull Long customerId,
            @NonNull Map<String, String> queries)
    {
        Long employeeId = Objects.nonNull(queries.get("employee-id")) ? Long.valueOf(queries.get("employee-id")) : null;
        Date startDate = Objects.nonNull(queries.get("start-date")) ? Date.valueOf(queries.get("start-date")) : null;
        Date endDate = Objects.nonNull(queries.get("end-date")) ? Date.valueOf(queries.get("end-date")) : null;
        return repository.getTimesheetsList(customerId, employeeId, startDate, endDate);
    }

    @Override
    public Timesheet create(
            @NonNull Long customerId,
            @NonNull Timesheet entity)
    {
        TimesheetValidationResponseDto validationResponse = validationClient.validateData(
                TimesheetValidationDto.builder()
                        .customerId(customerId)
                        .employeeId(entity.getEmployeeId())
                        .projectId(entity.getProjectId())
                        .taskId(entity.getTaskId())
                        .costCodeId(entity.getCostCodeId())
                        .build());

        if (!validationResponse.isValid()) {
            throw new RuntimeException(validationResponse.getMessage());
        }
        entity.setId(null);
        entity.setCustomerId(customerId);
        entity.setCreatedBy(getLoggedInUserId());
        entity.setModifiedBy(getLoggedInUserId());
        return repository.save(entity);
    }

    @Override
    public Timesheet update(
            @NonNull Long customerId,
            @NonNull Long timesheetId,
            @NonNull Timesheet entity)
    {
        Timesheet dbTimesheet = get(customerId, timesheetId);

        TimesheetValidationResponseDto validationResponse = validationClient.validateData(
                TimesheetValidationDto.builder()
                        .customerId(customerId)
                        .employeeId(entity.getEmployeeId())
                        .projectId(entity.getProjectId())
                        .taskId(entity.getTaskId())
                        .costCodeId(entity.getCostCodeId())
                        .build());

        if (!validationResponse.isValid()) {
            throw new RuntimeException(validationResponse.getMessage());
        }
        dbTimesheet.setProjectId(entity.getProjectId());
        dbTimesheet.setTaskId(entity.getTaskId());
        dbTimesheet.setCostCodeId(entity.getCostCodeId());
        dbTimesheet.setSeconds(entity.getSeconds());
        dbTimesheet.setModifiedBy(getLoggedInUserId());
        dbTimesheet.setStartTime(entity.getStartTime());
        return repository.save(dbTimesheet);
    }

    @Override
    public void delete(
            @NonNull Long customerId,
            @NonNull Long timesheetId)
    {
        Timesheet dbTimesheet = get(customerId, timesheetId);
        repository.delete(dbTimesheet);
    }
}
