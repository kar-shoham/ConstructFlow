package com.constructflow.entity_service.service.impl;

import com.constructflow.entity_service.entity.CostCode;
import com.constructflow.entity_service.enums.Status;
import com.constructflow.entity_service.repository.CostCodeRepository;
import com.constructflow.entity_service.service.CostCodeService;
import com.constructflow.entity_service.utils.AuthUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CostCodeServiceImpl
        extends AuthService
        implements CostCodeService
{
    @Autowired
    private CostCodeRepository repository;

    @Autowired
    private AuthUtils authUtils;

    @Override
    public List<CostCode> list(@NonNull Long customerId)
    {
        return repository.findByCustomerId(customerId);
    }

    @Override
    public CostCode get(
            @NonNull Long customerId,
            @NonNull Long costCodeId)
    {
        return repository.findByCustomerIdAndCostCodeId(customerId, costCodeId)
                .orElseThrow(() -> new EntityNotFoundException("No CostCode found with id: " + costCodeId + " and customerId: " + customerId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CostCode create(
            @NonNull Long customerId,
            @NonNull CostCode costCode)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        if (repository.findByCode(costCode.getCode()).isPresent()) {
            throw new RuntimeException("CostCode with code: " + costCode.getCode() + " already exists!");
        }
        costCode.setId(null);
        if (Objects.nonNull(costCode.getParent())) {
            CostCode parent = get(customerId, costCode.getParent().getId());
            costCode.setParent(parent);
        }
        if (Objects.isNull(costCode.getCostCodeStatus())) {
            costCode.setCostCodeStatus(Status.NOT_STARTED);
        }
        costCode.setCreatedBy(getLoggedInUserId());
        costCode.setModifiedBy(getLoggedInUserId());
        return repository.save(costCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CostCode update(
            @NonNull Long customerId,
            @NonNull Long costCodeId,
            @NonNull CostCode costCode)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        CostCode dbCostCode = get(customerId, costCodeId);
        if (!dbCostCode.getCode().equals(costCode.getCode())) {
            throw new RuntimeException("Cannot change CostCode Code!");
        }
        if (Objects.nonNull(costCode.getParent()) && !costCode.getParent().getId()
                .equals(dbCostCode.getParent().getId())) {
            CostCode parent = get(customerId, costCode.getParent().getId());
            dbCostCode.setParent(parent);
        }
        dbCostCode.setName(costCode.getName());
        if (Objects.nonNull(costCode.getCostCodeStatus())) {
            dbCostCode.setCostCodeStatus(costCode.getCostCodeStatus());
        }
        if (Objects.nonNull(costCode.getParent())) {
            dbCostCode.setParent(costCode.getParent());
        }
        dbCostCode.setModifiedBy(getLoggedInUserId());
        return repository.save(dbCostCode);
    }

    @Override
    public void delete(
            @NonNull Long customerId,
            @NonNull Long costCodeId)
    {
        authUtils.validateAccessForAnyCompanyAdmin(customerId);
        CostCode costCode = get(customerId, costCodeId);
        repository.delete(costCode);
    }
}
