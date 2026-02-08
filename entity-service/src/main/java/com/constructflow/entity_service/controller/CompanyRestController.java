package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.converter.CompanyConverter;
import com.constructflow.entity_service.dto.CompanyDto;
import com.constructflow.entity_service.entity.Company;
import com.constructflow.entity_service.service.CompanyService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CompanyRestController
{
    @Autowired
    private CompanyService companyService;

    //    ADMIN
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDto>> listAll()
    {
        List<Company> entities = companyService.listAll();
        return ResponseEntity.ok(entities.stream()
                .map(entity -> CompanyConverter.instance.toDto(null, entity))
                .collect(Collectors.toUnmodifiableList()));
    }

    // ADMIN or CUSTOMER ADMIN OR OWNER smt smt
    @GetMapping("/customers/{customerId}/companies")
    public ResponseEntity<List<CompanyDto>> list(@PathVariable @NonNull Long customerId)
    {
        List<Company> entities = companyService.list(customerId);
        return ResponseEntity.ok(entities.stream()
                .map(entity -> CompanyConverter.instance.toDto(null, entity))
                .collect(Collectors.toUnmodifiableList()));
    }

    @GetMapping("/customers/{customerId}/companies/{companyId}")
    public ResponseEntity<CompanyDto> get(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long companyId)
    {
        Company entity = companyService.get(customerId, companyId);
        return ResponseEntity.ok(CompanyConverter.instance.toDto(null, entity));
    }

    @PostMapping("/customers/{customerId}/companies")
    public ResponseEntity<CompanyDto> create(
            @PathVariable @NonNull Long customerId,
            @Valid @RequestBody @NonNull CompanyDto requestDto)
    {
        Company entity = CompanyConverter.instance.toEntity(null, requestDto);
        entity = companyService.create(customerId, entity);
        return ResponseEntity.ok(CompanyConverter.instance.toDto(null, entity));
    }

    @PutMapping("/customers/{customerId}/companies/{companyId}")
    public ResponseEntity<CompanyDto> update(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long companyId,
            @Valid @RequestBody @NonNull CompanyDto requestDto)
    {
        Company entity = CompanyConverter.instance.toEntity(null, requestDto);
        entity = companyService.update(customerId, companyId, entity);
        return ResponseEntity.ok(CompanyConverter.instance.toDto(null, entity));
    }

    @DeleteMapping("/customers/{customerId}/companies/{companyId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @NonNull Long customerId,
            @PathVariable @NonNull Long companyId)
    {
        companyService.delete(customerId, companyId);
        return ResponseEntity.ok(true);
    }
}
