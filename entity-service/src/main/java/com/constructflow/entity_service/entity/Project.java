package com.constructflow.entity_service.entity;


import com.constructflow.entity_service.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Project
        extends BaseEntity
{
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, updatable = false)
    private String code;

    @Column(nullable = false)
    @Builder.Default
    private Status projectStatus = Status.NOT_STARTED;

    @ManyToOne
    @JoinColumn(name = "column_id")
    private Customer customer;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Address address;

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks = new HashSet<>();

}
