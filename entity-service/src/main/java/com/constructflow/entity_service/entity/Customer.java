package com.constructflow.entity_service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
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
public class Customer
        extends BaseEntity
{
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, updatable = false)
    private String code;

    @OneToMany(mappedBy = "customer", cascade = {CascadeType.REMOVE})
    Set<Company> companies = new HashSet<>();
}
