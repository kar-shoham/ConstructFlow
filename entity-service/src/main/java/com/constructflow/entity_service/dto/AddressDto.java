package com.constructflow.entity_service.dto;

import jakarta.validation.constraints.NotBlank;
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
public class AddressDto extends BaseEntityDto {

    private String addressLine1;
    private String addressLine2;
    @NotBlank(message = "Address City is Mandatory")
    private String city;
    @NotBlank(message = "Address State is Mandatory")
    private String state;
    private String postalCode;
    private String country;
}
