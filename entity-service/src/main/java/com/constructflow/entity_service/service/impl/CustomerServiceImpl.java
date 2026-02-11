package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.entity.Company;
import com.constructflow.entity_service.entity.Customer;
import com.constructflow.entity_service.exception.CustomerNotFoundException;
import com.constructflow.entity_service.repository.CustomerRepository;
import com.constructflow.entity_service.service.CompanyService;
import com.constructflow.entity_service.service.CustomerService;
import com.constructflow.entity_service.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CustomerServiceImpl
        extends AuthService
        implements CustomerService
{
    @Autowired
    private CustomerRepository repository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private AuthUtils authUtils;

    @Override
    public List<Customer> list()
    {
        onlyAdmin();
        return repository.findAll();
    }

    @Override
    public Customer get(@NonNull Long id)
    {
        authUtils.validateAccessForCustomerAdmin(id);
        return repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer create(@NonNull Customer customer)
    {
        onlyAdmin();
        if (repository.findCustomerByCode(customer.getCode()).isPresent()) {
            throw new com.constructflow.entity_service.exception.ResourceAlreadyExistsException("Customer with code: " + customer.getCode() + " already exists");
        }
        customer.setId(null);
        customer.setCreatedBy(getLoggedInUserId());
        customer.setModifiedBy(getLoggedInUserId());
        customer = repository.save(customer);
        Company company = Company.builder()
                .name(customer.getName())
                .code(customer.getCode())
                .customer(customer)
                .build();

        companyService.create(customer.getId(), company);

        return customer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer update(
            @NonNull Long id,
            @NonNull Customer customer)
    {
        authUtils.validateAccessForCustomerAdmin(id);
        Customer dbCustomer = get(id);
        if (!dbCustomer.getCode().equals(customer.getCode())) {
            throw new com.constructflow.entity_service.exception.InvalidOperationException("Customer Code cannot be changed!");
        }
        dbCustomer.setModifiedBy(getLoggedInUserId());
        dbCustomer.setName(customer.getName());
        return repository.save(dbCustomer);
    }

    @Override
    public boolean delete(@NonNull Long id)
    {
        onlyAdmin();
        Customer customer = get(id);
        customer.setActive(false);
        customer.setModifiedBy(getLoggedInUserId());
        repository.save(customer);
        return true;
    }
}
