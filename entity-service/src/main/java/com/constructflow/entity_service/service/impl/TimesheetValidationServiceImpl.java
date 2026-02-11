package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.dto.TimesheetValidationDto;
import com.constructflow.entity_service.dto.TimesheetValidationResponseDto;
import com.constructflow.entity_service.exception.TimesheetValidationException;
import com.constructflow.entity_service.repository.EmployeeRepository;
import com.constructflow.entity_service.repository.ProjectBudgetRepository;
import com.constructflow.entity_service.repository.TaskRepository;
import com.constructflow.entity_service.service.TimesheetValidationService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class TimesheetValidationServiceImpl
        implements TimesheetValidationService
{
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectBudgetRepository projectBudgetRepository;

    @Override
    @Transactional(readOnly = true)
    public TimesheetValidationResponseDto validate(@NonNull TimesheetValidationDto validationDto)
    {
        if (Objects.isNull(validationDto.getCustomerId())) {
            throw new TimesheetValidationException("Missing Customer Id!");
        }
        if (Objects.isNull(validationDto.getEmployeeId())) {
            throw new TimesheetValidationException("Missing Employee Id!");
        }
        if (Objects.isNull(validationDto.getProjectId())) {
            throw new TimesheetValidationException("Missing Project Id!");
        }
        if (!employeeRepository.doesEmployeeBelongToCustomerId(
                validationDto.getCustomerId(), validationDto.getEmployeeId())) {
            throw new TimesheetValidationException("Invalid Employee!");
        }
        if (!taskRepository.isValidCustomerProjectTask(
                validationDto.getCustomerId(), validationDto.getProjectId(), validationDto.getTaskId())) {
            throw new TimesheetValidationException("Invalid Customer Project Task Combination!");
        }
        if (Objects.nonNull(validationDto.getTaskId()) && !projectBudgetRepository.isValidBudget(
                validationDto.getTaskId(), validationDto.getCostCodeId())) {
            throw new TimesheetValidationException("Invalid Task CostCode Combination!");
        }
        return TimesheetValidationResponseDto.builder()
                .valid(true)
                .message("Valid Ids, can create Timesheet!")
                .build();
    }
}
