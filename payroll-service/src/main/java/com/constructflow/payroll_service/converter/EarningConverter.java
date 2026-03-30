package com.constructflow.payroll_service.converter;

import com.constructflow.payroll_service.dto.EarningDto;
import com.constructflow.payroll_service.entity.Earning;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EarningConverter {

    private final ModelMapper modelMapper = new ModelMapper();

    public EarningDto toDto(Earning entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, EarningDto.class);
    }

    public Earning toEntity(EarningDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Earning.class);
    }
}
