package com.constructflow.entity_service.dto;

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
public class ProjectBudgetDto extends BaseEntityDto {

    private Long taskId;
    private String taskCode;
    private Long costCodeId;
    private String costCode;
    private Boolean active;
}
