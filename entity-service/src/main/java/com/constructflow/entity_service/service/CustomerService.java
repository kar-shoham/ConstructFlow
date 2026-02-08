package com.constructflow.entity_service.service;

import com.constructflow.entity_service.entity.Customer;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface CustomerService
{
    List<Customer> list();

    Customer get(@NonNull Long id);

    Customer create(@NonNull Customer customer);

    Customer update(
            @NonNull Long id,
            @NonNull Customer customer);

    boolean delete(@NonNull Long id);
}

