package com.constructflow.entity_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CompanyDto
        extends BaseEntityDto
{
    @NotBlank(message = "Company needs a Name!")
    private String name;
    @NotNull(message = "Company needs a Code!")
    private String code;
    private AddressDto address;
    private Long customerId;
}
