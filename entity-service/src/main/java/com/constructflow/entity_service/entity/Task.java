package com.constructflow.entity_service.entity;

import com.constructflow.entity_service.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@SQLRestriction("active = true")
public class Task
        extends BaseEntity
{
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, updatable = false)
    private String code;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private Status taskStatus = Status.NOT_STARTED;

    @ManyToOne
    @JoinColumn(name = "project_id", updatable = false)
    private Project project;
}
