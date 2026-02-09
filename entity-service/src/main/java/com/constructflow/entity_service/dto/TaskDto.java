package com.constructflow.entity_service.dto;

import com.constructflow.entity_service.enums.Status;
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
public class TaskDto extends BaseEntityDto {
    @NotBlank(message = "Task Name cannot be empty!")
    private String name;
    @NotBlank(message = "Task Code cannot be empty!")
    private String code;
    private Status taskStatus;
    private Long projectId;
}
