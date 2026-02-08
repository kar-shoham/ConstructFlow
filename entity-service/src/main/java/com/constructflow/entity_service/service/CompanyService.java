package com.constructflow.entity_service.service;

import com.constructflow.entity_service.entity.Company;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface CompanyService
{
    List<Company> listAll();

    List<Company> list(@NonNull Long customerId);

    Company get(
            @NonNull Long customerId,
            @NonNull Long companyId);

    Company create(
            @NonNull Long customerId,
            @NonNull Company company);

    Company update(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Company company);

    void delete(
            @NonNull Long customerId,
            @NonNull Long companyId);
}
