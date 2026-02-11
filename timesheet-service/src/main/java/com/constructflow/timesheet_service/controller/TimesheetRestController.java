package com.constructflow.timesheet_service.controller;

import com.constructflow.timesheet_service.converter.TimesheetConverter;
import com.constructflow.timesheet_service.dto.TimesheetDto;
import com.constructflow.timesheet_service.entity.Timesheet;
import com.constructflow.timesheet_service.service.TimesheetService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers/{customerId}/timesheets")
public class TimesheetRestController
{
    @Autowired
    private TimesheetService timesheetService;

    @GetMapping
    public ResponseEntity<List<TimesheetDto>> list(
            @PathVariable @NonNull Long customerId,
            @RequestParam @NonNull Map<String, String> queries)
    {
        List<Timesheet> entities = timesheetService.list(customerId, queries);
        return ResponseEntity.ok(entities.stream()
                .map(x -> TimesheetConverter.instance.toDto(null, x))
                .collect(Collectors.toUnmodifiableList()));
    }

    @PostMapping
    public ResponseEntity<TimesheetDto> create(
            @PathVariable @NonNull Long customerId,
            @RequestBody @NonNull TimesheetDto requestDto)
    {
        Timesheet entity = TimesheetConverter.instance.toEntity(null, requestDto);
        entity = timesheetService.create(customerId, entity);
        return ResponseEntity.ok(TimesheetConverter.instance.toDto(null, entity));
    }

    @PutMapping("/{timesheetId}")
    public ResponseEntity<TimesheetDto> update(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long timesheetId,
            @RequestBody @NonNull TimesheetDto requestDto)
    {
        Timesheet entity = TimesheetConverter.instance.toEntity(null, requestDto);
        entity = timesheetService.update(customerId, timesheetId, entity);
        return ResponseEntity.ok(TimesheetConverter.instance.toDto(null, entity));
    }

    @DeleteMapping("/{timesheetId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long timesheetId)
    {
        timesheetService.delete(customerId, timesheetId);
        return ResponseEntity.ok(true);
    }
}
