package com.constructflow.entity_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerDto extends BaseEntityDto {

    @NotBlank(message = "Customer needs a Name!")
    private String name;
    @NotBlank(message = "Customer needs a Code!")
    private String code;
    private Set<Long> companyIds;
}
