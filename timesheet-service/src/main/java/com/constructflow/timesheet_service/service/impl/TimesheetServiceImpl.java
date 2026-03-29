package com.constructflow.timesheet_service.service.impl;

import com.constructflow.timesheet_service.client.TimesheetValidationClient;
import com.constructflow.timesheet_service.dto.TimesheetValidationDto;
import com.constructflow.timesheet_service.dto.TimesheetValidationResponseDto;
import com.constructflow.timesheet_service.entity.Timesheet;
import com.constructflow.timesheet_service.entity.TimesheetStatus;
import com.constructflow.timesheet_service.repository.TimesheetRepository;
import com.constructflow.timesheet_service.service.TimesheetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
        Long companyId = Objects.nonNull(queries.get("company-id")) ? Long.valueOf(queries.get("company-id")) : null;
        Date startDate = Objects.nonNull(queries.get("start-date")) ? Date.valueOf(queries.get("start-date")) : null;
        Date endDate = Objects.nonNull(queries.get("end-date")) ? Date.valueOf(queries.get("end-date")) : null;

        List<Timesheet> timesheets = repository.findByCustomerId(customerId);

        if (employeeId != null) {
            timesheets = timesheets.stream()
                    .filter(t -> t.getEmployeeId().equals(employeeId))
                    .toList();
        }
        if (companyId != null) {
            List<Long> companyEmployeeIds = validationClient.getEmployeeIdsByCompany(customerId, companyId).getBody();
            if (Objects.nonNull(companyEmployeeIds)) {
                timesheets = timesheets.stream()
                        .filter(t -> companyEmployeeIds.contains(t.getEmployeeId()))
                        .toList();
            } else {
                timesheets = List.of();
            }
        }
        if (startDate != null) {
            timesheets = timesheets.stream()
                    .filter(t -> !t.getDateWorked().before(startDate))
                    .toList();
        }
        if (endDate != null) {
            timesheets = timesheets.stream()
                    .filter(t -> !t.getDateWorked().after(endDate))
                    .toList();
        }

        return timesheets;
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
                        .build()).getBody();

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

        if (dbTimesheet.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new RuntimeException("Cannot update timesheet with status: " + dbTimesheet.getStatus());
        }

        TimesheetValidationResponseDto validationResponse = validationClient.validateData(
                TimesheetValidationDto.builder()
                        .customerId(customerId)
                        .employeeId(entity.getEmployeeId())
                        .projectId(entity.getProjectId())
                        .taskId(entity.getTaskId())
                        .costCodeId(entity.getCostCodeId())
                        .build()).getBody();

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
        if (dbTimesheet.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new RuntimeException("Cannot delete timesheet with status: " + dbTimesheet.getStatus());
        }
        repository.delete(dbTimesheet);
    }

    @Override
    public Timesheet approve(
            @NonNull Long customerId,
            @NonNull Long timesheetId)
    {
        Timesheet dbTimesheet = get(customerId, timesheetId);
        if (dbTimesheet.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new RuntimeException("Timesheet must be in SUBMITTED status to approve. Current status: " + dbTimesheet.getStatus());
        }
        dbTimesheet.setStatus(TimesheetStatus.APPROVED);
        dbTimesheet.setModifiedBy(getLoggedInUserId());
        Timesheet savedTimesheet = repository.save(dbTimesheet);

        String message = String.format("{\"timesheetId\":%d,\"employeeId\":%d,\"customerId\":%d}",
                timesheetId, dbTimesheet.getEmployeeId(), customerId);
        kafkaTemplate.send("timesheet-approved", message);
        log.info("Published timesheet approval to Kafka: {}", message);

        return savedTimesheet;
    }

    @Override
    public Timesheet reject(
            @NonNull Long customerId,
            @NonNull Long timesheetId)
    {
        Timesheet dbTimesheet = get(customerId, timesheetId);
        if (dbTimesheet.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new RuntimeException("Timesheet must be in SUBMITTED status to reject. Current status: " + dbTimesheet.getStatus());
        }
        dbTimesheet.setStatus(TimesheetStatus.REJECTED);
        dbTimesheet.setModifiedBy(getLoggedInUserId());
        return repository.save(dbTimesheet);
    }

    @Override
    public Timesheet markPaid(
            @NonNull Long customerId,
            @NonNull Long timesheetId)
    {
        Timesheet dbTimesheet = get(customerId, timesheetId);
        if (dbTimesheet.getStatus() != TimesheetStatus.APPROVED) {
            throw new RuntimeException("Timesheet must be in APPROVED status to mark as paid. Current status: " + dbTimesheet.getStatus());
        }
        dbTimesheet.setStatus(TimesheetStatus.PAID);
        dbTimesheet.setModifiedBy(getLoggedInUserId());
        Timesheet savedTimesheet = repository.save(dbTimesheet);
        log.info("Marked timesheet {} as PAID", timesheetId);
        return savedTimesheet;
    }
}
