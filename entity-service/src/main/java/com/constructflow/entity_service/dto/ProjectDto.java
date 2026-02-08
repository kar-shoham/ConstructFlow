package com.constructflow.entity_service.dto;

import com.constructflow.entity_service.enums.Status;
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
public class ProjectDto extends BaseEntityDto {

    private String name;
    private String code;
    private Status projectStatus;
    private Long customerId;
    private AddressDto address;
    private Set<Long> taskIds;
}
