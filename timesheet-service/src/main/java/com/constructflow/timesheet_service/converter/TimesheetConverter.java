package com.constructflow.timesheet_service.converter;

import com.constructflow.timesheet_service.dto.TimesheetDto;
import com.constructflow.timesheet_service.entity.Timesheet;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class TimesheetConverter
        extends Converter<Timesheet, TimesheetDto>
{
    public static TimesheetConverter instance = new TimesheetConverter();


    public TimesheetConverter()
    {
        super(TimesheetConverter::fromEntity, TimesheetConverter::fromDto);
    }

    private static TimesheetDto fromEntity(
            @Nullable Map<ObjectType, Object> references,
            @NonNull Timesheet entity)
    {
        return TimesheetDto.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .employeeId(entity.getEmployeeId())
                .projectId(entity.getProjectId())
                .taskId(entity.getTaskId())
                .costCodeId(entity.getCostCodeId())
                .dateWorked(entity.getDateWorked())
                .seconds(entity.getSeconds())
                .startTime(entity.getStartTime())
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdOn(entity.getCreatedOn())
                .modifiedOn(entity.getModifiedOn())
                .build();
    }

    private static Timesheet fromDto(
            @Nullable Map<ObjectType, Object> references,
            @NonNull TimesheetDto dto)
    {
        return Timesheet.builder()
                .id(dto.getId())
                .customerId(dto.getCustomerId())
                .employeeId(dto.getEmployeeId())
                .projectId(dto.getProjectId())
                .taskId(dto.getTaskId())
                .costCodeId(dto.getCostCodeId())
                .dateWorked(dto.getDateWorked())
                .seconds(dto.getSeconds())
                .startTime(dto.getStartTime())
                .createdBy(dto.getCreatedBy())
                .modifiedBy(dto.getModifiedBy())
                .createdOn(dto.getCreatedOn())
                .modifiedOn(dto.getModifiedOn())
                .build();
    }
}
