package com.constructflow.timesheet_service.service;

import com.constructflow.timesheet_service.dto.TimesheetDto;
import com.constructflow.timesheet_service.entity.Timesheet;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public interface TimesheetService
{
    List<Timesheet> list(
            @NonNull Long customerId,
            @NonNull Map<String, String> queries);

    Timesheet create(
            @NonNull Long customerId,
            @NonNull Timesheet entity);

    Timesheet update(
            @NonNull Long customerId,
            @NonNull Long timesheetId,
            @NonNull Timesheet entity);

    void delete(
            @NonNull Long customerId,
            @NonNull Long timesheetId);
}
