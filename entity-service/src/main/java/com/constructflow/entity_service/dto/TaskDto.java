package com.constructflow.entity_service.dto;

import com.constructflow.entity_service.enums.Status;
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
public class TaskDto extends BaseEntityDto {

    private String name;
    private String code;
    private Status taskStatus;
    private Long projectId;
}
