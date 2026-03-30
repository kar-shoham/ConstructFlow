package com.constructflow.payroll_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseEntityDto {
    private Long id;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private Long createdBy;
    private Long modifiedBy;
    private Boolean active;
}
