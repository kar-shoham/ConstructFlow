package com.constructflow.entity_service.controller;

import com.constructflow.entity_service.converter.CustomerConverter;
import com.constructflow.entity_service.dto.CustomerDto;
import com.constructflow.entity_service.entity.Customer;
import com.constructflow.entity_service.service.CustomerService;
import org.jspecify.annotations.NonNull;
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
@RequestMapping("/customers")
public class CustomerRestController
{
    @Autowired
    private CustomerService customerService;

    // ADMIN
    @GetMapping
    public ResponseEntity<List<CustomerDto>> list()
    {
        List<Customer> entites = customerService.list();
        return ResponseEntity.ok(entites.stream().map(entity -> CustomerConverter.instance.toDto(null, entity))
                .collect(Collectors.toUnmodifiableList()));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> get(@PathVariable @NonNull Long customerId)
    {
        Customer entity = customerService.get(customerId);
        return ResponseEntity.ok(CustomerConverter.instance.toDto(null, entity));
    }

    // ADMIN
    @PostMapping
    public ResponseEntity<CustomerDto> create(@RequestBody @NonNull CustomerDto requestDto)
    {
        Customer customer = CustomerConverter.instance.toEntity(null, requestDto);
        customer = customerService.create(customer);
        return ResponseEntity.ok(CustomerConverter.instance.toDto(null, customer));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> update(
            @PathVariable @NonNull Long customerId,
            @RequestBody @NonNull CustomerDto requestDto)
    {
        Customer customer = CustomerConverter.instance.toEntity(null, requestDto);
        customer = customerService.update(customerId, customer);
        return ResponseEntity.ok(CustomerConverter.instance.toDto(null, customer));
    }

    // ADMIN
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Boolean> delete(@PathVariable @NonNull Long customerId)
    {
        customerService.delete(customerId);
        return ResponseEntity.ok(true);
    }
}
