package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.converter.CostCodeConverter;
import com.constructflow.entity_service.dto.CostCodeDto;
import com.constructflow.entity_service.entity.CostCode;
import com.constructflow.entity_service.service.CostCodeService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers/{customerId}/cost-codes")
public class CostCodeRestController
{
    @Autowired
    private CostCodeService costCodeService;

    @GetMapping
    public ResponseEntity<List<CostCodeDto>> list(@PathVariable @NonNull Long customerId)
    {
        List<CostCode> entities = costCodeService.list(customerId);
        List<CostCodeDto> dtos = entities.stream()
                .map(x -> CostCodeConverter.instance.toDto(null, x))
                .collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{costCodeId}")
    public ResponseEntity<CostCodeDto> get(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long costCodeId)
    {
        CostCode entity = costCodeService.get(customerId, costCodeId);
        return ResponseEntity.ok(CostCodeConverter.instance.toDto(null, entity));
    }

    @PostMapping
    public ResponseEntity<CostCodeDto> create(
            @PathVariable @NonNull Long customerId,
            @Valid @RequestBody @NonNull CostCodeDto requestDto)
    {
        CostCode entity = CostCodeConverter.instance.toEntity(null, requestDto);
        entity = costCodeService.create(customerId, entity);
        return ResponseEntity.ok(CostCodeConverter.instance.toDto(null, entity));
    }

    @PutMapping("/{costCodeId}")
    public ResponseEntity<CostCodeDto> update(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long costCodeId,
            @Valid @RequestBody @NonNull CostCodeDto requestDto)
    {
        CostCode entity = CostCodeConverter.instance.toEntity(null, requestDto);
        entity = costCodeService.update(customerId, costCodeId, entity);
        return ResponseEntity.ok(CostCodeConverter.instance.toDto(null, entity));
    }

    @DeleteMapping("/{costCodeId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long costCodeId)
    {
        costCodeService.delete(customerId, costCodeId);
        return ResponseEntity.ok(true);
    }
}
