package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.entity.Company;
import com.constructflow.entity_service.entity.Customer;
import com.constructflow.entity_service.exception.CustomerNotFoundException;
import com.constructflow.entity_service.repository.CompanyRepository;
import com.constructflow.entity_service.repository.CustomerRepository;
import com.constructflow.entity_service.service.CompanyService;
import com.constructflow.entity_service.utils.AuthUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CompanyServiceImpl
        extends AuthService
        implements CompanyService
{
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private AuthUtils authUtils;

    @Override
    public List<Company> listAll()
    {
        onlyAdmin();
        return repository.findAll();
    }

    @Override
    public List<Company> list(@NonNull Long customerId)
    {
        authUtils.validateAccessForCustomerAdmin(customerId);
        return repository.getCompaniesByCustomerId(customerId);
    }

    @Override
    public Company get(
            @NonNull Long customerId,
            @NonNull Long companyId)
    {
        authUtils.validateAccessForCompanyAdmin(customerId, companyId);
        return repository.getCompanyByCustomerIdAndCompanyId(
                customerId, companyId).orElseThrow(() -> new EntityNotFoundException("Company with id: " + companyId + " and Customer id: " + customerId + " not found!"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Company create(
            @NonNull Long customerId,
            @NonNull Company company)
    {
        authUtils.validateAccessForCustomerAdmin(customerId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
        if (Objects.nonNull(company.getCustomer()) && !company.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Customer Id Mismatch error!");
        }
        company.setCustomer(customer);
        if (repository.getCompanyByCode(company.getCode()).isPresent()) {
            throw new EntityExistsException("Company with code: " + company.getCode() + " already exists! ");
        }

        company.setId(null);
        company.setCreatedBy(getLoggedInUserId());
        company.setModifiedBy(getLoggedInUserId());

        return repository.save(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Company update(
            @NonNull Long customerId,
            @NonNull Long companyId,
            @NonNull Company company)
    {
        authUtils.validateAccessForCompanyAdmin(customerId, companyId);
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (Objects.nonNull(company.getCustomer()) && !company.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Customer Id Mismatch error!");
        }

        Company dbCompany = repository.getCompanyByCustomerIdAndCompanyId(customerId, companyId).orElseThrow(() ->
                new EntityNotFoundException("No Company Exists with CustomerId: " + customerId + ", CompanyId: " + companyId)
        );

        if (!dbCompany.getCode().equals(company.getCode())) {
            throw new RuntimeException("Cannot change Company Code!");
        }

        dbCompany.setName(company.getName());
        dbCompany.setAddress(company.getAddress());
        dbCompany.setModifiedBy(getLoggedInUserId());

        return repository.save(dbCompany);
    }

    @Override
    public void delete(
            @NonNull Long customerId,
            @NonNull Long companyId)
    {
        authUtils.validateAccessForCustomerAdmin(customerId);
        Company company = repository.getCompanyByCustomerIdAndCompanyId(customerId, companyId).orElseThrow(() ->
                new EntityNotFoundException("No Company Exists with CustomerId: " + customerId + ", CompanyId: " + companyId)
        );
        if (company.getCode().equals(company.getCustomer().getCode())) {
            throw new RuntimeException("Cannot delete default Company for Customer!");
        }
        company.setActive(false);
        company.setModifiedBy(getLoggedInUserId());
        repository.save(company);
    }
}
