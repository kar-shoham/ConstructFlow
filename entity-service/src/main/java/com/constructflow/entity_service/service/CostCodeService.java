package com.constructflow.entity_service.service;

import com.constructflow.entity_service.entity.CostCode;
import lombok.NonNull;

import java.util.List;

public interface CostCodeService
{
    List<CostCode> list(@NonNull Long customerId);

    CostCode get(
            @NonNull Long customerId,
            @NonNull Long costCodeId);

    CostCode create(
            @NonNull Long customerId,
            @NonNull CostCode costCode);

    CostCode update(
            @NonNull Long customerId,
            @NonNull Long costCodeId,
            @NonNull CostCode costCode);

    void delete(
            @NonNull Long customerId,
            @NonNull Long costCodeId);
}
